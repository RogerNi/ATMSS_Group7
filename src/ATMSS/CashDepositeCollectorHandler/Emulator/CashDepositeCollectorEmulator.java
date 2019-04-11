package ATMSS.CashDepositeCollectorHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CashDepositeCollectorHandler.CashDepositeCollectorHandler;

import AppKickstarter.timer.Timer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Represent the hardware component: cash deposit collector emulator.
 */
public class CashDepositeCollectorEmulator extends CashDepositeCollectorHandler {
    /**
     * Reference to the ATMSS starter.
     */
    private ATMSSStarter atmssStarter;
    /**
     * The ID of cash deposit collector.
     */
    private String id;
    /**
     * The stage of GUI.
     */
    private Stage myStage;
    /**
     * Reference to the controller of the GUI for cash deposit collector.
     */
    private CashDepositeCollectorEmulatorController cashDepositeCollectorEmulatorController;
    /**
     * The ID of Timer.
     */
    private int timer_id;

    /**
     * Constructs a new Cash Deposit Collector Emulator instance.
     * @param id The ID of cash deposit collector
     * @param atmssStarter The ATMSS Starter
     */
    public CashDepositeCollectorEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
        this.timer_id = timer_id;
    }

    /**
     * Start the cash deposit collector emulator.
     * @throws Exception
     */
    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "CashDepositeCollectorEmulator.fxml";
        loader.setLocation(CashDepositeCollectorEmulator.class.getResource(fxmlName));
        root = loader.load();
        Platform.runLater(() -> {
            cashDepositeCollectorEmulatorController = (CashDepositeCollectorEmulatorController) loader.getController();
            cashDepositeCollectorEmulatorController.initialize(id, atmssStarter, log, this);
            myStage.initStyle(StageStyle.DECORATED);
            myStage.setScene(new Scene(root, 350, 470));
            myStage.setTitle("Cash Deposite Collector");
            myStage.setResizable(false);
            myStage.setOnCloseRequest((WindowEvent event) -> {
                atmssStarter.stopApp();
                Platform.exit();
            });
            myStage.show();
        });
    }

    /**
     * Handle the Ready instruction from ATMSS.
     * Update status to ready.
     * The user is required to deposit money.
     */
    protected void handleReady() {
        // fixme
        super.handleReady();
        cashDepositeCollectorEmulatorController.appendTextArea("Ready");
        cashDepositeCollectorEmulatorController.setStatus("Ready");
        timer_id = Timer.setTimer(id,mbox,Integer.parseInt(appKickstarter.getProperty("CDC.ReadyTimeLimit")));
    }

    /**
     * Handle two situations of time out.
     * 1. Time out of no money put in.
     * 2. Time out of Removing invalid money in time.
     * @return The fist situation returns 0, while the second returns 1.
     */
    protected int handleTimeOut() {
        // fixme
        super.handleTimeOut();
        if(cashDepositeCollectorEmulatorController.getStatus().compareTo("Obstacle")==0){
            cashDepositeCollectorEmulatorController.appendTextArea("Retain Invalid Money.");
            cashDepositeCollectorEmulatorController.setNum_Invalid();
            cashDepositeCollectorEmulatorController.setInvalidCashField("Clear");
            cashDepositeCollectorEmulatorController.setStatus("Close");
            return 1;
        }else if(cashDepositeCollectorEmulatorController.getStatus().compareTo("Ready")==0){
            cashDepositeCollectorEmulatorController.appendTextArea("Time Out!");
            cashDepositeCollectorEmulatorController.setStatus("Close");
            cashDepositeCollectorEmulatorController.initialAmount();
            return 0;
        }
        return -1;
    }

    /**
     * Handle the Complete function, tell the ATMSS all operations are completed.
     */
    protected void handleComplete() {
        super.handleComplete();
        cashDepositeCollectorEmulatorController.appendTextArea("Close");
        cashDepositeCollectorEmulatorController.setStatus("Close");
    }

    /**
     * Handle Cash In function when the user press enter to deposit money.
     */
    protected void handleCashIn(){
        super.handleCashIn();
        Timer.cancelTimer(id, mbox,timer_id);
    }
}

