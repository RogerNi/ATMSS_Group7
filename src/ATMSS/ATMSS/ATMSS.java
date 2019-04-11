package ATMSS.ATMSS;

import ATMSS.BAMSHandler.BAMSHandler;
import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.logging.Logger;


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
    Properties cfg;

    int [] cashLeft = new int[2];
    boolean AP_OK = true;

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
            log.fine("Receive transMsg with Type: " + transMsg.msg.getType());
            switch (transMsg.msg.getType()) {
                case ACT_AbortNow:
                    activities.clear();
                    log.warning("Activity triggers ACT_AbortNow!");
                case ACT_Abort:
                    Arrays.stream(transMsg.msg.getDetails().split(":", -1)).forEach(x -> activities.add(x));
                    String top_act = "";
                    do {
                        top_act = activities.poll();
                    } while (top_act.equals(""));
                    if (top_act.equals("End")) {
                        touchDisplayMBox.send(new Msg(id, mbox,Msg.Type.TD_UpdateDisplay,"0:TEMP1:Thank you!:F"));
                        cashDispenserMBox.send(new Msg(id,mbox, Msg.Type.CD_CashAmountLeft,""));
                        log.info("Activity triggers End!");
                        currentRun = null;
                        cardNum = cred = "";
                        activities.clear();
                    } else {
                        String [] acts = top_act.split(",");
                        log.info("Change activity to " + acts[0]);
                        changeAct(acts[0],acts[acts.length-1]); // change to the top of the queue
                    }
                    return;
                case BAMS:
                    String reply = "";
                    String[] params = transMsg.msg.getDetails().split(":", -1);
                    log.fine("Receive BAMS request: " + params[0]);
                    try {
                        switch (params[0]) {
                            case "login":
                                reply = bams.login(cardNum, params[1]);
                                break;
                            case "getAcc":
                                log.fine("Send BAMS Request to server: cardNum: "+cardNum+" cred: "+cred);
                                reply = bams.getAccounts(cardNum, cred);
                                break;
                            case "withdraw":
                                reply = String.valueOf(bams.withdraw(cardNum, params[1], cred, params[2]));
                                break;
                            case "deposit":
                                reply = String.valueOf(bams.deposit(cardNum, params[1], cred, params[2]));
                                break;
                            case "enquiry":
                                reply = String.valueOf(bams.enquiry(cardNum, params[1], cred));
                                break;
                            case "transfer":
                                reply = String.valueOf(bams.transfer(cardNum, cred, params[1], params[2], params[3]));
                                break;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        log.severe("BAMS_Error: Connection failed!");
                        touchDisplayMBox.send(new Msg(id,mbox, Msg.Type.TD_UpdateDisplay,"0:TEMP1:ATM lost connection to the server\nPlease try again later\nSorry for any inconvenience:F"));
                        transMsg = new TransMsg(new Msg(id,mbox, Msg.Type.ACT_AbortNow,"Eject:End"),"");
                        continue;
                    }
                    reply = params[0] + ":" + reply;
                    log.fine("Reply from BAMS: " + reply);
                    currentRun.forward(new Msg(id, mbox, Msg.Type.BAMS, reply));
                    break;
                case ACT_CRED:
                    cred = transMsg.msg.getDetails();
                    log.fine("ATMSS: Cred Update to "+cred);
                    break;
                default:
                    log.fine("Redirect Messsage: " + transMsg.msg.getType() + ": " + transMsg.msg.getDetails() + " from current Activity to " + transMsg.destination);
//                    if(transMsg.destination.equals("td"))
//                        break;
                    MBoxes.get(transMsg.destination).send(transMsg.msg);
                    break;
            }
            transMsg = currentRun.collect();
        }
    }


    private void changeAct(String className, String detail) {
        try {
            Class[] args = {MBox.class, String.class, Logger.class};
            Class activity = Class.forName("ATMSS.ATMSS." + className);
            currentRun = (Activity) activity.getConstructor(args).newInstance(mbox, id, log);
            redirect(new Msg(id, mbox, Msg.Type.ACT_Start, detail));
        } catch (ClassNotFoundException | NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }


    //------------------------------------------------------------
    // run
    public void run() {
        cfg = new Properties();
        try {
            FileInputStream in = new FileInputStream("etc/ATM.cfg");
            cfg.load(in);
            log.finer("Properties Read Successfully");
            in.close();
        } catch (Exception e) {
            log.severe("Properties Read Failed");
            e.printStackTrace();
        }
//        log.info(cfg.toString());


        Timer.setTimer(id, mbox, Long.valueOf(cfg.getProperty("Timeout.Poll")));
        log.info(id + ": starting...");

        String urlPrefix = cfg.getProperty("Server.IP");
        bams = new BAMSHandler(urlPrefix, log);

        Hashtable<MBox, Boolean> pollAckTable = new Hashtable<>();

        cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
        keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
        touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        cashDispenserMBox = appKickstarter.getThread("CashDispenserHandler").getMBox();
        cashDepositMBox = appKickstarter.getThread("CashDepositHandler").getMBox();
        advicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
        buzzerMBox = appKickstarter.getThread("BuzzerHandler").getMBox();

        MBoxes = new Hashtable<>();

        MBoxes.put("cr", cardReaderMBox);
        MBoxes.put("k", keypadMBox);
        MBoxes.put("td", touchDisplayMBox);
        MBoxes.put("cd", cashDispenserMBox);
        MBoxes.put("cdc", cashDepositMBox);
        MBoxes.put("ap", advicePrinterMBox);
        MBoxes.put("b", buzzerMBox);

        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "0:TEMP1:ATM\nInsert Card Please:F"));
        buzzerMBox.send(new Msg(id,mbox, Msg.Type.BZ_ShortBuzz,""));
        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case CR_CardInserted:
                    log.info("CardInfo: " + msg.getDetails());
                    cardNum = msg.getDetails();
                    cred = "";
                    // Change to Login
                    changeAct("Login","");
                    break;

                case CR_Info:
                    log.info("CardInfo: " + msg.getDetails());
                    cardNum = msg.getDetails();
                    cred = "";
                    // Change to Login
                    changeAct("Login","");
                    break;

                case CR_MachError:
                    log.warning("Card Reader Fails!");
                    currentRun = null;
                    touchDisplayMBox.send(new Msg(id,mbox, Msg.Type.TD_UpdateDisplay,"0:TEMP1:Sorry! This machine meets severe problem\nAnd is now out of service.\nPlease contact the bank for more information.:F"));
                    touchDisplayMBox.send(new Msg(id,mbox, Msg.Type.TD_UpdateDisplay,"0:TEMP1:Out of Service!:F"));
                    break;

                case TimesUp:
                    Timer.setTimer(id, mbox, Long.valueOf(cfg.getProperty("Timeout.Poll")));
                    log.info("Poll: " + msg.getDetails());
                    pollAckTable.forEach((key, value) -> {
                        if (value == false)
                            log.severe("PollError: " + key.toString() + "does not respond!");
                    });

                    MBoxes.values().forEach(x -> pollAckTable.put(x, false)); // Set all to false
                    sendAllComponents(new Msg(id, mbox, Msg.Type.Poll, ""));
                    break;

                case PollAck:
                    pollAckTable.put(msg.getSenderMBox(), true);
                    log.info("PollAck: " + msg.getDetails());
                    break;

                case Terminate:
                    quit = true;
                    break;

                case KP_KeyPressed:
//                    Buzzer Mbox short buzz
                    buzzerMBox.send(new Msg(id,mbox, Msg.Type.BZ_ShortBuzz,""));
                    if (currentRun != null) {
                        redirect(msg);
//                      log.warning(id + ": unknown message type: [" + msg + "]");
                        log.fine("Redirect current message: " + msg);
                    } else
                        log.info("Keypad: " + "invalid press");
                    break;
                case AP_OutOfPaper:
                case AP_Jam:
                    AP_OK = false;
                    if (currentRun != null) {
                        log.fine("Redirect current message: " + msg.getType());
                        redirect(msg);
                    }
                    break;
                case CD_CashAmountLeft:
                    String [] msgInfo = msg.getDetails().split(",");
                    cashLeft[0] = Integer.valueOf(msgInfo[0]);
                    cashLeft[1] = Integer.valueOf(msgInfo[1]);
                    if(currentRun == null)
                    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "0:TEMP1:ATM\nInsert Card Please"+noCashInfo()+":F")); // Resume to init screen
                default:
                    if (currentRun != null) {
                        log.fine("Redirect current message: " + msg.getType());
                        redirect(msg);
//                      log.warning(id + ": unknown message type: [" + msg + "]");
//                        log.info("Redirect current message: " + msg);
                    }
            }
        }

        // declaring our departure
        appKickstarter.unregThread(this);
        log.info(id + ": terminating...");
    } // run

    // Initializing Function
    private void init() {
        currentRun = null;
        cardNum = cred = "";
        activities.clear();
        touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "0:TEMP1:ATM\nInsert Card Please"+noCashInfo()+":F")); // Resume to init screen
    }

    private String noCashInfo(){
        if(cashLeft[0]==0)
            if(cashLeft[1]==0)
                return "\nThis machine does not provide cash withdraw!" + AP_Info();
            else
                return "\nThis machine does not provide 500 HKD cash!" + AP_Info();
        if(cashLeft[1]==0)
            return "This machine does not provide 100 HKD cash!" + AP_Info();
        return ""+AP_Info();
    }

    private String AP_Info(){
        if(AP_OK)
            return "";
        return "\nThis machine does not provide Advice!";
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
