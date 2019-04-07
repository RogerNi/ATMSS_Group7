package ATMSS.CashDepositeCollectorHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import AppKickstarter.timer.Timer;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.control.TextArea;

public class CashDepositeCollectorEmulatorController {

    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CashDepositeCollectorEmulator cashDepositeCollectorEmulator;
    private MBox cashDepositeCollectorMBox;
    public TextArea CashDepositeCollectorTextArea;
    public TextField InvalidCashField;
    public String status;
    private int num_100 = 0;
    private int num_500 = 0;
    private int num_1000 = 0;
    private int num_Invalid = 0;
    private int timer_id;

    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CashDepositeCollectorEmulator cashDepositeCollectorEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.cashDepositeCollectorEmulator = cashDepositeCollectorEmulator;
        this.cashDepositeCollectorMBox = appKickstarter.getThread("CashDepositeCollectorHandler").getMBox();
        this.status = status;
        this.timer_id = timer_id;
    } // initialize

    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "+100":
                if (status.equals("Ready")) {
                    num_100 += 1;
                    break;
                }

            case "+500":
                if (status.equals("Ready")) {
                    num_500 += 1;
                    break;
                }
            case "+1000":
                if (status.equals("Ready")) {
                    num_1000 += 1;
                    break;
                }
            case "+Invalid":
                if (status.equals("Ready")) {
                    num_Invalid += 1;
                    break;
                }
            case "-100":
                if (status.equals("Ready")) {
                    num_100 -= 1;
                    break;
                }
            case "-500":
                if (status.equals("Ready")) {
                    num_500 -= 1;
                    break;
                }
            case "-1000":
                if (status.equals("Ready")) {
                    num_1000 -= 1;
                    break;
                }
            case "-Invalid":
                if (status.equals("Ready")) {
                    num_Invalid -= 1;
                    break;
                }
            case "Enter":
                if (status.compareTo("Ready")==0) {
                    cashDepositeCollectorMBox.send(new Msg(id, cashDepositeCollectorMBox, Msg.Type.CDC_CashIn, num_100 + "," + num_500 + "," + num_1000));
                    CashDepositeCollectorTextArea.appendText("Sending " + "100: " + num_100 + ", 500: " + num_500 + ", 1000: " + num_1000 + "\n");
                    InvalidCashField.appendText("Invalid: " + num_Invalid);
                    num_100 = 0;
                    num_500 = 0;
                    num_1000 = 0;
                    timer_id = Timer.setTimer(id,cashDepositeCollectorMBox,60000);
                    break;
                }
            case "Remove Invalid":
                if(num_Invalid>0){
                    cashDepositeCollectorMBox.send(new Msg(id, cashDepositeCollectorMBox, Msg.Type.CDC_Complete,""));
                    Timer.cancelTimer(id, cashDepositeCollectorMBox,timer_id);
                    num_Invalid = 0;
                    InvalidCashField.appendText("Clear");
                    updateCollectorStatus("Clear");
                }
                break;
            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed

    //------------------------------------------------------------
    // updateCollectorStatus
    public void updateCollectorStatus(String status) {
        this.status = status;
    } // updateCollectorStatus


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        CashDepositeCollectorTextArea.appendText(status+"\n");
    } // appendTextArea

    //change state
    public void setStatus(String s){
        this.status = s;
    }//change state

    public void setNum_Invalid(){ this.num_Invalid = 0;}
}
