package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

public class PrintAdvice extends Activity {

    public PrintAdvice(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
    }

    @Override
    void forward(Msg msg) {
        log.info("Print Advice: Get Msg: Type: "+msg.getType()+" Msg: "+msg.getDetails());
        switch (msg.getType()) {
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Waiting for advice printing:F", "td");
                addQueue(Msg.Type.AP_Print, msg.getDetails(), "ap");
                break;
            case AP_PrintCompleted:
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Please take away your advice!:F", "td");
                addQueue(Msg.Type.BZ_LongBuzz, "", "b");
                break;
            case AP_AdviceTaken:
                addQueue(Msg.Type.BZ_Stop, "", "b");
                addQueue(Msg.Type.ACT_Abort, "", "");
                break;
            case AP_Jam:
                addQueue(Msg.Type.TD_UpdateDisplay, "0:TEMP1:Sorry!\nSomething wrong with Advice Printer.\n" +
                        "We cannot provide you the advice.\n For more information, please contact with the bank", "td");
                addQueue(Msg.Type.BZ_Stop,"","b");
                addQueue(Msg.Type.ACT_Abort,"","");
                break;
            case AP_TimesUp:
                addQueue(Msg.Type.ACT_AbortNow,"Retain:End","");
                break;
        }
    }
}
