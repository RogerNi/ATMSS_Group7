package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import com.sun.javaws.ui.UpdateDialog;

import java.util.logging.Logger;

public class MainMenu extends Activity {
    public MainMenu(MBox mbox, String mid, Logger log) {
        super(mbox, mid, log);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()) {
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay, "0:" +
                        "TEMP2:" + "Welcome!\nPlease select service.:" +
                        "Check Balance:Withdraw Cash:Deposit Cash:Transfer:End Service:F", "td"); // Update MainMenu
                break;
            case KP_KeyPressed:
//                addQueue(Msg.Type.BZ_ShortBuzz,"","b");
                if (msg.getDetails().equals("Cancel"))
                    addQueue(Msg.Type.ACT_Abort, "Eject:End", "");
                break;
            case TD_MouseClicked:
                switch (msg.getDetails()) {
                    case "0":
                        addQueue(Msg.Type.ACT_Abort, "CheckBalance", "");
                        break;
                    case "1":
                        addQueue(Msg.Type.ACT_Abort, "Withdraw", "");
                        break;
                    case "2":
                        addQueue(Msg.Type.ACT_Abort, "Deposit", "");
                        break;
                    case "3":
                        addQueue(Msg.Type.ACT_Abort, "Transfer", "");
                        break;
                    case "4":
                        addQueue(Msg.Type.ACT_Abort, "Eject:End", "");
                        break;
                }
                break;
            case TD_TimesUp:
//                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Operation TimeOut!\nCard Retained.\nPlease Contact Bank for more information.","td");
                addQueue(Msg.Type.ACT_AbortNow, "Retain:End", "");
                break;
        }
    }
}
