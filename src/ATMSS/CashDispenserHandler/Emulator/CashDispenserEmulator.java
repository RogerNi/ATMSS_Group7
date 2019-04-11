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

import java.io.FileInputStream;
import java.util.Properties;


/**
 * Represent the hardware component: cash dispenser emulator.
 */
//======================================================================
// CashDispenserEmulator
public class CashDispenserEmulator extends CashDispenserHandler {
    /**
     * Reference to the ATMSS starter.
     */
    private ATMSSStarter atmssStarter;
    /**
     * The ID of cash dispenser.
     */
    private String id;
    /**
     * The stage of GUI.
     */
    private Stage myStage;
    /**
     * Reference to the controller of the GUI for cash dispenser.
     */
    private CashDispenserEmulatorController cashDispenserEmulatorController;
    /**
     * Configuration information loaded from ATM.cfg
     */
    private Properties cfg;

    private long TIME_LIMIT;
    /**
     * The initial number of remaining 500 HKD notes. The value will be read from the configuration file. If failed to read,
     * it will be set to 3.
     */
    private int INITIAL_NUM_OF_500HKD_NOTES;
    /**
     * The initial number of remaining 100 HKD notes. The value will be read from the configuration file. If failed to read,
     * it will be set to 10.
     */
    private int INITIAL_NUM_OF_100HKD_NOTES;
    /**
     * Whether the cash dispenser has the ability to retain the cash which is already out if time out. True
     * means the cash dispenser is able to retain the cash if the user does not take away the cash in time limit. False
     * means the cash dispenser is not able to do that. It can only report timeout to ATMSS. The value is read from the
     * configuration file. If failed to read it, it will be set to false
     */
    private boolean ABLE_TO_RETAIN_CASH;


    /**
     * Current number of remaining 500 HKD notes
     */
    private int currentNumOf500HKDNotes;
    /**
     * Current number of remaining 100 HKD notes
     */
    private int currentNumOf100HKDNotes;
    /**
     * It is used for roll back if necessary, (e.g. ATMSS instructs it to retain the cash which is prepared but not out)
     */
    private int previousNumOf500HKDNotes;
    /**
     * It is used for roll back if necessary, (e.g. ATMSS instructs it to retain the cash which is prepared but not out)
     */
    private int previousNumOf100HKDNotes;
    /**
     * The timer ID of current timer. If no current timer, its value is -1.
     */
    private int timerId;

    /**
     * Constructs a new Cash Dispenser Emulator instance.
     * @param id The ID of cash dispenser
     * @param atmssStarter The ATMSS Starter
     */
    //------------------------------------------------------------
    // CashDispenserEmulator
    public CashDispenserEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
        this.timerId=-1;
        cfg = new Properties();
        try {
            FileInputStream in = new FileInputStream("etc/ATM.cfg");
            cfg.load(in);
            log.finer("Properties Read Successfully");
            in.close();
            TIME_LIMIT=Long.valueOf(cfg.getProperty("CD.TimeLimit"));
            INITIAL_NUM_OF_500HKD_NOTES=Integer.valueOf(cfg.getProperty("CD.InitialNumberOf500HKDNotes"));
            INITIAL_NUM_OF_100HKD_NOTES=Integer.valueOf(cfg.getProperty("CD.InitialNumberOf100HKDNotes"));
            ABLE_TO_RETAIN_CASH=Boolean.valueOf(cfg.getProperty("CD.AbleToRetainCash"));

        } catch (Exception e) {
            log.severe("Properties Read Failed");
            e.printStackTrace();
            //Set them to default value if fails to load configuration file
            TIME_LIMIT=10000;
            INITIAL_NUM_OF_500HKD_NOTES=3;
            INITIAL_NUM_OF_100HKD_NOTES=10;
            ABLE_TO_RETAIN_CASH=false;
        }
    } // CashDispenserEmulator


    /**
     * Start the cash dispenser emulator.
     * @throws Exception
     */
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


    /**
     * Handle the preparing cash but not out instruction from the ATMSS
     * @param amount The amount of cash to be prepared. It must be integer and divisible by 100.
     * @return "Insufficient total notes" if amount of cash in the dispenser is insufficient to pay that much.
     * "Insufficient 100 HKD notes" if the amount of 100HKD notes is insufficient so that it cannot pay the exact requested amount.
     * "Fine" if successfully prepared.
     */
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

    /**
     * Handle the retaining prepared but not out cash instruction from the ATMSS.
     */
    protected void handleCashRetain()
    {
        super.handleCashRetain();
        //Roll back
        this.currentNumOf100HKDNotes=this.previousNumOf100HKDNotes;
        this.currentNumOf500HKDNotes=this.previousNumOf500HKDNotes;
        this.cashDispenserEmulatorController.setCashStatusField("Ready");
    }

    /**
     * Handle the putting out cash instruction from the ATMSS
     */
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

    /**
     * Handle the taking away cash event of user
     */
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

    /**
     * Get the current number of 500 HKD notes in dispenser.
     * @return current number of 500 HKD notes in dispenser.
     */
    public int getCurrentNumOf500HKDNotes() {
        return currentNumOf500HKDNotes;
    }

    /**
     * Get the current number of 100 HKD notes in dispenser.
     * @return current number of 100 HKD notes in dispenser.
     */
    public int getCurrentNumOf100HKDNotes() {
        return currentNumOf100HKDNotes;
    }

    /**
     * Handle the situation that user fails to take away the cash which is already out. According
     * to whether our dispenser is able to retain the cash which is already out, it will perform differently.
     */
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

    /**
     * Handle the query of remaining bank notes from ATMSS.
     * @return a string containing the number of remaining 500HKD and 100HKD notes in dispenser, in the format of
     * "num_500HKDNotes,num_100HKDNotes", as specified in our protocol of communicating with ATMSS.
     */
    protected String handlecashAmountQuery()
    {
        super.handlecashAmountQuery();
        return this.currentNumOf500HKDNotes+","+this.currentNumOf100HKDNotes;
    }
} // CashDispenserEmulator
