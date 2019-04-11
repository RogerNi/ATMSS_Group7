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

import java.io.FileInputStream;
import java.util.Properties;


/**
 * Represents the hardware component: advice printer
 */
//======================================================================
// AdvicePrinterEmulator
public class AdvicePrinterEmulator extends AdvicePrinterHandler {
    /**
     * Reference to the ATMSS starter.
     */
    private ATMSSStarter atmssStarter;
    /**
     * The ID of advice printer.
     */
    private String id;
    /**
     * The stage of GUI.
     */
    private Stage myStage;
    /**
     * Reference to the controller of the GUI for advice printer.
     */
    private AdvicePrinterEmulatorController advicePrinterEmulatorController;
    /**
     * Configuration information loaded from ATM.cfg
     */
    private Properties cfg;


    /**
     * The initial number of remaining paper pieces. The value will be read from the configuration file. If failed to read,
     * it will be set to 3.
     */
    private int INITIAL_NUM_OF_PAPER_PIECES;
    /**
     * The time limit for user to take away the advice since it's ready, in milliseconds. The value will be read from
     * the configuration file. If failed to read, it will be set to 10000.
     */
    private long TIME_LIMIT;
    /**
     * Whether the advice printer has the ability to retain the advice if time out to protect the user's privacy. True
     * means the advice printer is able to retain the advice if the user does not take away the advice in time limit. False
     * means the advice printer is not able to do that. It can only report timeout to ATMSS. The value is read from the
     * configuration file. If failed to read it, it will be set to false
     */
    private boolean ABLE_TO_RETAIN_ADVICE;


    /**
     * The number of remaining paper pieces in this printer. Each time it print, one piece of paper is consumed.
     */
    private int numOfRemainingPaperPieces;
    /**
     * Whether this advice printer is jammed
     */
    private boolean isJammed;
    /**
     * A string indicating the current status of advice printer.
     */
    private String adviceStatus;
    /**
     * The timer ID of current timer. If no current timer, its value is -1.
     */
    private int timerId;

    /**
     * Constructs a new Advice Printer Emulator instance.
     * @param id The ID of advice printer
     * @param atmssStarter The ATMSS Starter
     */
    //------------------------------------------------------------
    // AdvicePrinterEmulator
    public AdvicePrinterEmulator(String id, ATMSSStarter atmssStarter) {
        super(id, atmssStarter);
        this.atmssStarter = atmssStarter;
        this.id = id;

        cfg = new Properties();
        try {
            FileInputStream in = new FileInputStream("etc/ATM.cfg");
            cfg.load(in);
            log.finer("Properties Read Successfully");
            in.close();
            TIME_LIMIT=Long.valueOf(cfg.getProperty("AP.TimeLimit"));
            INITIAL_NUM_OF_PAPER_PIECES=Integer.valueOf(cfg.getProperty("AP.InitialNumberOfPaperPieces"));
            ABLE_TO_RETAIN_ADVICE=Boolean.valueOf(cfg.getProperty("AP.AbleToRetainAdvice"));
        } catch (Exception e) {
            log.severe("Properties Read Failed");
            e.printStackTrace();
            //Set them to default value if fails to load configuration file
            TIME_LIMIT=10000;
            INITIAL_NUM_OF_PAPER_PIECES=3;
            ABLE_TO_RETAIN_ADVICE=false;
        }

        this.numOfRemainingPaperPieces=INITIAL_NUM_OF_PAPER_PIECES;
        this.isJammed=false;
        this.adviceStatus="Ready";
        this.timerId=-1;
    } // AdvicePrinterEmulator


    /**
     * Start the advice printer emulator.
     * @throws Exception
     */
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


    /**
     * Handle the printing advice instruction from the ATMSS
     * @param adviceText The content of the advice to be printed.
     * @return a string indicating the printing status.
     */
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

    /**
     * Handle the situation that the user fails to take away the advice in time limit. According
     * to whether our printer is able to retain the cash which is already out, it will perform differently.
     */
    protected void handleTimeout()
    {
        super.handleTimeout();
        if(this.ABLE_TO_RETAIN_ADVICE)
        {
            this.advicePrinterEmulatorController.retainAdvice();
        }else
        {
            this.advicePrinterEmulatorController.timeoutWithoutRetainingAdvice();
        }
        this.adviceStatus="Ready";
        this.advicePrinterEmulatorController.setAdviceStatusField(this.adviceStatus);
    }

    /**
     * Let the advice printer get jammed, next time when ATMSS's instruction of printing arrives, advice printer
     * will send to ATMSS message telling this situation.
     */
    public void letItGetJammed()
    {
        this.adviceStatus="Jammed";
        this.isJammed=true;
        this.advicePrinterEmulatorController.setAdviceStatusField(this.adviceStatus);
    }

    /**
     * Handle the user taking away advice event.
     */
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
