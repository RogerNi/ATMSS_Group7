package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.logging.Logger;

public class Withdraw extends Activity {
    int stage = 0;
    // 0: Waiting account
    // 1: select account
    // 2: input amount of cash
    // 3: waiting response from server
    // 4：Insufficient Fund
    // 5: Prepare Cash
    // 6: Cash ready, select next step

    public Withdraw(MBox mMbox, String mId, Logger log) {
        super(mMbox, mId, log);
    }

    @Override
    void forward(Msg msg) {

    }
}
