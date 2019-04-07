package ATMSS.CashDispenserHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


//======================================================================
// CashDispenserHandler
public class CashDispenserHandler extends AppThread {
    //------------------------------------------------------------
    // CashDispenserHandler
    public CashDispenserHandler(String id, AppKickstarter appKickstarter) {
        super(id, appKickstarter);
    } // CashDispenserHandler


    //------------------------------------------------------------
    // run
    public void run() {
        MBox atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit;) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");

            switch (msg.getType()) {
                /*case AP_Print:
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
                    break;*/

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


    /*//------------------------------------------------------------
    // handleCardInsert
    protected void handleCardInsert() {
        log.info(id + ": card inserted");
    } // handleCardInsert


    //------------------------------------------------------------
    // handleCardEject
    protected void handleCardEject() {
        log.info(id + ": card ejected");
    } // handleCardEject


    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
        log.info(id + ": card removed");
    } // handleCardRemove*/

    //Overriding method in its subclass will return a string indicating the printing status.
    /*protected String handlePrintAdvice(String adviceText)
    {
        log.info(id+": Print advice...");
        return null;
    }

    protected void handleTimeout()
    {
        log.info(id+": Timeout");
    }*/

    //Overriding method in its subclass will return a string indicating the cash preparing status.
    protected String handleCashPrepare(int amount)
    {
        log.info(id+": Prepare Cash, amount is "+amount);
        return null;
    }

    protected void handleCashRetain()
    {
        log.info(id+": Prepared cash will not be dispensed");
    }

    protected void handleCashOut()
    {
        log.info(id+": Cash out.");
    }

    protected void handleTimeout()
    {
        log.info(id+": Timeout! Out cash retained!");
    }

    //Method at sub-class will return the requested information.
    protected String handlecashAmountQuery()
    {
        log.info(id+": Inquired by ATMSS  about the left amount of each kind of bank notes");
        return null;
    }

} // AdvicePrinterHandler
