package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

import static AppKickstarter.misc.Msg.Type.*;

public class Deposit extends Activity {
    int stage = 0;
    // 0: Waiting account
    // 1: select account
    // 2: input cash
    // 3: show result,select again or certain
    // 4: print ready, select next step

    String[] accs;
    String accFrom = "";
    int amountOfCash = 0;

    AdviceTemp advice;

    int[] moneyIn = new int[3];

    public Deposit(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
        advice = new AdviceTemp("Deposit");
    }

    @Override
    void forward(Msg msg) {

        log.info("Deposit Activity: Get Msg: Type: " + msg.getType() + ", Msg: " + msg.getDetails());
        switch (msg.getType()) {
            case ACT_Start:
                if (msg.getDetails().equals("Deposit")) {
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait!:F:N", "td");  // Set screen to waiting
                    addQueue(BAMS, "getAcc", "");
                    moneyIn[0] = 0;
                    moneyIn[1] = 0;
                    moneyIn[2] = 0;
                }
                break;

            case TD_MouseClicked:
                switch (stage) {
                    case 1:
                        accFrom = accs[Integer.valueOf(msg.getDetails())];
                        advice.setAccount(accFrom);
                        stage = 2;
                        addQueue(TD_UpdateDisplay, "0:TEMP1:Account Number is "+accFrom+"\nPlease Input the money you want to save.:F", "td");
                        addQueue(CDC_Ready,"","cdc");
                        addQueue(Msg.Type.BZ_LongBuzz,"","b");
                        break;
                    case 2:
                        stage = 3;
                        addQueue(TD_UpdateDisplay, "0:TEMP1:Account Number is "+accFrom+"\nPlease Input the money you want to save.:F", "td");
                        addQueue(CDC_Ready,"","cdc");
                        addQueue(Msg.Type.BZ_LongBuzz,"","b");
                        break;
                    case 3:

//
                        switch (msg.getDetails()) {
                            case "0":
                                stage = 2;
                                addQueue(TD_UpdateDisplay, "0:TEMP1:Account Number is "+accFrom+"\nPlease Input the money you want to save.:F", "td");
                                addQueue(CDC_Ready,"","cdc");
                                addQueue(Msg.Type.BZ_LongBuzz,"","b");
                                break;
                            case "1":
                                stage = 4;
                                addQueue(BAMS, "deposit:" + accFrom + ":" + amountOfCash, "");
                                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait.:F:N", "td");
                                advice.setAmount(String.valueOf(amountOfCash));
                                break;

                        }
                        break;
                    case 4:
                        switch (msg.getDetails()) {
                            case "0":
                                addQueue(ACT_Abort, "MainMenu", "");
                                break;
                            case "1":
                                addQueue(ACT_Abort, "Eject:End", "");
                                break;

                        }
                        break;
                }
                break;
            case CDC_CashIn:

                String[] number = msg.getDetails().split(",");
                moneyIn[0] += Integer.valueOf(number[0]);
                moneyIn[1] += Integer.valueOf(number[1]);
                moneyIn[2] += Integer.valueOf(number[2]);
                amountOfCash = moneyIn[0]*1000+moneyIn[1]*500+moneyIn[2]*100;
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please Wait.:F:N", "td");
                break;
            case CDC_Complete:
                stage = 3;
                addQueue(Msg.Type.TD_UpdateDisplay, "0:" + "TEMP3:" + "Saving account is "+accFrom+"\nHKD100 x "+moneyIn[2]+" = "+moneyIn[2]*100
                        +"\nHKD500 x "+moneyIn[1]+" = "+moneyIn[1]*500
                        +"\nHKD1000 x "+moneyIn[0]+" = "+moneyIn[0]*1000
                        +"\nTotal amount = "+amountOfCash
                        +":restore cash:Certain:F", "td");

                addQueue(Msg.Type.BZ_Stop,"","b");
                break;
            case CDC_Invalid:
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please take away invalid money.:F", "td");
                break;
            case CDC_TimeOut:
                if (msg.getDetails().equals("0")) {
                    stage = 3;
                    amountOfCash = moneyIn[0] * 1000 + moneyIn[1] * 500 + moneyIn[2] * 100;
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:" + "TEMP3:" + "Saving account is " + accFrom + "\nHKD100 x " + moneyIn[2] + " = " + moneyIn[2] * 100
                            + "\nHKD500 x " + moneyIn[1] + " = " + moneyIn[1] * 500
                            + "\nHKD1000 x " + moneyIn[0] + " = " + moneyIn[0] * 1000
                            + "\nTotal amount = " + amountOfCash
                            + ":restore cash:Certain:F", "td");
                    addQueue(Msg.Type.BZ_Stop,"","b");
                }else{
                    addQueue(BAMS, "deposit:" + accFrom + ":" + amountOfCash, "");
                    addQueue(Msg.Type.BZ_Stop,"","b");
                    addQueue(ACT_AbortNow, "Retain:End", "");
                }

                break;
            case BAMS:
                String[] reply = msg.getDetails().split(":");
                if (reply[0].equals("deposit")) {

                    addQueue(TD_UpdateDisplay, "0:TEMP3:Please collect your advice:Main Menu:Eject card:F", "td");
                    addQueue(AP_Print, advice.generate(), "ap");

                } else {
                    stage = 1;
                    accs = reply[1].split("/");
                    String accString = "";
                    for (String acc : accs) {
                        accString += ":" + acc;
                    }
                    addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP2:Please select the account deposit to" +
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