package ATMSS.TouchDisplayHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;

import java.io.FileInputStream;
import java.util.Properties;

/**
 * Represents the touch screen display handler.
 */
//======================================================================
// TouchDisplayHandler
public class TouchDisplayHandler extends AppThread {

	/**
	 * Configuration information loaded from ATM.cfg
	 */
	private Properties cfg;
	/**
	 * The freezing time of the screen, if any freezing is needed. The value will be read from the configuration file. If failed to read,
	 * it will be set to 3000.
	 */
	private long FREEZING_TIME_MILLISECONDS;

	/**
	 * Construct a new instance of touch screen display handler.
	 * @param id The ID of touch screen display.
	 * @param appKickstarter An appKickstarter object, in this application, pass an ATMSSStarter to it.
	 * @throws Exception
	 */
	//------------------------------------------------------------
    // TouchDisplayHandler
    public TouchDisplayHandler(String id, AppKickstarter appKickstarter) throws Exception {
	super(id, appKickstarter);
	cfg = new Properties();
	try {
		FileInputStream in = new FileInputStream("etc/ATM.cfg");
		cfg.load(in);
		log.finer("Properties Read Successfully");
		in.close();
		this.FREEZING_TIME_MILLISECONDS=Long.valueOf(cfg.getProperty("TD.Freeze"));
	} catch (Exception e) {
		log.severe("Properties Read Failed");
		e.printStackTrace();
		//Set it to default value if fails to load configuration file
		this.FREEZING_TIME_MILLISECONDS=3000;
	}
    } // TouchDisplayHandler


	/**
	 * Start the touch screen display thread.
	 */
    //------------------------------------------------------------
    // run
    public void run() {
	MBox atmss = appKickstarter.getThread("ATMSS").getMBox();
	log.info(id + ": starting...");

	for (boolean quit = false; !quit;) {
	    Msg msg = mbox.receive();

	    log.fine(id + ": message received: [" + msg + "].");

	    switch (msg.getType()) {
		case TD_MouseClicked:
		    atmss.send(new Msg(id, mbox, Msg.Type.TD_MouseClicked, msg.getDetails()));
		    break;

		case TD_UpdateDisplay:
			//The handleUpdateDisplay method will return a boolean indicating whether freeze the screen for a while.
		    if(handleUpdateDisplay(msg))
			{
				try {

					Thread.sleep(this.FREEZING_TIME_MILLISECONDS);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		    break;

		//One of my teammates ask me to tell ATMSS if the touch screen has not interacted with user
		//or receive instructions from ATMSS for a certain time.
		case TimesUp:

			atmss.send(new Msg(this.id, this.mbox, Msg.Type.TD_TimesUp, ""));
			break;


		case Poll:
		    atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
		    break;

		case Terminate:
		    quit = true;
		    break;

		default:
		    log.warning(id + ": unknown message type: [" + msg + "]");
	    }
	}

	// declaring our departure
	appKickstarter.unregThread(this);
	log.info(id + ": terminating...");
    } // run


	/**
	 * Handle the updating display instruction from ATMSS.
	 * @param msg The message telling to update display. The detailed parameters on how to update the display is in the details
	 *            attribute of message. All parameters are separated by colon. The first parameter must be 0 or 1. 0 means to update
	 *            the whole screen, while 1 means to update the content in input field only. If the first parameter is 0, the
	 *            second parameter must be TEMP1, TEMP2, or TEMP3, which means which display template should be used.
	 *            Template 1 is a blank template. Template 2 contains 6 buttons, among them some buttons may have no text.
	 *            Template 3 contains 2 buttons, usually used for confirmation page. No matter which template is used,
	 *            the third parameter is the main text to be displayed at the middle of the screen. Next parameter will
	 *            be different for different template. If it is TEMP1, next parameter must
	 *            be T or F. If it is T, screen will display an input field, then read the next parameter, which is the
	 *            prefix of that input field (Character N is reserved for other use, thus never let the prefix be a single
	 *            character N). If it is F, it will not display the input field. No metter it is T or F, you can add an
	 *            optional parameter with value S or N. If no this parameter or is S, the screen will freeze if there is
	 *            no input field, to let the user see the message on the screen clearly. If it is N, the screen will be forced
	 *            not to freeze. If the second parameter is TEMP2, the following 1 to 6
	 *            parameter(s) will be the texts on buttons, and the touch screen will check how many texts are received. If
	 *            it receives less than 6 texts, 6 buttons will still be displayed with some having no text, that is to
	 *            mimic the real ATM screen. After them, the next parameter must be T or F, the function is the same as
	 *            described in the case of TEMP1. If the second parameter is TEMP3, the fourth and fifth parameter are
	 *            the texts to be displayed on the 2 buttons (if one has no text, next parameter still should
	 *            exist but is a null string, e.g. ......:Yes::... to display one button with Yes on it and another with
	 *            no text). The sixth parameter must be T or F, the function is the same as described in case of
	 *            TEMP1. If the first parameter is 1, the second parameter should be the contents of input field.
	 * @return nothing meaningful. The overridden method in its subclass will return the boolean indicating whether the screen should freeze for a while.
	 */
    //------------------------------------------------------------
    // handleUpdateDisplay
	//Due to the need of realising the freezing screen functionality, I need to return a boolean variable.
    protected boolean handleUpdateDisplay(Msg msg) {
	log.info(id + ": update display -- " + msg.getDetails());
	return true;
    } // handleUpdateDisplay


	/**
	 * Handle the touch screen display timeout event
	 */
	protected void handleTimeout()
	{
		log.info(id+": times up!");
	}


} // TouchDisplayHandler
