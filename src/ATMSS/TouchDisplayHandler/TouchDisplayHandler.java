package ATMSS.TouchDisplayHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


//======================================================================
// TouchDisplayHandler
public class TouchDisplayHandler extends AppThread {
    //------------------------------------------------------------
    // TouchDisplayHandler

	//protected boolean sleep;
	//private int freezeTimerId;

    public TouchDisplayHandler(String id, AppKickstarter appKickstarter) throws Exception {
	super(id, appKickstarter);
	//this.freezeTimerId=-1;
    } // TouchDisplayHandler


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
			/*if(sleep)
			{
				try {
					//One of my teammate ask me to let the touch screen to sleep for 3 seconds
					//if it use template 1 and display no input box in order to let the user clearly
					//see the texts on screen
					sleep=false;
					Thread.sleep(3000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}*/
		    handleUpdateDisplay(msg);
		    break;

		//One of my teammates ask me to tell ATMSS if the touch screen has not interacted with user
		//or receive instructions from ATMSS for 60 seconds
		case TimesUp:
			atmss.send(new Msg(this.id, this.mbox, Msg.Type.TD_TimesUp, ""));
			break;

		case TD_Freeze:
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
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


    //------------------------------------------------------------
    // handleUpdateDisplay
    protected void handleUpdateDisplay(Msg msg) {
	log.info(id + ": update display -- " + msg.getDetails());
    } // handleUpdateDisplay

	/*protected boolean isSleep() {
		return sleep;
	}

	protected void setSleep(boolean sleep) {
		this.sleep = sleep;
	}*/

	/*public void setFreezeTimerId(int freezeTimerId) {
		this.freezeTimerId = freezeTimerId;
	}*/
} // TouchDisplayHandler
