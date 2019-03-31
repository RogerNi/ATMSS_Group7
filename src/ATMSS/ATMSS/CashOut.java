package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import com.sun.javaws.ui.UpdateDialog;

public class CashOut extends Activity {

    public  CashOut(MBox mBox, String id) {
        super(mBox, id);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()){
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Please take away the cash.","td");
                addQueue(Msg.Type.CD_CashOut,"","cd");
                break;
            case CD_Complete:
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
            case CD_TimeOut:
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Time Out\nCash Retained","td");
                addQueue(Msg.Type.ACT_AbortNow,"End","");
                break;
        }
    }
}
