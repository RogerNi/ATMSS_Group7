package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import static AppKickstarter.misc.Msg.Type.*;

public class CheckBalance extends Activity {
//    String next = "";

    int stage = 0;
//    checkBalance:
//0:waiting account
//1:select account
//2:waiting loading balance
//3:show balance

    String [] accs;
    CheckBalance(MBox mMbox, String mId) {
        super(mMbox, mId);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()) {
            case ACT_Start:
                // BAMS Check_Balance
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait!", "td");  // Set screen to waiting
                addQueue(BAMS, "getAcc","");
                break;
            case KP_KeyPressed:
                addQueue(Msg.Type.BZ_ShortBuzz,"","b");
                if (msg.getDetails().equals("Cancel")) {
                    addQueue(Msg.Type.ACT_Abort, "Eject:End", "");
                }
                break;
            case TD_MouseClicked:
                if (stage == 1){
                    stage = 2;
                    String account = accs[Integer.valueOf(msg.getDetails())];
                    addQueue(TD_UpdateDisplay,"0:TEMP1:Please Wait.","td");
                    addQueue(BAMS,"enquiry:" + account,"");
                } else if (stage == 3){
                    switch (msg.getDetails()){
                        case "0":
                            addQueue(ACT_Abort,"MainMenu","");
                            break;
                        case "1":
                            addQueue(ACT_Abort,"Eject:End","");
                            break;
                    }
                }
                break;
            case BAMS:
                String [] reply = msg.getDetails().split(":");
                if (reply[0].equals("enquiry")){
                    // Show Enquiry Result
                    stage = 3;
                    addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP2:Account Balance\n"+
                            reply[1] +":Back to Main Menu:End Service","td");
                } else {
                    // Select Account
                    stage = 1;
                    accs = reply[1].split("/");
                    String accString = "";
                    for (String acc : accs){
                        accString += ":" + acc;
                    }
                    addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Please select the account you want to check" +
                            accString,"td");
                }
                break;
            case TD_TimeOut:
                addQueue(ACT_AbortNow,"Retain:End","");
                break;
//            case ACT_SUBENDS:
//                if(msg.getDetails().equals("0")){
//                    switch (next){
//                        case "End":
//                            addQueue(Msg.Type.ACT_Abort,"End","");
//                            break;
//                        case "MainMenu":
//                            addQueue(Msg.Type.ACT_Abort,"MainMenu","");
//                            break;
//                        case "Eject":
//                            next = "End";
//                            runSubActivity(new Eject(masterMBox,masterId));
//                            break;
//                    }
//                }
//                else {
//                    addQueue(Msg.Type.ACT_Abort,"End","");
//                }
//                break;
            default:
                break;
        }
    }
}
