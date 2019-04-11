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

public class CashDepositeCollectorEmulator extends CashDepositeCollectorHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private CashDepositeCollectorEmulatorController cashDepositeCollectorEmulatorController;
    private int timer_id;

    public CashDepositeCollectorEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;
        this.timer_id = timer_id;
    }

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

    protected void handleReady() {
        // fixme
        super.handleReady();
        cashDepositeCollectorEmulatorController.appendTextArea("Ready");
        cashDepositeCollectorEmulatorController.setStatus("Ready");
        timer_id = Timer.setTimer(id,mbox,10000);
    }

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
            return 0;
        }
        return -1;
    }

    protected void handleComplete() {
        super.handleComplete();
        cashDepositeCollectorEmulatorController.appendTextArea("Close");
        cashDepositeCollectorEmulatorController.setStatus("Close");
    }

    protected void handleCashIn(){
        super.handleCashIn();
        Timer.cancelTimer(id, mbox,timer_id);
    }
}

