package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

public class Transfer extends Activity {
    public Transfer(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
    }

    @Override
    void forward(Msg msg) {

    }
}
