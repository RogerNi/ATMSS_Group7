package ATMSS.TouchDisplayHandler.Emulator;

import ATMSS.ATMSSStarter;
import ATMSS.TouchDisplayHandler.TouchDisplayHandler;
import AppKickstarter.misc.Msg;

import AppKickstarter.timer.Timer;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;

import java.util.Arrays;


//======================================================================
// TouchDisplayEmulator
public class TouchDisplayEmulator extends TouchDisplayHandler {
    private final int WIDTH = 680;
    private final int HEIGHT = 520;
    private ATMSSStarter atmssStarter;
    private String id;
    private Stage myStage;
    private TouchDisplayEmulatorController touchDisplayEmulatorController;
    private boolean sleep;

	private int timerId;
	private static final long TIME_LIMIT=60000;

    //------------------------------------------------------------
    // TouchDisplayEmulator
    public TouchDisplayEmulator(String id, ATMSSStarter atmssStarter) throws Exception {
	super(id, atmssStarter);
	this.atmssStarter = atmssStarter;
	this.id = id;
	this.sleep=false;
	this.timerId=-1;
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // start
    public void start() throws Exception {
	Parent root;
	myStage = new Stage();
	FXMLLoader loader = new FXMLLoader();
	String fxmlName = "TouchDisplayEmulator.fxml";
	loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlName));
	root = loader.load();
	touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
	touchDisplayEmulatorController.initialize(id, atmssStarter, log, this);
	myStage.initStyle(StageStyle.DECORATED);
	myStage.setScene(new Scene(root, WIDTH, HEIGHT));
	myStage.setTitle("Touch Display");
	myStage.setResizable(false);
	myStage.setOnCloseRequest((WindowEvent event) -> {
	    atmssStarter.stopApp();
	    Platform.exit();
	});
	myStage.show();
    } // TouchDisplayEmulator


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected boolean handleUpdateDisplay(Msg msg) {

	this.resetTimer();

	log.info(id + ": update display -- " + msg.getDetails());

	this.sleep=false;

	//According to the protocol that my teammates and I discussed, we separate the
	// parameters in msg.details by colon.
	String[] params=msg.getDetails().split(":",-1);

	if(params[0].equals("0"))
	{
		//According that protocol, if the first parameter is 1, I will update the whole screen.
		switch (params[1])
		{
			case "TEMP1":
				//According that protocol, the second parameter tells the screen which template to use.
				//TEMP1 means the touch screen use the first template, i.e. the one with nothing in it
				//I will add dynamic contents according to the instruction given by ATMSS
				this.sleep=reloadStage("TouchDisplayEmulator.fxml", params);
				//According that protocol, the third parameter tells the screen the main text.
				//this.touchDisplayEmulatorController.setMainText(params[2]);
				break;
			case "TEMP2":
				//According that protocol, the second parameter tells the screen which template to use.
				//TEMP2 means the touch screen use the second template, i.e. the one with 6 buttons in it
				//I will add dynamic contents according to the instruction given by ATMSS
				reloadStage("TouchDisplayMainMenu.fxml", params);
				break;
            case "TEMP3":
                //According that protocol, the second parameter tells the screen which template to use.
                //TEMP2 means the touch screen use the second template, i.e. the one with 6 buttons in it
                //I will add dynamic contents according to the instruction given by ATMSS
                reloadStage("TouchDisplayConfirmation.fxml", params);
                break;
		}
	}else if(params[0].equals("1"))
	{
	    //According to our protocol, if the first parameter is 1, I will only update the content of input field.
        //The second parameter will be the content of input field.
        touchDisplayEmulatorController.setInputFieldContent(params[1]);
	}else
	{
		switch (params[0])
		{
			case "test1":
				this.touchDisplayEmulatorController.setMainText("What?");
				this.touchDisplayEmulatorController.set6ButtonsText(new String[]{"button0"});
		}
	}

	/*switch (msg.getDetails()) {
	    case "BlankScreen":
		reloadStage("TouchDisplayEmulator.fxml");
		break;

	    case "MainMenu":
		reloadStage("TouchDisplayMainMenu.fxml");
		break;

	    case "Confirmation":
		reloadStage("TouchDisplayConfirmation.fxml");
		break;

	    default:
		log.severe(id + ": update display with unknown display type -- " + msg.getDetails());
		break;
	}*/
		return sleep;
	} // handleUpdateDisplay


    //------------------------------------------------------------
    // reloadStage
    private boolean reloadStage(String fxmlFName, String[] params) {
        TouchDisplayEmulator touchDisplayEmulator = this;


		//One of my teammate ask me to let the touch screen to show its content to user for at least 3 seconds
		//if it use template 1 and display no input box in order to let the user clearly
		//see the texts on screen
		//This seemingly simple functionality turns out to be very difficult ti implement
		//If I let the thread sleep here, since updating display is asynchronous, the updating will
		//also pause here, which is undesirable. After try a lot of unsuccessful method (including
		//create another thread and pause it, then join), I come up with this method: let the controller
		//send freeze signal to handler, then let this thread continue. The handler will pause itself upon
		//receiving this signal, it pause itself without affecting current updating. But this is still unsatisfactory.
		//If ATMSS send me 2 consecutive singnals, the concurrency control has problems.
		//At last, I let the reloadStage and handleUpdateDisplay to return a boolean variable to tell the
		//handler thread whether to freeze or not. This attempt succeeds. A little point is that I set
		//the value of "sleep" not in "runLater" part but here so that it can work correctly.
        if(fxmlFName.equals("TouchDisplayEmulator.fxml"))
		{
			if(params[3].equals("F"))
			{
				//My teammate said that maybe ATMSS will send me an extra parameter to instruct screen not to sleep
				//If that extra parameter doesn't exist or is "S", the screen will freeze. Otherwise, it doesn't freeze.
				if(params.length<=4 || params[4].equals("S"))
				{
					this.sleep=true;
				}
			}
		}



        Platform.runLater(new Runnable() {
	    @Override
	    public void run() {
		try {
		    log.info(id + ": loading fxml: " + fxmlFName);

			//According to that protocol, if we use template 2, the ATMSS will give me texts of some buttons
			//The ATMSS may give me 1 to 6 parameters for texts of buttons, but won't give me how many parameters
			//are for texts of  buttons, I have to check it by myself.
			int numOfButtons;


		    Parent root;
		    FXMLLoader loader = new FXMLLoader();
		    loader.setLocation(TouchDisplayEmulator.class.getResource(fxmlFName));
		    root = loader.load();
		    touchDisplayEmulatorController = (TouchDisplayEmulatorController) loader.getController();
		    touchDisplayEmulatorController.initialize(id, atmssStarter, log, touchDisplayEmulator);
		    myStage.setScene(new Scene(root, WIDTH, HEIGHT));


		    //I put most part of code for updating display here due to the need of currency control.
            //I must wait until the new stage is completely loaded then update dynamic content.

			//According that protocol, the third parameter tells the screen the main text.
			touchDisplayEmulatorController.setMainText(params[2]);

			//Tell the controller that we have gone to another fxml file.
            touchDisplayEmulatorController.setCurrentPage(fxmlFName);

			System.out.println("fxmlFName: "+fxmlFName);

			switch(fxmlFName)
            {
                case "TouchDisplayEmulator.fxml":
                    //According to that protocol, if we use template 1, the 4th parameter will tell the screen whether to
                    //display the input field (e.g. PIN, amount of money)
                    if(params[3].equals("T"))
                    {
                        //According to that protocol, if we use template 1, the 5th parameter will tell the screen
                        //what the input field prefix is.
                        touchDisplayEmulatorController.setInputFieldPrefixAndUnderline(params[4]);
                    }else
					{
						//One of my teammate ask me to let the touch screen to show its content to user for at least 3 seconds
						//if it use template 1 and display no input box in order to let the user clearly
						//see the texts on screen
						//This seemingly simple functionality turns out to be very difficult ti implement
						//If I let the thread sleep here, since updating display is asynchronous, the updating will
						//also pause here, which is undesirable. After try a lot of unsuccessful method (including
						//create another thread and pause it, then join), I come up with this method: let the controller
						//send freeze signal to handler, then let this thread continue. The handler will pause itself upon
						//receiving this signal, it pause itself without affecting current updating. But this is still unsatisfactory.
						//If ATMSS send me 2 consecutive singnals, the concurrency control has problems.
						//At last, I let the reloadStage and handleUpdateDisplay to return a boolean variable to tell the
						//handler thread whether to freeze or not. This attempt succeeds. A little point is that I set\
						//the value of "sleep" not here because it is "runLater". I set it at beginning of this method.
						//sleep=true;
//						Freeze f=new Freeze();
//						f.start();
//						f.join();
						//touchDisplayEmulatorController.freeze();
						//sleep=true;
					}
                    break;
                case "TouchDisplayMainMenu.fxml":
                    //According to that protocol, if we use template 2, the ATMSS will give me texts of some buttons
					//The ATMSS may give me 1 to 6 parameters for texts of buttons, but won't give me how many parameters
					//are for texts of  buttons, I have to check it by myself.

					if(params[params.length-1].equals("F"))
					{
						//My teammates promise me that they will never let ATMSS give me the input field prefix to be
						//"F", thus I am able to check the number of buttons by myself, otherwise I have no way to
						// check it by myself, since the text on button may be anything, including "T" and "F"
						//With this assumption, the last parameter is "F" means I shouldn't display input box.
						numOfButtons=params.length-4;
					}else
					{
						//Else, the screen should display the input box
						//The method of calculating number of buttons is different
						numOfButtons=params.length-5;
						//According to that protocol, if we use template 2 and display the input box, the last
						// parameter will tell the scree what the input field prefix is.
						touchDisplayEmulatorController.setInputFieldPrefixAndUnderline(params[params.length-1]);
					}
                    touchDisplayEmulatorController.set6ButtonsText(Arrays.copyOfRange(params, 3, 3+numOfButtons));
                    //According to that protocol, if we use template 2, the 10th parameter will tell the screen whether to
                    //display the input field (e.g. PIN, amount of money)
                    /*if(params[9].equals("T"))
                    {
                        //According to that protocol, if we use template 2, the 11th parameter will tell the screen
                        //what the input field prefix is.
                        touchDisplayEmulatorController.setInputFieldPrefixAndUnderline(params[10]);
                    }*/
                    break;
                case "TouchDisplayConfirmation.fxml":
                    //According to that protocol, if we use template 3, the 4th and the 5th parameters will be the
                    //texts of the 6 buttons.
                    touchDisplayEmulatorController.set2ButtonsText(Arrays.copyOfRange(params, 3, 5));
                    //According to that protocol, if we use template 3, the 6th parameter will tell the screen whether to
                    //display the input field (e.g. PIN, amount of money)
                    if(params[5].equals("T"))
                    {
                        //According to that protocol, if we use template 3, the 7th parameter will tell the screen
                        //what the input field prefix is.
                        touchDisplayEmulatorController.setInputFieldPrefixAndUnderline(params[6]);
                    }
                    break;
            }
		} catch (Exception e) {
		    log.severe(id + ": failed to load " + fxmlFName);
		    e.printStackTrace();
		}
	    }
	});
        return sleep;
    } // reloadStage


	protected void handleTimeout()
	{
		super.handleTimeout();
		this.timerId=-1;
	}

	public void resetTimer()
	{
		if(this.timerId!=-1)
			Timer.cancelTimer(this.id,this.mbox,this.timerId);
		this.timerId=Timer.setTimer(this.id,this.mbox,this.TIME_LIMIT);
	}

	/*public boolean isSleep() {
		return sleep;
	}*/

	/*public void setSleep(boolean sleep) {
		this.sleep = sleep;
	}*/


} // TouchDisplayEmulator
