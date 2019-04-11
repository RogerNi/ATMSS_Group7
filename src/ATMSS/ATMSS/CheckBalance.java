package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import static AppKickstarter.misc.Msg.Type.*;

public class CheckBalance extends Activity {

    int stage = 0;
    //    checkBalance:
//0:waiting account
//1:select account
//2:waiting loading balance
//3:show balance
//4:Brief Balance Show
    AdviceTemp advice;
    String[] accs;

    public CheckBalance(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
    }

    @Override
    void forward(Msg msg) {
        log.info("Check Balance Activity: Get Msg: Type: " +msg.getType()+ ", Msg: "+msg.getDetails());
        switch (msg.getType()) {

            case ACT_Start:
                // BAMS Check_Balance
                if (msg.getDetails().equals("CheckBalance")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait!:F:N", "td");  // Set screen to waiting
                    addQueue(BAMS, "getAcc", "");
                } else {
                    stage = 4;
                    addQueue(TD_UpdateDisplay, "0:TEMP1:Please Wait.:F:N", "td");
                    addQueue(BAMS, "enquiry:" + msg.getDetails(), "");
                }
                break;
            case KP_KeyPressed:
                addQueue(Msg.Type.BZ_ShortBuzz, "", "b");
                if (msg.getDetails().equals("Cancel")) {
                    addQueue(Msg.Type.ACT_Abort, "Eject:End", "");
                }
                break;
            case TD_MouseClicked:
                if (stage == 4)
                    addQueue(ACT_Abort,"End","");
                if (stage == 1) {
                    stage = 2;
                    String account = accs[Integer.valueOf(msg.getDetails())];
                    advice = new AdviceTemp("Check Balance");
                    advice.setAccount(account);
                    addQueue(TD_UpdateDisplay, "0:TEMP1:Please Wait.:F:N", "td");
                    addQueue(BAMS, "enquiry:" + account, "");
                } else if (stage == 3) {
                    switch (msg.getDetails()) {
                        case "0":
                            addQueue(ACT_Abort, "MainMenu", "");
                            break;
                        case "1":
                            addQueue(ACT_Abort, "PrintAdvice," + advice.generate() + ":MainMenu", "");
                            break;
                        case "2":
                            addQueue(ACT_Abort, "Eject:PrintAdvice," + advice.generate() + ":End", "");
                            break;
                        case "3":
                            addQueue(ACT_Abort, "Eject:End", "");
                            break;
                    }
                }
                break;
            case BAMS:

                String[] reply = msg.getDetails().split(":");
                if (reply[0].equals("enquiry")) {
                    // Show Enquiry Result
                    if (stage != 4) {
                        stage = 3;
                        advice.setAmount(reply[1]);
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP2:Account Balance\n" +
                                reply[1] + ":Back to Main Menu:Print Advice and Back:Print Advice and End:End Service:F", "td");
                    } else {
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP3:Account Balance\n" +
                                reply[1] + ":Continue::F", "td");
                    }
                } else {
                    // Select Account
                    stage = 1;
                    accs = reply[1].split("/");
                    String accString = "";
                    for (String acc : accs) {
                        accString += ":" + acc;
                    }
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP2:Please select the account you want to check" +
                            accString + ":F", "td");
                }
                break;
            case TD_TimesUp:
                addQueue(ACT_AbortNow, "Retain:End", "");
                break;
            default:
                break;
        }
    }
}
