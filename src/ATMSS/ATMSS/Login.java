package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

public class Login extends Activity {
    InputBuffer inBuffer = new InputBuffer();
    int retry = 0;

    public Login(MBox mBox, String id) {
        super(mBox, id);
    }

    void forward(Msg msg) {
        switch (msg.getType()) {
            case ACT_Start:
                // Change Screen
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Input PIN:T,PIN","td");
                inBuffer.clear();
                break;
            case KP_KeyPressed:
                if (msg.getDetails().equals("Enter")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "", "td");
                    addQueue(Msg.Type.BAMS, "login:" + inBuffer.pop(), "");
                } else if (msg.getDetails().equals("Erase")) {
                    inBuffer.clear();
                    // Update Display
                    addQueue(Msg.Type.TD_UpdateDisplay,"1:","td");
                } else {
                    inBuffer.buff(msg.getDetails().toCharArray()[0]);
                    addQueue(Msg.Type.TD_UpdateDisplay,"1:"+inBuffer.get(),"td");
                    // Update Display
                }
                break;
            case BAMS:
                String[] reply = msg.getDetails().split(":",-1);
                if (reply[0].equals("login")) {
                    if (reply[1].equals("ERROR")) {
                        // Handle Error PIN
                        retry++;
                        if (retry == 2) {
                            addQueue(Msg.Type.TD_UpdateDisplay, "", "td"); // Retain Card Display
//                            addQueue(Msg.Type.CR_Retain,"","cr");
                            addQueue(Msg.Type.ACT_Abort,"Retain:End","");
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
