package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import com.sun.javaws.ui.UpdateDialog;

import java.util.logging.Logger;

public class CashOut extends Activity {

    public  CashOut(MBox mBox, String id, Logger log) {
        super(mBox, id,log);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()){
            case ACT_Start:
                addQueue(Msg.Type.BZ_LongBuzz,"","b");
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Please take away the cash.:F","td");
                addQueue(Msg.Type.CD_CashOut,"","cd");
                break;
            case CD_Complete:
                addQueue(Msg.Type.BZ_Stop,"","b");
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
            case CD_TimeOut:
                addQueue(Msg.Type.BZ_Stop,"","b");
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Time Out\nCash Retained:F","td");
                addQueue(Msg.Type.ACT_AbortNow,"End","");
                break;
        }
    }
}
