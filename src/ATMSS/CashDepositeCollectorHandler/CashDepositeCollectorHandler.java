package ATMSS.CashDepositeCollectorHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

public class CashDepositeCollectorHandler extends AppThread {

    // CashDepositeCollectorHandler
    public CashDepositeCollectorHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // CashDepositeCollectorHandler

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

    protected void handleReady() {
        log.info(id + ": Ready!");
    }

    protected int handleTimeOut() { log.info(id + ": Retain Invalid Money."); return -1;}

    protected void handleComplete() {
        log.info(id + ": Complete! ");
    }

    protected void handleCashIn() {}
}
