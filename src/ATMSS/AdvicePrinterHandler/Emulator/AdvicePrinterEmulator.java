package ATMSS.AdvicePrinterHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.AdvicePrinterHandler.AdvicePrinterHandler;

import AppKickstarter.timer.Timer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;


//======================================================================
// AdvicePrinterEmulator
public class AdvicePrinterEmulator extends AdvicePrinterHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private AdvicePrinterEmulatorController advicePrinterEmulatorController;

    private static final int INITIAL_NUM_OF_PAPER_PIECES=3;
    private static final long TIME_LIMIT=10000;

    private int numOfRemainingPaperPieces;
    private boolean isJammed;
    private String adviceStatus;
    private int timerId;

    //------------------------------------------------------------
    // AdvicePrinterEmulator
    public AdvicePrinterEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;

        this.numOfRemainingPaperPieces=INITIAL_NUM_OF_PAPER_PIECES;
        this.isJammed=false;
        this.adviceStatus="Ready";
        this.timerId=-1;
    } // AdvicePrinterEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
        Parent root;
        myStage = new Stage();
        FXMLLoader loader = new FXMLLoader();
        String fxmlName = "AdvicePrinterEmulator.fxml";
        loader.setLocation(AdvicePrinterEmulator.class.getResource(fxmlName));
        root = loader.load();
        advicePrinterEmulatorController = (AdvicePrinterEmulatorController) loader.getController();
        advicePrinterEmulatorController.initialize(id, atmssStarter, log, this);
        advicePrinterEmulatorController.setRemainingPaperText("Number of remaining paper pieces: "+Integer.toString(INITIAL_NUM_OF_PAPER_PIECES));
        advicePrinterEmulatorController.setAdviceStatusField(this.adviceStatus);
        myStage.initStyle(StageStyle.DECORATED);
        myStage.setScene(new Scene(root, 350, 470));
        myStage.setTitle("Advice Printer");
        myStage.setResizable(false);
        myStage.setOnCloseRequest((WindowEvent event) -> {
            atmssStarter.stopApp();
            Platform.exit();
        });
        myStage.show();
    } // AdvicePrinterEmulator


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
    protected String handlePrintAdvice(String adviceText)
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
    }
} // CardReaderEmulator
