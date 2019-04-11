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


//======================================================================
// AdvicePrinterEmulatorController
public class AdvicePrinterEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private AdvicePrinterEmulator advicePrinterEmulator;
    private MBox advicePrinterMBox;



    @FXML TextArea advicePrinterTextArea;
    @FXML TextField adviceStatusField;
    @FXML TextField remainingPaperField;



    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, AdvicePrinterEmulator advicePrinterEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.advicePrinterEmulator = advicePrinterEmulator;
        this.advicePrinterMBox = appKickstarter.getThread("AdvicePrinterHandler").getMBox();
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

            case "Take Away Advice":
                /*if (cardNumField.getText().length() != 0) {
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardInserted, cardNumField.getText()));
                    cardReaderTextArea.appendText("Sending " + cardNumField.getText()+"\n");
                    cardStatusField.setText("Card Inserted");
                }*/
                //If the user take away the advice, clear the area
                this.advicePrinterEmulator.takeAwayAdvice();
                this.advicePrinterMBox.send(new Msg(this.id, this.advicePrinterMBox, Msg.Type.AP_AdviceTaken, null));
                break;

            case "Let It Get Jammed":
                /*if (cardStatusField.getText().compareTo("Card Ejected") == 0) {
                    cardReaderTextArea.appendText("Removing card\n");
                    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardRemoved, cardNumField.getText()));
                }*/
                advicePrinterEmulator.letItGetJammed();
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
