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
                addQueue(Msg.Type.TD_UpdateDisplay, "", "td");
                break;
            case KP_KeyPressed:
                if (msg.getDetails().equals("Cancel")) {
                    addQueue(Msg.Type.ACT_Abort, "MainMenu", "");
                }
                break;
            case TD_MouseClicked:
                if (msg.getDetails().equals("Back"))
                    addQueue(Msg.Type.ACT_Abort, "MainMenu", "");
                break;
            default:
                break;
        }
    }
}
