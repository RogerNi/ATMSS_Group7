package ATMSS.CardReaderHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import java.util.logging.Logger;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;


/**
 * Represents the controller of GUI for card reader.
 */
//======================================================================
// CardReaderEmulatorController
public class CardReaderEmulatorController {
	/**
	 * The ID of card reader.
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
	 * The card reader emulator.
	 */
    private CardReaderEmulator cardReaderEmulator;
	/**
	 * The mailbox of card reader, used for communication with handler.
	 */
    private MBox cardReaderMBox;
	/**
	 * The text of card number.
	 */
    public TextField cardNumField;
	/**
	 * The status of card reader.
	 */
    public TextField cardStatusField;
	/**
	 * The main text.
	 */
    public TextArea cardReaderTextArea;


	/**
	 * Initial the controller.
	 * @param id The ID of card reader.
	 * @param appKickstarter In this application, it is an ATMSSStarter.
	 * @param log The logger to record the hardware running status.
	 * @param cardReaderEmulator The card reader emulator.
	 */
    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, CardReaderEmulator cardReaderEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
	this.log = log;
	this.cardReaderEmulator = cardReaderEmulator;
	this.cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
    } // initialize


	/**
	 * Handle the button clicking events on card reader emulator GUI
	 * @param actionEvent The event to be handled
	 */
    //------------------------------------------------------------
    // buttonPressed
    public void buttonPressed(ActionEvent actionEvent) {
	Button btn = (Button) actionEvent.getSource();

	switch (btn.getText()) {
	    case "Card 1":
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
		break;

	    case "Insert Card":
			if(cardStatusField.getText().equals("")||cardStatusField.getText().equals("Card Reader Empty")){
				if (cardNumField.getText().length() != 0) {
					cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardInserted, cardNumField.getText()));
					cardReaderTextArea.appendText("Sending " + cardNumField.getText()+"\n");
					cardStatusField.setText("Card Inserted");
				}
			}
			break;

	    case "Remove Card":
	        if (cardStatusField.getText().compareTo("Card Ejected") == 0) {
		    cardReaderTextArea.appendText("Removing card\n");
		    cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_CardRemoved, cardNumField.getText()));
		}
		break;

		case  "Machine Error":
			cardReaderMBox.send(new Msg(id, cardReaderMBox, Msg.Type.CR_MachError, ""));
			break;

	    default:
	        log.warning(id + ": unknown button: [" + btn.getText() + "]");
		break;
	}
    } // buttonPressed


	/**
	 * Update the status of card reader, which is used to specify the validity of actions.
	 * @param status The new status of card reader.
	 */
    //------------------------------------------------------------
    // updateCardStatus
    public void updateCardStatus(String status) {
        Platform.runLater(() -> {
            cardStatusField.setText(status);
        });
    } // updateCardStatus


	/**
	 * Output the status of card reader.
	 * @param status The status of card reader.
	 */
    //------------------------------------------------------------
    // appendTextArea
    public void appendTextArea(String status) {
        Platform.runLater(() -> {
            cardReaderTextArea.appendText(status + "\n");
        });
    } // appendTextArea

	/**
	 * Get the present status of card reader.
	 * @return Return the present status of card reader.
	 */
	//getStatus
	public TextField getCardStatusField(){
    	return cardStatusField;
	}//getStatus
} // CardReaderEmulatorController
