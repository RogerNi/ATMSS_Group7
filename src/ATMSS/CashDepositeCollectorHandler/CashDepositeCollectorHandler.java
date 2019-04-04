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
                    break;

                case CDC_Complete:
                    atmss.send(new Msg(id, mbox, Msg.Type.CDC_CashIn, "complete"));
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
}
