package ATMSS.BuzzerHandler;

import ATMSS.ATMSSStarter;
import AppKickstarter.misc.*;

import javax.sound.sampled.*;
import java.io.File;
import java.io.IOException;
import java.util.Scanner;

//======================================================================
// BuzzerHandler
public class BuzzerHandler extends AppThread {
    private Clip clip = null;
    public BuzzerHandler(String id, ATMSSStarter atmssStarter) {

        super(id, atmssStarter);
        this.clip = clip;
    } // BuzzerHandler

    public void Playsound(String file_path) {

        try {
            // Open an audio input stream.
            File soundFile = new File(file_path);
            AudioInputStream audioIn = AudioSystem.getAudioInputStream(soundFile);
            // Get a sound clip resource.
            clip = AudioSystem.getClip();
            // Open audio clip and load samples from the audio input stream.
            clip.open(audioIn);
            clip.start();
        } catch (UnsupportedAudioFileException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (LineUnavailableException e) {
            e.printStackTrace();
        }
    }

    //------------------------------------------------------------
    // run

    public void run() {
        MBox atmss = appKickstarter.getThread("ATMSS").getMBox();
        log.info(id + ": starting...");

        for (boolean quit = false; !quit; ) {
            Msg msg = mbox.receive();

            log.fine(id + ": message received: [" + msg + "].");
            switch (msg.getType()) {
                case BZ_ShortBuzz:
                    Playsound("sound/short.wav");
                    break;

                case BZ_LongBuzz:
                    Playsound("sound/long.wav");
                    break;

                case BZ_Stop:
                    if(clip==null){
                        log.warning(id + ": unknown message type: [" + msg + "]");
                    }else{
                        clip.close();
                    }
                    break;

                case Poll:
                    atmss.send(new Msg(id, mbox, Msg.Type.PollAck, id + " is up!"));
                    break;

                case Terminate:
                    quit = true;
                    break;

                default:
                    log.warning(id + ": unknown message type: [" + msg + "]");
            }
            // declaring our departure
            appKickstarter.unregThread(this);
            log.info(id + ": terminating...");
        }
    }// run
}// BuzzerHandler