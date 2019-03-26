package ATMSS.ATMSS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;


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
        cardReaderMBox.send(msg);
        keypadMBox.send(msg);
        touchDisplayMBox.send(msg);
        cashDispenserMBox.send(msg);
        cashDepositMBox.send(msg);
        advicePrinterMBox.send(msg);
        buzzerMBox.send(msg);
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
