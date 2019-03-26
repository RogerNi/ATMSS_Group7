package ATMSS.ATMSS;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;
import AppKickstarter.timer.Timer;


//======================================================================
// ATMSS
public class ATMSS extends AppThread {
    private MBox cardReaderMBox;
    private MBox keypadMBox;
    private MBox touchDisplayMBox;
<<<<<<< HEAD
=======
    private MBox cashDispenserMBox;
    private MBox cashDepositMBox;
    private MBox advicePrinterMBox;
    private MBox buzzerMBox;
>>>>>>> 32badf09f03914641bb40b364f4adc547501e84b

    //------------------------------------------------------------
    // ATMSS
    public ATMSS(String id, AppKickstarter appKickstarter) throws Exception {
	super(id, appKickstarter);
    } // ATMSS


    //------------------------------------------------------------
    // run
    public void run() {
	Timer.setTimer(id, mbox, 60000);
	log.info(id + ": starting...");

	cardReaderMBox = appKickstarter.getThread("CardReaderHandler").getMBox();
	keypadMBox = appKickstarter.getThread("KeypadHandler").getMBox();
	touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
<<<<<<< HEAD
=======
	cashDispenserMBox = appKickstarter.getThread("").getMBox();
	cashDepositMBox = appKickstarter.getThread("").getMBox();
	advicePrinterMBox = appKickstarter.getThread("").getMBox();
	buzzerMBox = appKickstarter.getThread("").getMBox();
>>>>>>> 32badf09f03914641bb40b364f4adc547501e84b

	for (boolean quit = false; !quit;) {
	    Msg msg = mbox.receive();

	    log.fine(id + ": message received: [" + msg + "].");

	    switch (msg.getType()) {
<<<<<<< HEAD
		case TD_MouseClicked:
		    log.info("MouseCLicked: " + msg.getDetails());
		    processMouseClicked(msg);
		    break;

=======
>>>>>>> 32badf09f03914641bb40b364f4adc547501e84b
		case KP_KeyPressed:
		    log.info("KeyPressed: " + msg.getDetails());
		    processKeyPressed(msg);
		    break;

		case CR_CardInserted:
		    log.info("CardInserted: " + msg.getDetails());
		    break;

		case TimesUp:
		    Timer.setTimer(id, mbox, 60000);
		    log.info("Poll: " + msg.getDetails());
		    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    keypadMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.Poll, ""));
		    break;

		case PollAck:
		    log.info("PollAck: " + msg.getDetails());
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
    // processKeyPressed
    private void processKeyPressed(Msg msg) {
        // *** The following is an example only!! ***
        if (msg.getDetails().compareToIgnoreCase("Cancel") == 0) {
	    cardReaderMBox.send(new Msg(id, mbox, Msg.Type.CR_EjectCard, ""));
<<<<<<< HEAD
	} else if (msg.getDetails().compareToIgnoreCase("1") == 0) {
	    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "BlankScreen"));
	} else if (msg.getDetails().compareToIgnoreCase("2") == 0) {
	    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "MainMenu"));
	} else if (msg.getDetails().compareToIgnoreCase("3") == 0) {
	    touchDisplayMBox.send(new Msg(id, mbox, Msg.Type.TD_UpdateDisplay, "Confirmation"));
	}
    } // processKeyPressed


    //------------------------------------------------------------
    // processMouseClicked
    private void processMouseClicked(Msg msg) {
	// *** process mouse click here!!! ***
    } // processMouseClicked
=======
	}
    } // processKeyPressed
>>>>>>> 32badf09f03914641bb40b364f4adc547501e84b
} // CardReaderHandler