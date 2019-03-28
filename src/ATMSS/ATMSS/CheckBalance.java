package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

public class CheckBalance extends Activity {
    CheckBalance(MBox mMbox, String mId) {
        super(mMbox, mId);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()) {
            case ACT_Start:
                // BAMS Check_Balance
                addQueue(Msg.Type.TD_UpdateDisplay, "", "td");  // Set screen to waiting
                addQueue(Msg.Type.BAMS, "enquiry","");
                break;
            case KP_KeyPressed:
                addQueue(Msg.Type.BZ_ShortBuzz,"","b");
                if (msg.getDetails().equals("Cancel")) {
                    addQueue(Msg.Type.ACT_Abort, "MainMenu", "");
                }
                break;
            case TD_MouseClicked:
                if (msg.getDetails().equals("Back"))
                    addQueue(Msg.Type.ACT_Abort, "MainMenu", "");
                break;
            case BAMS:
                String [] reply = msg.getDetails().split(":");
                if (reply[0].equals("enquiry")){
                    // Set new display
                } else {
                    // Display Error
                    addQueue(Msg.Type.ACT_Abort,"EjectCard","");
                }
                break;
            default:
                break;
        }
    }
}
