package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.util.logging.Logger;


//======================================================================
// TouchDisplayEmulatorController
public class TouchDisplayEmulatorController {
    private String id;
    private AppKickstarter appKickstarter;
    private Logger log;
    private TouchDisplayEmulator touchDisplayEmulator;
    private MBox touchDisplayMBox;

    private static final double MAIN_TEXT_FONT_SIZE=20.0;
    private static final double INPUT_FIELD_PREFIX_FONT_SIZE=20.0;


    @FXML Text mainText;
    @FXML Text inputFieldPrefix;
    @FXML Line inputFieldUnderline;


    //------------------------------------------------------------
    // initialize
    public void initialize(String id, AppKickstarter appKickstarter, Logger log, TouchDisplayEmulator touchDisplayEmulator) {
        this.id = id;
        this.appKickstarter = appKickstarter;
        this.log = log;
        this.touchDisplayEmulator = touchDisplayEmulator;
        this.touchDisplayMBox = appKickstarter.getThread("TouchDisplayHandler").getMBox();
        this.mainText.setFont(new Font(MAIN_TEXT_FONT_SIZE));
        this.mainText.setTextAlignment(TextAlignment.CENTER);
        this.inputFieldPrefix.setFont(new Font(INPUT_FIELD_PREFIX_FONT_SIZE));
        this.inputFieldPrefix.setVisible(false);
        this.inputFieldUnderline.setVisible(false);
        this.test();
    } // initialize


    //------------------------------------------------------------
    // td_mouseClick
    public void td_mouseClick(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
        int y = (int) mouseEvent.getY();

        log.fine(id + ": mouse clicked: -- (" + x + ", " + y + ")");
        touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
    } // td_mouseClick


    private void test()
    {
        this.mainText.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        this.inputFieldPrefix.setText("re-enter new PIN:");
        String inputPrefix=this.inputFieldPrefix.getText();
//        //To make it nicer, I add 20 more pixels at x-direction
//        double startXOfLine=220.0+this.pixelsPerChar(this.INPUT_FIELD_PREFIX_FONT_SIZE)*inputPrefix.length();
//        System.out.println("inputPrefix.length(): "+inputPrefix.length());
//        System.out.println("startXOfLine:"+startXOfLine);
//        this.inputFieldUnderline.setStartX(startXOfLine);
//        this.inputFieldUnderline.setEndX(startXOfLine+this.INPUT_FIELD_UNDERLINE_LENGTH);
        this.inputFieldPrefix.setVisible(true);
        this.inputFieldUnderline.setVisible(true);
    }

    /*//Return the number of pixels per character.
    private double pixelsPerChar(double fontSize)
    {
        //After read table about font size to pixel conversion,
        //I feel that this formula is a satisfactory approximation.
        //We only need this to approximately determine where should I
        //display the line after the input field prefix.
        return (double) Math.round(fontSize*1.33);
    }*/


} // TouchDisplayEmulatorController
