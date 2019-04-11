package ATMSS.CashDepositeCollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import AppKickstarter.timer.Timer;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

/**
 * Represents the controller of GUI for cash deposit collector.
 */
public class CashDepositeCollectorEmulatorController {

    /**
     * The ID of cash deposit collector.
     */
    private String id;
    /**
     * In this application, it is an ATMSSStarter.
     */
    private AppKickstarter appKickstarter;
    /**
     * The logger to record the hardware running status
     */
    private Logger log;
    /**
     * The cash deposit collector.
     */
    private CashDepositeCollectorEmulator cashDepositeCollectorEmulator;
    /**
     * The mailbox of cash deposit collector, used for communication with handler.
     */
    private MBox cashDepositeCollectorMBox;
    /**
     * The main text.
     */
    public TextArea CashDepositeCollectorTextArea;
    /**
     * The text of number of invalid money.
     */
    public TextField InvalidCashField;
    /**
     * The text of number of total amount.
     */
    public TextField SumCashField;
    /**
     * The text of status of cash deposit collector.
     */
    public String status;
    /**
     * The number of 100HKD.
     */
    private int num_100;
    /**
     * The number of 500HKD.
     */
    private int num_500 = 0;
    /**
     * The number of 1000HKD.
     */
    private int num_1000 = 0;
    /**
     * The number of invalid money.
     */
    private int num_Invalid = 0;
    /**
     * The number of total amount.
     */
    private int sum = 0;
    /**
     * The ID of timer.
     */
    private int timer_id;

    /**
     * Initial the controller.
     * @param id The ID of cash deposit collector.
     * @param appKickstarter In this application, it is an ATMSSStarter.
     * @param log The logger to record the hardware running status.
     * @param cashDepositeCollectorEmulator The cash deposit collector emulator.
     */
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CashDepositeCollectorEmulator cashDepositeCollectorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.cashDepositeCollectorEmulator = cashDepositeCollectorEmulator;
        this.cashDepositeCollectorMBox = appKickstarter.getThread("CashDepositHandler").getMBox();
        this.status = null;
        this.timer_id = timer_id;
    } // initialize

    /**
     * Handle the button clicking events on cash deposit collector emulator GUI
     * @param actionEvent The event to be handled
     */
    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "+100":
                if (status.equals("Ready")) {
                    num_100 += 1;
                    sum = sum + 100;
                    setSumCashField();
                }
                break;

            case "+500":
                if (status.equals("Ready")) {
                    num_500 += 1;
                    sum = sum + 500;
                    setSumCashField();
                }
                break;
            case "+1000":
                if (status.equals("Ready")) {
                    num_1000 += 1;
                    sum = sum + 1000;
                    setSumCashField();
                }
                break;
            case "+Invalid":
                if (status.equals("Ready")) {
                    num_Invalid += 1;
                }
                break;
            case "-100":
                if (status.equals("Ready")&&num_100>0) {
                    num_100 -= 1;
                    sum = sum - 100;
                    setSumCashField();
                }
                break;
            case "-500":
                if (status.equals("Ready")&&num_500>0) {
                    num_500 -= 1;
                    sum = sum - 500;
                    setSumCashField();
                }
                break;
            case "-1000":
                if (status.equals("Ready")&&num_1000>0) {
                    num_1000 -= 1;
                    sum = sum - 1000;
                    setSumCashField();
                    break;
                }
            case "-Invalid":
                if (status.equals("Ready")&&num_Invalid>0) {
                    num_Invalid -= 1;
                }
                break;
            case "Enter":
                if (status.compareTo("Ready")==0) {
                    cashDepositeCollectorMBox.send(new Msg(id, cashDepositeCollectorMBox, Msg.Type.CDC_CashIn, num_1000 + "," + num_500 + "," + num_100));
                    CashDepositeCollectorTextArea.appendText("Sending " + "100: " + num_100 + ", 500: " + num_500 + ", 1000: " + num_1000 + "\n");
                    InvalidCashField.setText("Invalid: " + num_Invalid);
                    initialAmount();
                    setSumCashField();
                    if(num_Invalid>0){
                        cashDepositeCollectorMBox.send(new Msg(id, cashDepositeCollectorMBox, Msg.Type.CDC_Invalid,""));
                        timer_id = Timer.setTimer(id,cashDepositeCollectorMBox,Integer.parseInt(appKickstarter.getProperty("CDC.InvalidTimeLimit")));
                        setStatus("Obstacle");
                    }else{
                        cashDepositeCollectorMBox.send(new Msg(id, cashDepositeCollectorMBox, Msg.Type.CDC_Complete,""));
                    }
                }
                break;
            case "Remove Invalid":
                if(num_Invalid>0){
                    cashDepositeCollectorMBox.send(new Msg(id, cashDepositeCollectorMBox, Msg.Type.CDC_Complete,""));
                    Timer.cancelTimer(id, cashDepositeCollectorMBox,timer_id);
                    num_Invalid = 0;
                    setInvalidCashField("Clear");
                    updateCollectorStatus("Close");
                }
                break;
            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    /**
     * Update the present status of cash deposit collector.
     * @param status The status of cash deposit collector.
     */
    //------------------------------------------------------------
    // updateCollectorStatus
    public void updateCollectorStatus(String status) {
        this.status = status;
    } // updateCollectorStatus


    /**
     * Output the information of operation and status of cash deposit collector.
     * @param status Output information.
     */
    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> {
            CashDepositeCollectorTextArea.appendText(status + "\n");
        });
    } // appendTextArea

    /**
     * Update the text area of status of cash deposit collector.
     * @param status The status of cash deposit collector.
     */
    //------------------------------------------------------------
    //set status
    public void setInvalidCashField(String status) {
        Platform.runLater(() -> {
            InvalidCashField.setText(status);
        });
    }//set status

    /**
     * Getter of status.
     * @return Current status of cash deposit collector.
     */
    //get status
    public String getStatus(){
        return status;
    }//get status

    /**
     * Setter of status.
     * @param s status of cash deposit collector
     */
    //change state
    public void setStatus(String s){
        this.status = s;
    }//change state

    /**
     * Initialize invalid amount.
     */
    //initialize invalid amount
    public void setNum_Invalid(){ this.num_Invalid = 0;}//initialize invalid amount

    /**
     * Initialize number of all kinds of money.
     */
    //initialize total amount
    public void initialAmount(){
        this.num_100 = 0;
        this.num_500 = 0;
        this.num_1000 = 0;
        this.sum = 0;
    }//initialize total amount

    /**
     * Update total amount.
     */
    //update total amount
    public void setSumCashField(){
        Platform.runLater(() -> {
            SumCashField.setText(""+sum);
        });
    }//update total amount
}
