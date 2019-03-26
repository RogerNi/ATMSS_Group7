package ATMSS.ATMSS;

import AppKickstarter.misc.Msg;

public class TransMsg {
    Msg msg;
    String destination;

    TransMsg(Msg msg, String destination) {
        this.msg = msg;
        this.destination = destination;
    }

}