package ATMSS.CashDepositeCollectorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

/**
 * Represents the cash deposit collector handler.
 */
//CashDepositCollectorHandler
public class CashDepositeCollectorHandler extends AppThread {

    /**
     * Construct a new instance of cash deposit collector handler.
     * @param id The ID of cash deposit collector.
     * @param appKickstarter An appKickstarter object, in this application, pass an ATMSSStarter to it.
     */
    // CashDepositeCollectorHandler
    public CashDepositeCollectorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // CashDepositeCollectorHandler

    /**
     * Start the cash deposit collector thread.
     */
    //------------------------------------------------------------
    // run
    public void run() {
        MBox atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                case CDC_Ready:
                    handleReady();
                    break;

                case CDC_CashIn:
                    atmss.send(new Msg(id, mbox, Msg.Type.CDC_CashIn, msg.getDetails()));
                    handleCashIn();
                    break;

                case CDC_Complete:
                    atmss.send(new Msg(id, mbox, Msg.Type.CDC_Complete, "complete"));
                    handleComplete();
                    break;

                case CDC_Invalid:
                    atmss.send(new Msg(id, mbox, Msg.Type.CDC_Invalid, msg.getDetails()));
                    break;

                case TimesUp:
                    int pivot = handleTimeOut();
                    if(pivot==0){
                        atmss.send(new Msg(id, mbox, Msg.Type.CDC_TimeOut, "0"));
                    }else if(pivot==1){
                        atmss.send(new Msg(id, mbox, Msg.Type.CDC_TimeOut, "1"));
                    }
                    break;

                case Poll:
                    atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
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

    /**
     * Handle the Ready instruction from ATMSS.
     * Update status to ready.
     * The user is required to deposit money.
     */
    //handleReady
    protected void handleReady() {
        log.info(id + ": Ready!");
    }//handleReady

    /**
     * Handle two situations of time out.
     * 1. Time out of no money put in.
     * 2. Time out of Removing invalid money in time.
     * @return It is meaningless, there is an overwrite version in emulator.java
     */
    //handleTimeOut
    protected int handleTimeOut() { log.info(id + ": Retain Invalid Money."); return -1;}//handleTimeOut

    /**
     * Handle the Complete function, tell the ATMSS all operations are completed.
     */
    //handleComplete
    protected void handleComplete() {
        log.info(id + ": Complete! ");
    }//handleComplete

    /**
     * Handle Cash In function when the user press enter to deposit money.
     */
    //handleCashIn
    protected void handleCashIn() {}//handleCashIn
}//CashDepositCollectorHandler
