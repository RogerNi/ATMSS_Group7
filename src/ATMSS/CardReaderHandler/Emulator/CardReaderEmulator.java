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


//======================================================================
// CardReaderEmulator
public class CardReaderEmulator extends CardReaderHandler {
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private CardReaderEmulatorController cardReaderEmulatorController;
    private int timer_id;

    //------------------------------------------------------------
    // CardReaderEmulator
    public CardReaderEmulator(String id, ATMSSStarter atmssStarter) {
	super(id, atmssStarter);
	this.atmssStarter = atmssStarter;
	this.id = id;
	this.timer_id = timer_id;
    } // CardReaderEmulator


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


    //------------------------------------------------------------
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
		if(cardReaderEmulatorController.getCardStatusField().getText().compareTo("Card Inserted")==0){
			super.handleCardEject();
			cardReaderEmulatorController.appendTextArea("Card Ejected");
			cardReaderEmulatorController.updateCardStatus("Card Ejected");
			timer_id = Timer.setTimer(id,mbox,60000);
		}
    } // handleCardEject


    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
	// fixme
	super.handleCardRemove();
	cardReaderEmulatorController.appendTextArea("Card Removed");
	cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
	Timer.cancelTimer(id, mbox,timer_id);
    } // handleCardRemove

	//handleCardRetain
	protected  void handleCardRetain(){
    	if(cardReaderEmulatorController.getCardStatusField().getText().compareTo("Card Ejected")==0||cardReaderEmulatorController.getCardStatusField().getText().compareTo("Card Inserted")==0){
			super.handleCardRetain();
			cardReaderEmulatorController.appendTextArea("Card Retained");
			cardReaderEmulatorController.updateCardStatus("Card Reader Empty");
		}
    }
} // CardReaderEmulator
