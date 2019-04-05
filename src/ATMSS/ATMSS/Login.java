package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.io.FileInputStream;
import java.util.Properties;
import java.util.logging.Logger;

public class Login extends Activity {
    InputBuffer inBuffer = new InputBuffer();
    int retry = 0;
    Properties cfg;

    public Login(MBox mBox, String id, Logger log) {
        super(mBox, id, log);
        cfg = new Properties();
        try {
            FileInputStream in = new FileInputStream("etc/ATM.cfg");
            cfg.load(in);
//            log.info("Properties Read Successfully");
            in.close();
        } catch (Exception e) {
//            log.warning("Properties Read Failed");
            e.printStackTrace();
        }
    }

    void forward(Msg msg) {
        switch (msg.getType()) {
            case ACT_Start:
                // Change Screen
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Input PIN:T:PIN", "td");
                inBuffer.clear();
                break;
            case KP_KeyPressed:
                if (msg.getDetails().equals("Enter")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait:F", "td");
                    addQueue(Msg.Type.BAMS, "login:" + inBuffer.pop(), "");
                } else if (msg.getDetails().equals("Erase")) {
                    inBuffer.clear();
                    // Update Display
                    addQueue(Msg.Type.TD_UpdateDisplay, "1::T,PIN", "td");
                } else if (msg.getDetails().equals("Cancel")) {
                    addQueue(Msg.Type.ACT_AbortNow,"Eject:End","");
                } else {
                    if (msg.getDetails().equals("00") || msg.getDetails().equals(".") || msg.getDetails().equals(""))
                        break;
                    inBuffer.buff(msg.getDetails().toCharArray()[0]);
                    addQueue(Msg.Type.TD_UpdateDisplay, "1:" + inBuffer.get().replaceAll("[0-9]","*"), "td");
                    // Update Display
                }
                break;
            case BAMS:
                String[] reply = msg.getDetails().split(":", -1);
                if (reply[0].equals("login")) {
                    if (reply[1].equals("WRONG")) {
                        // Handle Error PIN
                        retry++;
                        if (retry == Integer.valueOf(cfg.getProperty("ATMSS.MaxRetry"))) {
//                            addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Wrong PIN\nPlease Input PIN:F", "td"); // Retain Card Display
//                            addQueue(Msg.Type.CR_Retain,"","cr");
                            addQueue(Msg.Type.ACT_Abort, "Retain:End", "");
                        } else {
                            addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Wrong PIN\nPlease Input PIN:F", "td"); // Retry PIN
                        }
                    } else {
                        // Update credit and exit Activity
                        addQueue(Msg.Type.ACT_CRED, reply[1], "");
                        log.info("Login Activity: CRED: "+reply[1]);
                        addQueue(Msg.Type.ACT_Abort, "MainMenu", "");
                    }
                }
        }
    }
}
