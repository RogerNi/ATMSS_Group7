package ATMSS.CashDispenserHandler.Emulator;

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
 * Represents the controller of GUI for cash dispenser.
 */
//======================================================================
// CashDispenserEmulatorController
public class CashDispenserEmulatorController {
    /**
     * The ID of cash dispenser.
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
     * The cash dispenser emulator.
     */
    private CashDispenserEmulator cashDispenserEmulator;
    /**
     * The mailbox of cash dispenser, used for communication with handler.
     */
    private MBox cashDispenserMBox;




    /**
     * The text field at the top, used for showing the number of remaining 500HKD notes for easier testing. In real case,
     * cash dispenser cannot display that.
     */
    @FXML TextField remaining500HKDNotesField;
    /**
     * The text field at the top, used for showing the number of remaining 100HKD notes for easier testing. In real case,
     * cash dispenser cannot display that.
     */
    @FXML TextField remaining100HKDNotesField;
    /**
     * The big text area in the middle. it is dedicated to display the numbers of each kind of bank notes which is out.
     */
    @FXML TextArea cashOutArea;
    /**
     * The text field at the bottom. It is to display the current status of cash dispenser.
     */
    @FXML TextField cashStatusField;

    /**
     * Initial the controller.
     * @param id The ID of cash dispenser.
     * @param appKickstarter In this application, it is an ATMSSStarter.
     * @param log The logger to record the hardware running status.
     * @param cashDispenserEmulator The cash dispenser emulator.
     */
    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CashDispenserEmulator cashDispenserEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.cashDispenserEmulator = cashDispenserEmulator;
        this.cashDispenserMBox = appKickstarter.getThread("CashDispenserHandler").getMBox();
    } // initialize


    /**
     * Handle the button clicking events on cash dispenser emulator GUI
     * @param actionEvent The event to be handled
     */
    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {

            case "Take Away Cash":
                if(this.cashStatusField.getText().equals("Cash to be taken away"))
                {
                    this.cashDispenserEmulator.takeAwayCash();
                    //Notify the handler about the remaining number bank notes
                    this.cashDispenserMBox.send(new Msg(this.id, this.cashDispenserMBox, Msg.Type.CD_Complete,
                            this.cashDispenserEmulator.getCurrentNumOf500HKDNotes()+","+this.cashDispenserEmulator.getCurrentNumOf100HKDNotes()));
                }
                break;

            default:
                log.warning(id + ": unknown button: [" + btn.getText() + "]");
                break;
        }
    } // buttonPressed


    /**
     * Set the content of the text field indicating the number of remaining 500HKD notes. This method won't affect
     * the true value of the number of remaining 500HKD notes which is stored in emulator
     * @param s A string to be displayed, containing the information of remaining 500HKD notes.
     */
    public void setRemaining500HKDNotesField(String s)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    remaining500HKDNotesField.setText(s);
                } catch (Exception e) {
                    log.severe(id + ": failed to set remaining 500HKD Notes field");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set the content of the text field indicating the number of remaining 100HKD notes. This method won't affect
     * the true value of the number of remaining 100HKD notes which is stored in emulator
     * @param s A string to be displayed, containing the information of remaining 100HKD notes.
     */
    public void setRemaining100HKDNotesField(String s)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    remaining100HKDNotesField.setText(s);
                } catch (Exception e) {
                    log.severe(id + ": failed to set remaining 100HKD Notes field");
                    e.printStackTrace();
                }
            }
        });
    }


    /**
     * Set the content of the text field indicating the status of cash dispenser. This method won't affect
     * the true value of the status of cash dispenser.
     * @param s A string indicating the new status of cash dispenser.
     */
    public void setCashStatusField(String s)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    cashStatusField.setText(s);
                } catch (Exception e) {
                    log.severe(id + ": failed to set cash status field");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Set the content of the big text area on the GUI
     * @param s The string to be displayed on the text area. It should contain information about what kind of cash and how many are out.
     */
    public void setCashOutArea(String s)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    cashOutArea.setText(s);
                } catch (Exception e) {
                    log.severe(id + ": failed to set cash out area");
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * Get the string indicating the current status of cash dispenser
     * @return a string indicating the current status of cash dispenser
     */
    public String getCashStatus()
    {
        return this.cashStatusField.getText();
    }

    /**
     * Handle the retaining cash event. Different from "retain cash", which means retain prepared but not out yet cash
     * Here "retain out cash" means retain the cash which is already out.
     */
    public void retainOutCash()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //JOptionPane.showMessageDialog(null, "Out cash retained!");
                    Alert alert=new Alert(Alert.AlertType.INFORMATION, "Out cash retained!");
                    alert.show();
                    //Clear the cash out area.
                    cashOutArea.setText("");
                    cashStatusField.setText("Ready");
                } catch (Exception e) {
                    log.severe(id + ": failed to show retain out cash event");
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Handle the timeout event if the dispenser does not have the ability to retain cash which is already out.
     */
    public void timeoutWithoutRetainingOutCash()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    Alert alert=new Alert(Alert.AlertType.INFORMATION, "Cash Dispenser Timeout!");
                    alert.show();
                } catch (Exception e) {
                    log.severe(id + ": failed to show Cash Dispenser Timeout event");
                    e.printStackTrace();
                }
            }
        });

    }

} // CardReaderEmulatorController
