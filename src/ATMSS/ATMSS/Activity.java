package ATMSS.ATMSS;

import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;

import java.util.LinkedList;
import java.util.Queue;

public abstract class Activity {

    MBox masterMBox;
    String masterId;
    Activity(MBox mMbox, String mId){
        msgQueue = new LinkedList<>();
        masterId = mId;
        masterMBox = mMbox;
    }

    Queue<TransMsg> msgQueue;

    abstract void forward(Msg msg);

    TransMsg collect(){
        return msgQueue.poll();
    }

    void addQueue(Msg.Type type, String details, String receiver){
        msgQueue.add(new TransMsg(new Msg(masterId, masterMBox, type, details),receiver));
    }
}
