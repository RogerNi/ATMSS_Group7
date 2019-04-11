package ATMSS.AdvicePrinterHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


/**
 * Represents the advice printer handler.
 */
//======================================================================
// AdvicePrinterHandler
public class AdvicePrinterHandler extends AppThread {
    /**
     * Construct a new instance of advice printer handler.
     * @param id The ID of advice printer
     * @param appKickstarter An appKickstarter object, in this application, pass an ATMSSStarter to it.
     */
    //------------------------------------------------------------
    // AdvicePrinterHandler
    public AdvicePrinterHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // AdvicePrinterHandler


    /**
     * Start the advice printer thread.
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
                case AP_Print:
                    atmss.send(new Msg(id, mbox, Msg.Type.AP_PrintStarted, null));
                    String status=this.handlePrintAdvice(msg.getDetails());
                    if(status.equals("Out of paper"))
                    {
                        atmss.send(new Msg(id, mbox, Msg.Type.AP_OutOfPaper, null));
                    }else if(status.equals("Jammed"))
                    {
                        atmss.send(new Msg(id, mbox, Msg.Type.AP_Jam, null));
                    }else
                    {
                        atmss.send(new Msg(id, mbox, Msg.Type.AP_PrintCompleted, null));
                    }
                    break;

                case AP_AdviceTaken:
                    atmss.send(new Msg(this.id, this.mbox, Msg.Type.AP_AdviceTaken, null));
                    break;


                case TimesUp:
                    this.handleTimeout();
                    atmss.send(new Msg(this.id, this.mbox, Msg.Type.AP_TimesUp, null));
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
     * Handle the event that this advice printer is instructed by ATMSS to print out the advice.
     * @param adviceText The content of the advice to be printed.
     * @return nothing meaningful. Overriding method in its subclass will return a string indicating the printing status.
     */
    protected String handlePrintAdvice(String adviceText)
    {
        log.info(id+": Print advice...");
        return null;
    }

    /**
     * Handle the situation that the user fails to take away the advice in time limit.
     */
    protected void handleTimeout()
    {
        log.info(id+": Timeout");
    }

} // AdvicePrinterHandler
