package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

public class Eject extends Activity {
    Eject(MBox mbox, String id){
        super(mbox, id);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()){
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Please take away your card","td");
                addQueue(Msg.Type.CR_EjectCard,"","cr");
                addQueue(Msg.Type.BZ_LongBuzz,"","b");
                break;
            case CR_CardRemoved:
                addQueue(Msg.Type.BZ_Stop,"","b");
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Thank you!","td");
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
            case CR_TimeOut:
                addQueue(Msg.Type.BZ_Stop,"","b");
//                addQueue(Msg.Type.CR_Retain,"","");
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Time out, card retained!","td");
                addQueue(Msg.Type.ACT_AbortNow,"Retain:End","");
                break;
        }
    }
}
