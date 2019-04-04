package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.LinkedList;
import java.util.Queue;
import java.util.logging.Logger;

public abstract class Activity {

    MBox masterMBox;
    String masterId;
    Logger log;
//    Activity sub = null;

    public Activity(MBox mMbox, String mId, Logger log){
        msgQueue = new LinkedList<>();
        masterId = mId;
        masterMBox = mMbox;
        this.log = log;
    }

    Queue<TransMsg> msgQueue;

//    void process(Msg msg){
//        if(sub==null)
//            forward(msg);
//        else{
//            sub.forward(msg);
//            TransMsg transMsg = sub.collect();
//            while (transMsg != null) {
//                if(transMsg.msg.getType() == Msg.Type.ACT_Abort){
//                    sub = null;
//                    if (transMsg.msg.getDetails().equals("End"))
//                        process(new Msg(masterId,masterMBox, Msg.Type.ACT_SUBENDS,"0"));
//                    else
//                        process(new Msg(masterId,masterMBox, Msg.Type.ACT_SUBENDS,"1"));
//                    return;
//                }
//                msgQueue.add(transMsg);
//                transMsg = sub.collect();
//            }
//        }
//    }

    abstract void forward(Msg msg);

    TransMsg collect(){
        return msgQueue.poll();
    }

    void addQueue(Msg.Type type, String details, String receiver){
        msgQueue.add(new TransMsg(new Msg(masterId, masterMBox, type, details),receiver));
    }
//
//    void runSubActivity(Activity activity){
//        sub = activity;
//        process(new Msg(masterId,masterMBox, Msg.Type.ACT_Start,""));
//    }
}
