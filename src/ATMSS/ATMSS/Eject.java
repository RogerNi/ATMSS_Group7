package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;

import java.util.logging.Logger;

public class Eject extends Activity {
     public Eject(MBox mbox, String id, Logger log){
        super(mbox, id, log);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()){
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Please take away your card:F","td");
                addQueue(Msg.Type.CR_EjectCard,"","cr");
                addQueue(Msg.Type.BZ_LongBuzz,"","b");
                break;
            case CR_CardRemoved:
                log.info("Eject Activity: Card Removed");
                addQueue(Msg.Type.BZ_Stop,"","b");
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
            case CR_TimeOut:
                addQueue(Msg.Type.BZ_Stop,"","b");
//                addQueue(Msg.Type.CR_Retain,"","");
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Time out, card retained!:F","td");
                addQueue(Msg.Type.ACT_AbortNow,"Retain:End","");
                break;
        }
    }
}
