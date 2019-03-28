package ATMSS.ATMSS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.Queue;


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


    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
        super(id, appKickstarter);
    } // ATMSS

    public class InputBuffer {
        private String buffer = "";

        public void buff(char input) {
            buffer += input;
        }

        public String pop() {
            String out = buffer;
            buffer = "";
            return out;
        }

        public void deleteLast() {
            if (buffer.length() == 0)
                return;
            buffer = buffer.substring(0, buffer.length() - 1);
        }

        public int getLength() {
            return buffer.length();
        }

    }


    private void redirect(Msg msg) {
        currentRun.forward(msg);
        TransMsg transMsg = currentRun.collect();
        while (transMsg != null) {
            if (msg.getType() == Msg.Type.ACT_Abort) {
                switch (msg.getDetails()){
                    case "MainMenu":
                        // Back to Main Menu
                        break;
                    case "RejectCard":
                        // RejectCard
                        break;
                    case "CheckBalance":
                        changeAct("CheckBalance");
                }
                return;
            }
            MBoxes.get(transMsg.destination).send(transMsg.msg);
            transMsg = currentRun.collect();
        }
    }

    private void changeAct(String className){
        try {
            Class[] args = {MBox.class, String.class};
            Class activity = Class.forName(className);
            currentRun = (Activity) activity.getConstructor(args).newInstance(id,mbox);
            currentRun.forward(new Msg(id, mbox, Msg.Type.ACT_Start, ""));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e){
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

        cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
        keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        cashDispenserMBox = appKickstarter.getThread("").getMBox();
        cashDepositMBox = appKickstarter.getThread("").getMBox();
        advicePrinterMBox = appKickstarter.getThread("").getMBox();
        buzzerMBox = appKickstarter.getThread("").getMBox();

        MBoxes.put("cr", cardReaderMBox);
        MBoxes.put("k", keypadMBox);
        MBoxes.put("td", touchDisplayMBox);
        MBoxes.put("cd", cashDispenserMBox);
        MBoxes.put("cdc", cashDepositMBox);
        MBoxes.put("ap", advicePrinterMBox);
        MBoxes.put("b", buzzerMBox);

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case TD_MouseClicked:
                    log.info("MouseCLicked: " + msg.getDetails());
                    processMouseClicked(msg);
                    break;

                case KP_KeyPressed:
                    log.info("KeyPressed: " + msg.getDetails());
                    processKeyPressed(msg);
                    break;

                case CR_CardInserted:
                    log.info("CardInserted: " + msg.getDetails());
                    break;

                case TimesUp:
                    Timer.setTimer(id, mbox, 60000);
                    log.info("Poll: " + msg.getDetails());
                    sendAllComponents(new Msg(id, mbox, Msg.Type.Poll, ""));
                    break;

                case PollAck:
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case Terminate:
                    quit = true;
                    break;

                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
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
