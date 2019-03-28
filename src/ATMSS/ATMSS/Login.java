package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

public class Login extends Activity {
    InputBuffer inBuffer = new InputBuffer();
    int retry = 0;

    Login(MBox mBox, String id) {
        super(mBox, id);
    }

    void forward(Msg msg) {
        switch (msg.getType()) {
            case ACT_Start:
                // Change Screen
                inBuffer.clear();
                break;
            case KP_KeyPressed:
                if (msg.getDetails().equals("Enter")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "", "td");
                    addQueue(Msg.Type.BAMS, "login:" + inBuffer.pop(), "");
                } else if (msg.getDetails().equals("Back")) {
                    inBuffer.deleteLast();
                    // Update Display
                } else {
                    inBuffer.buff(msg.getDetails().toCharArray()[0]);
                    // Update Display
                }
                break;
            case BAMS:
                String[] reply = msg.getDetails().split(":");
                if (reply[0].equals("login")) {
                    if (reply[1].equals("ERROR")) {
                        // Handle Error PIN
                        retry++;
                        if (retry == 2) {
                            addQueue(Msg.Type.TD_UpdateDisplay, "", "td"); // Retain Card Display
                            addQueue(Msg.Type.ACT_Abort,"RetainCard","");
                        } else {
                            addQueue(Msg.Type.TD_UpdateDisplay,"","td"); // Retry PIN
                        }
                    } else {
                        // Update credit and exit Activity
                        addQueue(Msg.Type.ACT_CRED, reply[1], "");
                        addQueue(Msg.Type.ACT_Abort, "MainMenu", "");
                    }
                }
        }
    }
}
