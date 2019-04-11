package ATMSS.AdvicePrinterHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;

import javax.swing.*;


/**
 * Represents the controller of GUI for advice printer
 */
//======================================================================
// AdvicePrinterEmulatorController
public class AdvicePrinterEmulatorController {
    /**
     * The ID of advice printer.
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
     * The advice printer emulator.
     */
    private AdvicePrinterEmulator advicePrinterEmulator;
    /**
     * The mailbox of advice printer, used for communication with handler.
     */
    private MBox advicePrinterMBox;


    /**
     * The big text area in the middle. it is dedicated to display the content of advice.
     */
    @FXML TextArea advicePrinterTextArea;
    /**
     * The text field at the bottom. It is to display the current status of advcie printer.
     */
    @FXML TextField adviceStatusField;
    /**
     * The text field at the top, used for showing the number of remaining paper pieces for easier testing. In real case,
     * advice printer cannot display that.
     */
    @FXML TextField remainingPaperField;


    /**
     * Initial the controller.
     * @param id The ID of advice printer.
     * @param appKickstarter In this application, it is an ATMSSStarter.
     * @param log The logger to record the hardware running status.
     * @param advicePrinterEmulator The advice printer emulator.
     */
    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, AdvicePrinterEmulator advicePrinterEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.advicePrinterEmulator = advicePrinterEmulator;
        this.advicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
    } // initialize


    /**
     * Handle the button clicking events on advice printer emulator GUI
     * @param actionEvent The event to be handled
     */
    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            case "Take Away Advice":
                //If the user take away the advice, clear the area
                this.advicePrinterEmulator.takeAwayAdvice();
                this.advicePrinterMBox.send(new Msg(this.id, this.advicePrinterMBox, Msg.Type.AP_AdviceTaken, null));
                break;

            case "Let It Get Jammed":
                advicePrinterEmulator.letItGetJammed();
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    /**
     * Set the content of the big text area on the GUI
     * @param adviceText The advice content that is printed out.
     */
    public void setMainText(String adviceText)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    advicePrinterTextArea.setText(adviceText);
                } catch (Exception e) {
                    log.severe(id + ": failed to set text");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set the content of the text field indicating the number of remaining paper pieces. This method won't affect
     * the true value of the number of remaining paper pieces which is stored in emulator
     * @param num The new number of remaining pieces of paper.
     */
    public void setRemainingPaperText(String num)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    remainingPaperField.setText(num);
                } catch (Exception e) {
                    log.severe(id + ": failed to set remaining paper field");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set the content of the text field indicating the status of advice printer. This method won't affect
     * the true value of the status of advice printer which is stored in emulator
     * @param status A string indicating the new status of advice printer.
     */
    public void setAdviceStatusField(String status)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    adviceStatusField.setText(status);
                } catch (Exception e) {
                    log.severe(id + ": failed to set status field");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Update the relevant GUI components as if this advice printer retains the advice.
     */
    public void retainAdvice()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    setMainText("");
                    //JOptionPane.showMessageDialog(null, "Advice retained!");
                    Alert alert=new Alert(Alert.AlertType.INFORMATION, "Advice retained!");
                    alert.show();
                } catch (Exception e) {
                    log.severe(id + ": failed to show retaining advice event");
                    e.printStackTrace();
                }
            }
        });


    }

    /**
     * Update the relevant GUI components as if this advice printer times out without retaining the advice.
     */
    public void timeoutWithoutRetainingAdvice()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //JOptionPane.showMessageDialog(null, "Advice Printer Timeout!");
                    Alert alert=new Alert(Alert.AlertType.INFORMATION, "Advice Printer Timeout!");
                    alert.show();
                } catch (Exception e) {
                    log.severe(id + ": failed to show Advice Printer Timeout event");
                    e.printStackTrace();
                }
            }
        });
    }

} // CardReaderEmulatorController
