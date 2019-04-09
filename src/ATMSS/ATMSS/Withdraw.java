package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import static AppKickstarter.misc.Msg.Type.*;

public class Withdraw extends Activity {
    int stage = 0;
    // 0: Waiting account
    // 1: select account
    // 2: input amount of cash
    // 3: waiting response from server
    // 4：Insufficient Fund
    // 5: Prepare Cash
    // 6: Cash ready, select next step

    String[] accs;
    String accFrom = "";
    int amountOfCash = 0;
    InputBuffer inputBuffer;
    AdviceTemp advice;

    int [] amountLeft = new int[2];

    public Withdraw(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
        inputBuffer = new InputBuffer();
        advice = new AdviceTemp("Withdraw");
    }

    private boolean amountLegal(int amount){
        if (amount%100 != 0)
            return false;
        int left = amount;
        int cash500 = amountLeft[0];
        int cash100 = amountLeft[1];
        while(left >= 500 && cash500 > 0){
            left = left % 500;
            cash500 --;
        }
        if(left > cash100*100)
            return false;
        return true;
    }

    @Override
    void forward(Msg msg) {
        log.info("Withdraw Activity: Get Msg: Type: " + msg.getType() + ", Msg: " + msg.getDetails());
        switch (msg.getType()) {
            case ACT_Start:
                if (msg.getDetails().equals("Withdraw")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait!:F", "td");  // Set screen to waiting
                    addQueue(CD_CashAmountLeft,"","cd");
                    addQueue(BAMS, "getAcc", "");
                }
                break;
            case KP_KeyPressed:
                addQueue(BZ_ShortBuzz,"","b");
                if(stage>=3)
                    break;
                if (stage!=2&&msg.getDetails().equals("Cancel")){
                    addQueue(ACT_AbortNow,"Eject:End","");
                    break;
                }
                if(stage==2){
                    switch (msg.getDetails()){
                        case "Cancel":
                            addQueue(ACT_AbortNow,"Eject:End","");
                            break;
                        case "Erase":
                            inputBuffer.clear();
                            // Update Display
                            addQueue(Msg.Type.TD_UpdateDisplay, "1:", "td");
                            break;
                        case "Enter":
                            amountOfCash = Integer.valueOf(inputBuffer.pop());
                            if (!amountLegal(amountOfCash)){
                                addQueue(TD_UpdateDisplay,"0:TEMP1:Sorry, we cannot provide you this amount of cash!:F","td");
                                addQueue(TD_UpdateDisplay, "0:TEMP2:Input amount of money:100:300:500:1000:T:Cash", "td");
                                break;
                            }
                            addQueue(TD_UpdateDisplay, "0:TEMP1:Waiting!:F", "td");
                            addQueue(BAMS, "withdraw:" + accFrom + ":" + amountOfCash, "");
                            advice.setAmount(String.valueOf(amountOfCash));
                            stage = 3;
                            break;
                        default:
                            if(msg.getDetails().equals(".") || msg.getDetails().equals(""))
                                break;
                            if(msg.getDetails().equals("00")){
                                inputBuffer.buff('0');
                                inputBuffer.buff('0');
                            } else {
                                inputBuffer.buff(msg.getDetails().toCharArray()[0]);
                            }
                            addQueue(TD_UpdateDisplay,"1:"+inputBuffer.get(),"td");
                            break;
                    }
                }
                break;
            case CD_CashAmountLeft:
                String [] a = msg.getDetails().split(",");
                amountLeft[0] = Integer.valueOf(a[0]);
                amountLeft[1] = Integer.valueOf(a[1]);
                break;
            case TD_MouseClicked:
                switch (stage) {
                    case 1:
                        accFrom = accs[Integer.valueOf(msg.getDetails())];
                        advice.setAccount(accFrom);
                        stage = 2;
                        addQueue(TD_UpdateDisplay, "0:TEMP2:Input amount of money:100:300:500:1000:T:Cash", "td");
                        break;
                    case 2:

                        int[] amounts = {100, 300, 500, 1000};
                        amountOfCash = amounts[Integer.valueOf(msg.getDetails())];
                        if (!amountLegal(amountOfCash)){
                            addQueue(TD_UpdateDisplay,"0:TEMP1:Sorry, we cannot provide you this amount of cash!:F","td");
                            addQueue(TD_UpdateDisplay, "0:TEMP2:Input amount of money:100:300:500:1000:T:Cash", "td");
                            break;
                        }
                        stage = 3;
                        advice.setAmount(String.valueOf(amountOfCash));
                        addQueue(TD_UpdateDisplay, "0:TEMP1:Waiting!:F", "td");
                        addQueue(BAMS, "withdraw:" + accFrom + ":" + amountOfCash, "");
                        break;
                    case 4:
                        // Fail Withdraw
                        break;
                    case 6:
                        // Suc Withdraw
                        switch (msg.getDetails()) {
                            case "0":
                                addQueue(ACT_Abort, "Eject:CashOut:End", "");
                                break;
                            case "1":
                                addQueue(ACT_Abort, "CheckBalance,"+accFrom+":Eject:CashOut:End", "");
                                break;
                            case "2":
                                addQueue(ACT_Abort, "PrintAdvice,"+advice.generate()+":Eject:CashOut:End", "");
                                break;
                            case "3":
                                addQueue(ACT_Abort, "CheckBalance,"+accFrom+":PrintAdvice,"+advice.generate()+":Eject:CashOut:End", "");
                                break;
                        }
                        break;
                }
                break;
            case CD_CashPrepared:
                stage = 6;
                addQueue(Msg.Type.TD_UpdateDisplay, "0:" + "TEMP2:" + "Select your next steps:" +
                        "Eject Card:Check Balance and Eject card:Print Advice and Eject Card:Check Balance, Print Advice\nand Eject Card:F", "td");
                break;
            case BAMS:
                String[] reply = msg.getDetails().split(":");
                if (reply[0].equals("withdraw")) {
                    if (reply[1].equals("-1")) {
                        stage = 4;
                        addQueue(TD_UpdateDisplay, "0:TEMP1:Insufficient Fund\nPlease Take your card.:F", "td");
                        advice.setAmount("Insufficient Fund!");
                        addQueue(ACT_Abort, "PrintAdvice,"+advice.generate()+":End", "");
                    } else {
                        stage = 5;
                        addQueue(TD_UpdateDisplay,"0:TEMP1:Please Wait.:F","td");
                        addQueue(CD_CashPrepare,String.valueOf(amountOfCash),"cd");
                    }
                } else {
                    stage = 1;
                    accs = reply[1].split("/");
                    String accString = "";
                    for (String acc : accs) {
                        accString += ":" + acc;
                    }
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP2:Please select the account withdraw from" +
                            accString + ":F", "td");
                }
                break;
        }
    }
}
