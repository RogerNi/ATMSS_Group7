package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

public class Retain extends Activity {
    public Retain(MBox mbox, String id){
        super(mbox, id);
    }

    @Override
    void forward(Msg msg) {
        switch (msg.getType()){
            case ACT_Start:
                addQueue(Msg.Type.TD_UpdateDisplay,"0:TEMP1:Card Retained." +
                        "\nPlease Contact the bank for more information.","td");
                addQueue(Msg.Type.CR_Retain,"","cr");
                addQueue(Msg.Type.ACT_Abort,"End","");
                break;
        }
    }

}
