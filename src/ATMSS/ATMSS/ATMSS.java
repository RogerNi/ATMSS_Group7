package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.lang.reflect.InvocationTargetException;
import java.util.*;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
    private MBox cashDispenserMBox;
    private MBox cashDepositMBox;
    private MBox advicePrinterMBox;
    private MBox buzzerMBox;
    private Hashtable<String, MBox> MBoxes;
    private InputBuffer inBuffer;
    private Activity currentRun;
    BAMSHandler bams;

    // Define States
    private final int INITIALIZE = 0;
    private final int MAINPAGE = 1;
    private final int ERROR = -1;
    private final int INSERTCARD = 10;
    // +0 for waiting cards, +1 for reading cards, +2 for input pin, +3 for checking pin, +4 for lock card, +5 for Reinput Pin, +8 for communicating, +9 for abort operation
    private final int DEPOSIT = 20;
    // +0 for select account, +1 for confirm, +2 for accepted, +8 for communicating, +9 for abort operation
    private final int WITHDRAWAL = 30;
    // +0 for select account, +1 for input amount, +8 for communicating, +9 for abort operation
    private final int TRANSFER = 40;
    //
    private final int CHECKBALANCE = 50;
    //

    String cardNum;
    String cred;
    Queue<String> activities = new ArrayDeque<>();


    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
    } // ATMSS


    private void redirect(Msg msg) {
        currentRun.forward(msg);
        TransMsg transMsg = currentRun.collect();
        while (transMsg != null) {
            if (msg.getType() == Msg.Type.ACT_AbortNow)
                activities.clear();
            if (msg.getType() == Msg.Type.ACT_Abort || msg.getType() == Msg.Type.ACT_AbortNow) {
                Arrays.stream(msg.getDetails().split(";")).forEach(x -> activities.add(x));
                String top_act = activities.poll();
                if (top_act.equals("End")) {
                    currentRun = null;
                    cardNum = cred = "";
                    activities.clear();
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "")); // Resume to init screen
                } else
                    changeAct(top_act); // change to the top of the queue
                return;
            }
            if (msg.getType() == Msg.Type.BAMS) {
                String reply = "";
                String [] params = msg.getDetails().split(":");
                try {
                    switch (params[0]) {
                        case "login":
                            reply = bams.login(cardNum, params[1]);
                        case "getAcc":
                            reply = bams.getAccounts(cardNum,cred);
                        case "withdraw":
                            reply = String.valueOf(bams.withdraw(cardNum, params[1],cred,params[2]));
                        case "deposit":
                            reply = String.valueOf(bams.deposit(cardNum,params[1],cred,params[2]));
                        case "enquiry":
                            reply = String.valueOf(bams.enquiry(cardNum,params[1],cred));
                        case "transfer":
                            reply = String.valueOf(bams.transfer(cardNum,cred,params[1],params[2],params[3]));
                    }
                }catch (Exception e){
                    log.warning("BAMS_Error: Connection failed!");
                }
                reply = params[0] + ":" + reply;
                currentRun.forward(new Msg(id, mbox, Msg.Type.BAMS, reply));
            } else {
                if (msg.getType() == Msg.Type.ACT_CRED)
                    cred = msg.getDetails();
                else
                    MBoxes.get(transMsg.destination).send(transMsg.msg);
            }
            transMsg = currentRun.collect();
        }
    }


    private void changeAct(String className) {
        try {
            Class[] args = {MBox.class, String.class};
            Class activity = Class.forName(className);
            currentRun = (Activity) activity.getConstructor(args).newInstance(id, mbox);
            redirect(new Msg(id, mbox, Msg.Type.ACT_Start, ""));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------
    // State
//    private static class State {
//        private Stage currentStage;
//
//        private static class Stage {
//            enum PrimaryStage {
//                Initialize,
//                MainPage,
//                InsertCard,
//                Deposit,
//                Error;
//            }
//
//            enum InsertCard implements Stage {
//                Waiting,
//                Reading,
//                InputPin,
//                CheckingPin,
//                LockCard,
//                ReinputPin;
//            }
//
//            enum Deposit implements Stage {
//                SelectAccount,
//                Confirm, // Return concluded
//                Accepted;
//
//            }
//
//            enum Withdrawal implements Stage {
//                SelectAccount,
//            }
//
//
//        }
//
//
//    }


    //------------------------------------------------------------
    // run
    public void run() {
        Timer.setTimer(id, mbox, 60000);
        log.info(id + ": starting...");

        String urlPrefix = "http://cslinux0.comp.hkbu.edu.hk/comp4107_18-19_grp07/";
        bams = new BAMSHandler(urlPrefix,log);

        Hashtable<MBox, Boolean> pollAckTable = new Hashtable<>();

        cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
        keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
//        cashDispenserMBox = appKickstarter.getThread("").getMBox();
//        cashDepositMBox = appKickstarter.getThread("").getMBox();
//        advicePrinterMBox = appKickstarter.getThread("").getMBox();
//        buzzerMBox = appKickstarter.getThread("").getMBox();

        MBoxes = new Hashtable<>();

        MBoxes.put("cr", cardReaderMBox);
        MBoxes.put("k", keypadMBox);
        MBoxes.put("td", touchDisplayMBox);
//        MBoxes.put("cd", cashDispenserMBox);
//        MBoxes.put("cdc", cashDepositMBox);
//        MBoxes.put("ap", advicePrinterMBox);
//        MBoxes.put("b", buzzerMBox);


        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case CR_CardInserted:
                    log.info("CardInserted: " + msg.getDetails());
                    cardNum = cred = "";
                    // Display reading card
                    break;

                case CR_Info:
                    log.info("CardInfo: " + msg.getDetails());
                    cardNum = msg.getDetails();
                    cred = "";
                    // Change to Login
                    changeAct("Login");
                    break;

                case CR_MachError:
                    log.warning("Card Reader Fails!");
                    break;

                case TimesUp:
                    Timer.setTimer(id, mbox, 60000);
                    log.info("Poll: " + msg.getDetails());
                    pollAckTable.forEach((key,value)->{
                        if (value==false)
                            log.warning("PollError: " + key.toString() + "does not respond!");
                    });

                    MBoxes.values().forEach(x->pollAckTable.put(x,false)); // Set all to false
                    sendAllComponents(new Msg(id, mbox, Msg.Type.Poll, ""));
                    break;

                case PollAck:
                    pollAckTable.put(msg.getSenderMBox(),true);
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case Terminate:
                    quit = true;
                    break;

                case KP_KeyPressed:


                default:
                    if (currentRun != null) {
                        redirect(msg);
//                      log.warning(id + ": unknown message type: [" + msg + "]");
                        log.info("Redirect current message: " + msg);
                    }
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    // Initializing Function
    private void init() {

    }

    // Assistant Function
    // Send Msg to All components
    private void sendAllComponents(Msg msg) {
        MBoxes.forEach((name, box) ->
                box.send(msg)
        );
    }


    //------------------------------------------------------------
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
        // *** The following is an example only!! ***
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
            cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
        } else if (msg.getDetails().compareToIgnoreCase("1") == 0) {
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
        } else if (msg.getDetails().compareToIgnoreCase("2") == 0) {
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
        } else if (msg.getDetails().compareToIgnoreCase("3") == 0) {
            touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
        }
    } // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
        // *** process mouse click here!!! ***
    } // processMouseClicked
} // CardReaderHandler
