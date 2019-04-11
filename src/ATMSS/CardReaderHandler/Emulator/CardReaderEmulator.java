package ATMSS.CardReaderHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.CardReaderHandler.CardReaderHandler;
import AppKickstarter.timer.Timer;

import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

/**
 * Represent the hardware component: card reader emulator.
 */

//======================================================================
// CardReaderEmulator
public class CardReaderEmulator extends CardReaderHandler {
	/**
	 * Reference to the ATMSS starter.
	 */
    private ATMSSStarter atmssStarter;
	/**
	 * The ID of card reader.
	 */
    private String id;
	/**
	 * The stage of GUI.
	 */
    private Stage myStage;
	/**
	 * Reference to the controller of the GUI for card reader.
	 */
    private CardReaderEmulatorController cardReaderEmulatorController;
	/**
	 * The ID of Timer.
	 */
	private int timer_id;

	/**
	 * Constructs a new Card Reader Emulator instance.
	 * @param id The ID of card reader
	 * @param atmssStarter The ATMSS Starter
	 */
    //------------------------------------------------------------
    // CardReaderEmulator
    public CardReaderEmulator(String id, ATMSSStarter atmssStarter) {
	super(id, atmssStarter);
	this.atmssStarter = atmssStarter;
	this.id = id;
	this.timer_id = timer_id;
    } // CardReaderEmulator


	/**
	 * Start the card reader emulator.
	 * @throws Exception
	 */
    //------------------------------------------------------------
    // start
    public void start() throws Exception {
	Parent root;
	myStage = new Stage();
	FXMLLoader loader = new FXMLLoader();
	String fxmlName = "CardReaderEmulator.fxml";
	loader.setLocation(CardReaderEmulator.class.getResource(fxmlName));
	root = loader.load();
		Platform.runLater(() -> {
			cardReaderEmulatorController = (CardReaderEmulatorController) loader.getController();
			cardReaderEmulatorController.initialize(id, atmssStarter, log, this);
			myStage.initStyle(StageStyle.DECORATED);
			myStage.setScene(new Scene(root, 350, 470));
			myStage.setTitle("Card Reader");
			myStage.setResizable(false);
			myStage.setOnCloseRequest((WindowEvent event) -> {
				atmssStarter.stopApp();
				Platform.exit();
			});
			myStage.show();
		});
    } // CardReaderEmulator


	/**
	 * Handle the Card Insert action from users
	 * Update the status to Inserted.
	 */
    //------------------------------------------------------------
    // handleCardInsert
    protected void handleCardInsert() {
        // fixme
	super.handleCardInsert();
	cardReaderEmulatorController.appendTextArea("Card Inserted");
	cardReaderEmulatorController.updateCardStatus("Card Inserted");
    } // handleCardInsert


	/**
	 * Handle the card eject instruction from the ATMSS.
	 * Update the status to Ejected.
	 */
    //------------------------------------------------------------
    // handleCardEject
    protected void handleCardEject() {
        // fixme
		if(cardReaderEmulatorController.getCardStatusField().getText().compareTo("Card Inserted")==0){
			super.handleCardEject();
			cardReaderEmulatorController.appendTextArea("Card Ejected");
			cardReaderEmulatorController.updateCardStatus("Card Ejected");
			timer_id = Timer.setTimer(id,mbox,Integer.parseInt(appKickstarter.getProperty("CR.TimeLimit")));
		}
    } // handleCardEject


	/**
	 * Handle the card remove action of users.
	 * Update the status to Empty.
	 */
    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
	// fixme
	super.handleCardRemove();
	cardReaderEmulatorController.appendTextArea("Card Removed");
	cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
	Timer.cancelTimer(id, mbox,timer_id);
    } // handleCardRemove

	/**
	 * Handle the card retain instruction from the ATMSS.
	 * Update the status to Empty.
	 */
	//handleCardRetain
	protected  void handleCardRetain(){
    	if(cardReaderEmulatorController.getCardStatusField().getText().compareTo("Card Ejected")==0||cardReaderEmulatorController.getCardStatusField().getText().compareTo("Card Inserted")==0){
			super.handleCardRetain();
			cardReaderEmulatorController.appendTextArea("Card Retained");
			cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
		}
    }
} // CardReaderEmulator
