package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

public class EjectCard extends Activity {
    EjectCard(MBox mbox, String id){
        super(mbox, id);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()){
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay,"","td");
                addQueue(Msg.Type.CR_EjectCard,"","");
                addQueue(Msg.Type.BZ_LongBuzz,"","b");
                break;
            case CR_CardRemoved:
                addQueue(Msg.Type.BZ_Stop,"","b");
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
            case CR_TimeOut:
                addQueue(Msg.Type.BZ_Stop,"","");
                addQueue(Msg.Type.CR_Retain,"","");
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
        }
    }
}
