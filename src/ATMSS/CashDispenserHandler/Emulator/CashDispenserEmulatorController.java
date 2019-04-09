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


//======================================================================
// CashDispenserEmulatorController
public class CashDispenserEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private CashDispenserEmulator cashDispenserEmulator;
    private MBox cashDispenserMBox;



    /*@FXML TextArea advicePrinterTextArea;
    @FXML TextField adviceStatusField;
    @FXML TextField remainingPaperField;*/

    @FXML TextField remaining500HKDNotesField;
    @FXML TextField remaining100HKDNotesField;
    @FXML TextArea cashOutArea;
    @FXML TextField cashStatusField;

    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CashDispenserEmulator cashDispenserEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.cashDispenserEmulator = cashDispenserEmulator;
        this.cashDispenserMBox = appKickstarter.getThread("CashDispenserHandler").getMBox();
    } // initialize


    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
        Button btn = (Button) actionEvent.getSource();

        switch (btn.getText()) {
            /*case "Card 1":
                cardNumField.setText(appKickstarter.getProperty("CardReader.Card1"));
                break;

            case "Card 2":
                cardNumField.setText(appKickstarter.getProperty("CardReader.Card2"));
                break;

            case "Card 3":
                cardNumField.setText(appKickstarter.getProperty("CardReader.Card3"));
                break;

            case "Reset":
                cardNumField.setText("");
                break;*/

            /*case "Take Away Advice":
                *//*if (cardNumField.getText().length() != 0) {
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardInserted, cardNumField.getText()));
                    cardReaderTextArea.appendText("Sending " + cardNumField.getText()+"\n");
                    cardStatusField.setText("Card Inserted");
                }*//*
                //If the user take away the advice, clear the area
                this.advicePrinterEmulator.takeAwayAdvice();
                this.advicePrinterMBox.send(new Msg(this.id, this.advicePrinterMBox, Msg.Type.AP_AdviceTaken, null));
                break;

            case "Let It Get Jammed":
                *//*if (cardStatusField.getText().compareTo("Card Ejected") == 0) {
                    cardReaderTextArea.appendText("Removing card\n");
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardRemoved, cardNumField.getText()));
                }*//*
                advicePrinterEmulator.letItGetJammed();
                break;*/

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


    /*//------------------------------------------------------------
    // updateCardStatus
    public void updateCardStatus(String status) {
        cardStatusField.setText(status);
    } // updateCardStatus


    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        cardReaderTextArea.appendText(status+"\n");
    } // appendTextArea*/

    /*public void setMainText(String adviceText)
    {
        this.advicePrinterTextArea.setText(adviceText);
    }

    public void setRemainingPaperText(String num)
    {
        this.remainingPaperField.setText(num);
    }

    public void setAdviceStatusField(String status)
    {
        this.adviceStatusField.setText(status);
    }

    public void retainAdvice()
    {
        this.setMainText("");
        JOptionPane.showMessageDialog(null, "Advice retained!");

    }*/

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

    public String getCashStatus()
    {
        return this.cashStatusField.getText();
    }

    //Different from "retain cash", which means retain prepared but not out yet cash
    //Here "retain out cash" means retain the cash which is already out
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

    public void timeoutWithoutRetainingOutCash()
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    //JOptionPane.showMessageDialog(null, "Cash Dispenser Timeout!");
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
