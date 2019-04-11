package ATMSS.CardReaderHandler;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.*;


/**
 * Represents the card reader handler.
 */
//======================================================================
// CardReaderHandler
public class CardReaderHandler extends AppThread {
	/**
	 * Construct a new instance of card reader handler.
	 * @param id The ID of card reader.
	 * @param appKickstarter An appKickstarter object, in this application, pass an ATMSSStarter to it.
	 */
    //------------------------------------------------------------
    // CardReaderHandler
    public CardReaderHandler(String id, AppKickstarter appKickstarter) {
	super(id, appKickstarter);
    } // CardReaderHandler


	/**
	 * Start the card reader thread.
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
		case CR_CardInserted:
			handleCardInsert();
		    atmss.send(new Msg(id, mbox, Msg.Type.CR_CardInserted, msg.getDetails()));
		    break;

		case CR_EjectCard:
		    handleCardEject();
		    break;

		case CR_CardRemoved:
			handleCardRemove();
			atmss.send(new Msg(id, mbox, Msg.Type.CR_CardRemoved, msg.getDetails()));
		    break;

			case TimesUp:
				atmss.send(new Msg(id,mbox,Msg.Type.CR_TimeOut, msg.getDetails()));
				break;

			case CR_Retain:
				handleCardRetain();
				break;

			case CR_MachError:
				atmss.send(new Msg(id,mbox,Msg.Type.CR_MachError, msg.getDetails()));
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
	 * Handle the Card Insert action from the user.
	 */
    //------------------------------------------------------------
    // handleCardInsert
    protected void handleCardInsert() {
	log.info(id + ": card inserted");
    } // handleCardInsert


	/**
	 * Handle the Card Eject intrustion from ATMSS.
	 */
    //------------------------------------------------------------
    // handleCardEject
    protected void handleCardEject() {
	log.info(id + ": card ejected");
    } // handleCardEject


	/**
	 * Handle the Card Remove action from the user.
	 */
    //------------------------------------------------------------
    // handleCardRemove
    protected void handleCardRemove() {
	log.info(id + ": card removed");
    } // handleCardRemove

	/**
	 * Handle the Card Retain instruction from ATMSS.
	 */
	//handleCardRetain
	protected void handleCardRetain()  {
		log.info(id + ": card retained");
	} // handleCardRetain
} // CardReaderHandler
