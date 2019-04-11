package ATMSS.CashDispenserHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


/**
 * Represents the advice printer handler.
 */
//======================================================================
// CashDispenserHandler
public class CashDispenserHandler extends AppThread {
    /**
     * Construct a new instance of cash dispenser handler.
     * @param id The ID of cash dispenser.
     * @param appKickstarter An appKickstarter object, in this application, pass an ATMSSStarter to it.
     */
    //------------------------------------------------------------
    // CashDispenserHandler
    public CashDispenserHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // CashDispenserHandler


    /**
     * Start the cash dispenser thread.
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

                case CD_CashPrepare:
                    try {
                        int amount=Integer.parseInt(msg.getDetails());
                        if(amount%100!=0)
                        {
                            atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_InvalidAmount, "Not Integer!"));
                            break;
                        }
                        String status=this.handleCashPrepare(amount);
                        if(status.equals("Insufficient total notes"))
                        {
                            atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_Insufficient, null));
                        }else if(status.equals("Insufficient 100 HKD notes"))
                        {
                            atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_Insufficient100HKDNotes, null));
                        }else
                        {
                            atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_CashPrepared,null));
                        }
                    }catch (NumberFormatException nfe)
                    {
                        atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_InvalidAmount, "Not Integer!"));
                    }finally {
                        break;
                    }

                case CD_CashRetain:
                    this.handleCashRetain();
                    break;

                case CD_CashOut:
                    this.handleCashOut();
                    break;

                case CD_Complete:
                    atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_Complete, null));
                    //Remaining number of bank notes is in the details of that message sent by emulator
                    atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_CashAmountLeft, msg.getDetails()));
                    break;

                case CD_CashAmountLeft:
                    atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_CashAmountLeft, this.handlecashAmountQuery()));
                    break;

                case TimesUp:
                    //If the user fails to take away the cash within the time limit after cash is out,
                    //the timer in CashDispenserEmulator will timeout
                    this.handleTimeout();
                    atmss.send(new Msg(this.id, this.mbox, Msg.Type.CD_Timeout, null));
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
     * Handle the event that this cash dispenser is instructed by ATMSS to prepare but not put out cash.
     * @param amount The amount of cash to be prepared.
     * @return nothing meaningful. Overriding method in its subclass will return a string indicating the preparation status.
     */
    //Overriding method in its subclass will return a string indicating the cash preparing status.
    protected String handleCashPrepare(int amount)
    {
        log.info(id+": Prepare Cash, amount is "+amount);
        return null;
    }

    /**
     * Handle the instruction of retaining the prepared but not out cash.
     */
    protected void handleCashRetain()
    {
        log.info(id+": Prepared cash will not be dispensed");
    }

    /**
     * Handle the instruction of putting out the prepared cash.
     */
    protected void handleCashOut()
    {
        log.info(id+": Cash out.");
    }

    /**
     * Handle the situation that the user fails to take away the cash in time limit.
     */
    protected void handleTimeout()
    {
        log.info(id+": Timeout! Out cash retained!");
    }

    /**
     * Handle the event that this cash dispenser is queried by ATMSS about the remaining bank notes of each kind.
     * @return nothing meaningful. Overriding method in its subclass will return a string containing the requested information in a pre-defined format.
     */
    //Method at sub-class will return .
    protected String handlecashAmountQuery()
    {
        log.info(id+": Inquired by ATMSS  about the left amount of each kind of bank notes");
        return null;
    }

} // AdvicePrinterHandler
