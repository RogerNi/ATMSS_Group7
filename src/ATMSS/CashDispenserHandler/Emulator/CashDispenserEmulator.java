package ATMSS.CashDispenserHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CashDispenserHandler.CashDispenserHandler;

import AppKickstarter.timer.Timer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// CashDispenserEmulator
public class CashDispenserEmulator extends CashDispenserHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private CashDispenserEmulatorController cashDispenserEmulatorController;

//    private static final int INITIAL_NUM_OF_PAPER_PIECES=3;
    private static final long TIME_LIMIT=10000;
    private static final int INITIAL_NUM_OF_500HKD_NOTES=3;
    private static final int INITIAL_NUM_OF_100HKD_NOTES=10;
    //In case we are provided hardware that cannot retain cash
    private static final boolean ABLE_TO_RETAIN_CASH=false;


    /*private int numOfRemainingPaperPieces;
    private boolean isJammed;
    private String adviceStatus;*/
    private int currentNumOf500HKDNotes;
    private int currentNumOf100HKDNotes;
    private int previousNumOf500HKDNotes;
    private int previousNumOf100HKDNotes;
    private int timerId;

    //------------------------------------------------------------
    // CashDispenserEmulator
    public CashDispenserEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;


        this.timerId=-1;
    } // CashDispenserEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "CashDispenserEmulator.fxml";
        loader.setLocation(CashDispenserEmulator.class.getResource(fxmlName));
        root = loader.load();
        cashDispenserEmulatorController = (CashDispenserEmulatorController) loader.getController();
        cashDispenserEmulatorController.initialize(id, atmssStarter, log, this);
        /*cashDispenserEmulatorController.setRemainingPaperText("Number of remaining paper pieces: "+Integer.toString(INITIAL_NUM_OF_PAPER_PIECES));
        cashDispenserEmulatorController.setAdviceStatusField(this.adviceStatus);*/
        this.currentNumOf500HKDNotes=INITIAL_NUM_OF_500HKD_NOTES;
        this.currentNumOf100HKDNotes=INITIAL_NUM_OF_100HKD_NOTES;
        this.previousNumOf500HKDNotes=INITIAL_NUM_OF_500HKD_NOTES;
        this.previousNumOf100HKDNotes=INITIAL_NUM_OF_100HKD_NOTES;
        this.cashDispenserEmulatorController.setRemaining500HKDNotesField("Remaining number of 500 HKD notes: "+this.currentNumOf500HKDNotes);
        this.cashDispenserEmulatorController.setRemaining100HKDNotesField("Remaining number of 100 HKD notes: "+this.currentNumOf100HKDNotes);
        this.cashDispenserEmulatorController.setCashStatusField("Ready");
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Cash Dispenser");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // CashDispenserEmulator


    /*//------------------------------------------------------------
    // handleCardInsert
    protected void handleCardInsert() {
        // fixme
        super.handleCardInsert();
        cardReaderEmulatorController.appendTextArea("Card Inserted");
        cardReaderEmulatorController.updateCardStatus("Card Inserted");
    } // handleCardInsert


    //------------------------------------------------------------
    // handleCardEject
    protected void handleCardEject() {
        // fixme
        super.handleCardEject();
        cardReaderEmulatorController.appendTextArea("Card Ejected");
        cardReaderEmulatorController.updateCardStatus("Card Ejected");
    } // handleCardEject


    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
        // fixme
        super.handleCardRemove();
        cardReaderEmulatorController.appendTextArea("Card Removed");
        cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
    } // handleCardRemove*/

    //It will return a string indicating the printing status.
    /*protected String handlePrintAdvice(String adviceText)
    {
        super.handlePrintAdvice(adviceText);
        if(this.numOfRemainingPaperPieces<=0)
        {
            return "Out of paper";
        }
        if(this.isJammed)
        {
            return "Jammed";
        }
        this.advicePrinterEmulatorController.setMainText(adviceText);
        this.numOfRemainingPaperPieces--;
        this.adviceStatus="Advice to be taken away";
        this.advicePrinterEmulatorController.setRemainingPaperText("Number of remaining paper pieces: "+Integer.toString(this.numOfRemainingPaperPieces));
        this.advicePrinterEmulatorController.setAdviceStatusField(adviceStatus);
        this.timerId= Timer.setTimer(this.id, this.mbox, TIME_LIMIT);
        return "Fine";
    }

    protected void handleTimeout()
    {
        super.handleTimeout();
        this.advicePrinterEmulatorController.retainAdvice();
        this.adviceStatus="Ready";
        this.advicePrinterEmulatorController.setAdviceStatusField(this.adviceStatus);
    }

    public void letItGetJammed()
    {
        this.adviceStatus="Jammed";
        this.isJammed=true;
        this.advicePrinterEmulatorController.setAdviceStatusField(this.adviceStatus);
    }

    public void takeAwayAdvice()
    {
        //If there is no advice, just return.
        if(!this.adviceStatus.equals("Advice to be taken away"))
        {
            return;
        }
        Timer.cancelTimer(this.id, this.mbox, this.timerId);
        this.adviceStatus="Ready";
        this.advicePrinterEmulatorController.setMainText("");
        this.advicePrinterEmulatorController.setAdviceStatusField(this.adviceStatus);
    }*/

    protected String handleCashPrepare(int amount)
    {
        super.handleCashPrepare(amount);
        //I add the code to detect invalid amount in handler, thus here amount must be integer and divisible by 100
        int n=amount/100;
        if(n>5*this.currentNumOf500HKDNotes+this.currentNumOf100HKDNotes)
        {
            return "Insufficient total notes";
        }
        int numOf500HKDNotes=n/5;
        int numOf100HKDNotes=n%5;
        if(numOf100HKDNotes>this.currentNumOf100HKDNotes)
        {
            //In this case, we cannot pay to user the exact amount the user requested.
            return "Insufficient 100 HKD notes";
        }
        if(numOf500HKDNotes>this.currentNumOf500HKDNotes)
        {
            //Insufficient 500 HKD notes, just use 100 HKD notes to replace some 500 HKD notes.
            //Since we check for sufficient total bank notes, here we must have enough 100 HKD notes to pay.
            //Here, just use up 500 HKD bank notes.
            numOf500HKDNotes=this.currentNumOf500HKDNotes;
            //Remaining we use 100 HKD bank notes
            numOf100HKDNotes=n-5*numOf500HKDNotes;
        }
        this.currentNumOf500HKDNotes-=numOf500HKDNotes;
        this.currentNumOf100HKDNotes-=numOf100HKDNotes;
        this.cashDispenserEmulatorController.setCashStatusField("Cash prepared but not out");
        return "Fine";
    }

    protected void handleCashRetain()
    {
        super.handleCashRetain();
        //Roll back
        this.currentNumOf100HKDNotes=this.previousNumOf100HKDNotes;
        this.currentNumOf500HKDNotes=this.previousNumOf500HKDNotes;
        this.cashDispenserEmulatorController.setCashStatusField("Ready");
    }

    protected void handleCashOut()
    {
        super.handleCashOut();
        this.cashDispenserEmulatorController.setRemaining500HKDNotesField("Remaining number of 500 HKD notes: "+this.currentNumOf500HKDNotes);
        this.cashDispenserEmulatorController.setRemaining100HKDNotesField("Remaining number of 100 HKD notes: "+this.currentNumOf100HKDNotes);
        this.cashDispenserEmulatorController.setCashOutArea("Cash out:\n500 HKD notes: "+(this.previousNumOf500HKDNotes-this.currentNumOf500HKDNotes)+
                "\n100 HKD notes: "+(this.previousNumOf100HKDNotes-this.currentNumOf100HKDNotes));
        this.cashDispenserEmulatorController.setCashStatusField("Cash to be taken away");
        this.timerId=Timer.setTimer(this.id, this.mbox, TIME_LIMIT);
    }

    public void takeAwayCash()
    {
        //Confirm the change of remaining cash
        this.previousNumOf500HKDNotes=this.currentNumOf500HKDNotes;
        this.previousNumOf100HKDNotes=this.currentNumOf100HKDNotes;
        this.cashDispenserEmulatorController.setCashOutArea("");
        this.cashDispenserEmulatorController.setCashStatusField("Ready");
        if(this.timerId!=-1)
        {
            Timer.cancelTimer(this.id, this.mbox, this.timerId);
        }
    }

    public int getCurrentNumOf500HKDNotes() {
        return currentNumOf500HKDNotes;
    }

    public int getCurrentNumOf100HKDNotes() {
        return currentNumOf100HKDNotes;
    }

    protected void handleTimeout()
    {
        super.handleTimeout();
        this.timerId=-1;
        if(this.ABLE_TO_RETAIN_CASH)
        {
            this.cashDispenserEmulatorController.retainOutCash();
            //Roll back
            this.currentNumOf100HKDNotes=this.previousNumOf100HKDNotes;
            this.currentNumOf500HKDNotes=this.previousNumOf500HKDNotes;
            this.cashDispenserEmulatorController.setRemaining500HKDNotesField("Remaining number of 500 HKD notes: "+this.currentNumOf500HKDNotes);
            this.cashDispenserEmulatorController.setRemaining100HKDNotesField("Remaining number of 100 HKD notes: "+this.currentNumOf100HKDNotes);
        }else
        {
            this.cashDispenserEmulatorController.timeoutWithoutRetainingOutCash();
            this.previousNumOf100HKDNotes=this.currentNumOf100HKDNotes;
            this.previousNumOf500HKDNotes=this.currentNumOf500HKDNotes;
            this.cashDispenserEmulatorController.setRemaining500HKDNotesField("Remaining number of 500 HKD notes: "+this.currentNumOf500HKDNotes);
            this.cashDispenserEmulatorController.setRemaining100HKDNotesField("Remaining number of 100 HKD notes: "+this.currentNumOf100HKDNotes);
        }

    }

    protected String handlecashAmountQuery()
    {
        super.handlecashAmountQuery();
        return this.currentNumOf500HKDNotes+","+this.currentNumOf100HKDNotes;
    }
} // CashDispenserEmulator
