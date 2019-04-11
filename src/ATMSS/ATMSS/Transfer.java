package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.ArrayList;
import java.util.logging.Logger;

import static AppKickstarter.misc.Msg.Type.*;

public class Transfer extends Activity {

    int stage = 0;
    //    transfer:
//0:waiting account
//1:select account from
//2:select account to
//3:enter account to
//4:enter money amount
//5:finish transfer
    AdviceTemp advice;
    String[] accs;
    ArrayList<String> list = new ArrayList<String>();
    String[] accs2;
    String[] send = new String[3];
    InputBuffer inBuffer = new InputBuffer();

    public Transfer(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
    }

    @Override
    void forward(Msg msg) {
        log.info("Transfer Activity: Get Msg: Type: " + msg.getType() + ", Msg: " + msg.getDetails());
        switch (msg.getType()) {

            case ACT_Start:
                // BAMS Transfer
                if (msg.getDetails().equals("Transfer")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait!:F:N", "td");  // Set screen to waiting
                    addQueue(BAMS, "getAcc", "");
//                } else {
//
//                    addQueue(TD_UpdateDisplay, "0:TEMP1:Please Wait.:F", "td");
//                    addQueue(BAMS, "enquiry:" + msg.getDetails().split(",")[1], "");
                }
                break;
            case KP_KeyPressed:
                addQueue(Msg.Type.BZ_ShortBuzz, "", "b");
                if (msg.getDetails().equals("Cancel")) {
                    addQueue(Msg.Type.ACT_Abort, "Eject:End", "");
                } else if (msg.getDetails().equals("Enter") && stage == 3) {
                    stage = 4;
                    String accNo = inBuffer.pop();
                    char[] accNoArray = accNo.toCharArray();
                    String accNoString = "";
                    for (int i = 0; i < accNoArray.length; i++) {
                        if (i % 3 == 0 && i > 0) {
                            accNoString += "-";
                        }
                        accNoString += accNoArray[i];
                    }
                    send[1] = accNoString;
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Transfer money:T:amount", "td");
                } else if (msg.getDetails().equals("Enter") && stage == 4) {
                    stage = 5;
                    send[2] = inBuffer.pop();
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait!:F:N", "td");
                    addQueue(BAMS, "transfer:" + send[0] + ":" + send[1] + ":" + send[2], "");
                    advice = new AdviceTemp("Transfer");
                    advice.setAccount("from：" + send[0] + " to：" + send[1]);
                    advice.setAmount(send[2]);
                } else if (msg.getDetails().equals("Erase")) {
                    inBuffer.clear();
                    // Update Display
                    addQueue(Msg.Type.TD_UpdateDisplay, "1::T:", "td");

                } else {
                    if (msg.getDetails().equals(""))
                        break;
                    if(msg.getDetails().equals("00")){
                        inBuffer.buff('0');
                        inBuffer.buff('0');
                        addQueue(Msg.Type.TD_UpdateDisplay, "1:" + inBuffer.get(), "td");
                        break;
                    }
                    inBuffer.buff(msg.getDetails().toCharArray()[0]);
                    addQueue(Msg.Type.TD_UpdateDisplay, "1:" + inBuffer.get(), "td");
                }
                break;
            case TD_MouseClicked:
//                if (stage == 4)
//                    addQueue(ACT_Abort,"End","");

                if (stage == 1) {
                    stage = 2;
                    send[0] = accs[Integer.valueOf(msg.getDetails())];

                    addQueue(BAMS, "getAcc", "");
                } else if (stage == 2) {
                    if (Integer.valueOf(msg.getDetails()) < accs2.length) {
                        stage = 4;
                        send[1] = accs2[Integer.valueOf(msg.getDetails())];
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Transfer money:T:amount", "td");
                    } else {
                        stage = 3;
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Transfer Account:T:account number", "td");

                    }
                } else if (stage == 5) {
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
                if (reply[0].equals("transfer")) {
                    if(!reply[1].equals("-1")) {
                        stage = 5;
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:" + "TEMP2:" + "Finish transfer:" +
                                "Back to Main Menu:Print Advice and Back:Print Advice and End:End Service:F", "td");
                    }else{
                        stage = 5;
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:" + "TEMP2:" + "Your balance is insufficient:" +
                                "Back to Main Menu:Print Advice and Back:Print Advice and End:End Service:F", "td");
                    }
                } else {
                    // Select Account
                    accs = reply[1].split("/");
                    String accString = "";

                    if (stage == 0) {
                        for (String acc : accs) {
                            accString += ":" + acc;
                        }
                        stage = 1;
                        addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP2:Please select the account transferred from" +
                                accString + ":F", "td");
                    } else if (stage == 2) {

                        for(int i=0; i<accs.length; i++) {
                            if (! accs[i].equals(send[0])) {
                                list.add(accs[i]);
                            }
                        }
                        accs2 = list.toArray(new String[list.size()]);

                        for (String acc : accs2) {
                            accString += ":" + acc;
                        }

                        addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP2:Please select the account transferred to" +
                                accString + ":other account:F", "td");

                    }
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