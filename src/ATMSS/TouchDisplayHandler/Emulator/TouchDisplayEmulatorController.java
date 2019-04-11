package ATMSS.TouchDisplayHandler.Emulator;

import AppKickstarter.AppKickstarter;
import AppKickstarter.misc.MBox;
import AppKickstarter.misc.Msg;
import AppKickstarter.timer.Timer;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.input.MouseEvent;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;

import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Represents the controller of GUI for touch screen display.
 */
//======================================================================
// TouchDisplayEmulatorController
public class TouchDisplayEmulatorController {
    /**
     * The ID of touch screen display.
     */
    private String id;
    /**
     * In this application, it is an ATMSSStarter.
     */
    private AppKickstarter appKickstarter;
    /**
     * The logger to record the hardware running status
     */
    private Logger log;
    /**
     * The touch screen display emulator.
     */
    private TouchDisplayEmulator touchDisplayEmulator;
    /**
     * The mailbox of touch screen display, used for communication with handler.
     */
    private MBox touchDisplayMBox;
    /**
     * The filename of current FXML file used.
     */
    private String currentFXML;


    /**
     * Font size used for main text.
     */
    private static final double MAIN_TEXT_FONT_SIZE=20.0;
    /**
     * Font size used for input field prefix.
     */
    private static final double INPUT_FIELD_PREFIX_FONT_SIZE=20.0;
    /**
     * Font size used for input field content.
     */
    private static final double INPUT_FIELD_CONTENT_FONT_SIZE=20.0;
    /**
     * Font size used for button text.
     */
    private static final double BUTTON_TEXT_FONT_SIZE=20.0;


    /**
     * The main text
     */
    @FXML Text mainText;
    /**
     * The input filed prefix text
     */
    @FXML Text inputFieldPrefix;
    /**
     * The input field content text.
     */
    @FXML Text inputFieldContent;
    /**
     * The underline of input field content, to mimic the real ATM.
     */
    @FXML Line inputFieldUnderline;
    /**
     * The text of button 0, which is the left top button in template 2 or left button in template 3.
     */
    @FXML Text button0Text;
    /**
     * The text of button 1, which is the left middle button in template 2 or right button in template 3.
     */
    @FXML Text button1Text;
    /**
     * The text of button 2, which is the left bottom button in template 2.
     */
    @FXML Text button2Text;
    /**
     * The text of button 3, which is the right top button in template 2.
     */
    @FXML Text button3Text;
    /**
     * The text of button 4, which is the right middle button in template 2.
     */
    @FXML Text button4Text;
    /**
     * The text of button 5, which is the right bottom button in template 2.
     */
    @FXML Text button5Text;



    /**
     * Initial the controller.
     * @param id The ID of touch screen display.
     * @param appKickstarter In this application, it is an ATMSSStarter.
     * @param log The logger to record the hardware running status.
     * @param touchDisplayEmulator The touch screen display emulator.
     */
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
	this.inputFieldContent.setFont(new Font(INPUT_FIELD_CONTENT_FONT_SIZE));
	this.button0Text.setVisible(false);
	this.button1Text.setVisible(false);
	this.button2Text.setVisible(false);
    this.button3Text.setVisible(false);
    this.button4Text.setVisible(false);
    this.button5Text.setVisible(false);
    this.inputFieldPrefix.setVisible(false);
	this.inputFieldUnderline.setVisible(false);
	this.inputFieldContent.setVisible(false);
    this.button0Text.setFont(new Font(this.BUTTON_TEXT_FONT_SIZE));
    this.button1Text.setFont(new Font(this.BUTTON_TEXT_FONT_SIZE));
    this.button2Text.setFont(new Font(this.BUTTON_TEXT_FONT_SIZE));
    this.button3Text.setFont(new Font(this.BUTTON_TEXT_FONT_SIZE));
    this.button4Text.setFont(new Font(this.BUTTON_TEXT_FONT_SIZE));
    this.button5Text.setFont(new Font(this.BUTTON_TEXT_FONT_SIZE));
    this.currentFXML="TouchDisplayEmulator.fxml";
        //this.test();
        this.log.setLevel(Level.FINER);
    } // initialize


    /**
     * Handle the mouse clicking events on touch screen display emulator GUI.
     * @param mouseEvent The event to be handled
     */
    //------------------------------------------------------------
    // td_mouseClick
    public void td_mouseClick(MouseEvent mouseEvent) {
        int x = (int) mouseEvent.getX();
	int y = (int) mouseEvent.getY();

	//Here, my teammate instructed me to let it timeout as long as current page has button and
    //user doesn't click any button and screen doesn't receive update display message. If user click the screen but not
    // on any button, timeout will still happen.


    switch (currentFXML)
    {

        case "TouchDisplayMainMenu.fxml":
            if(x>0 && x<300)
            {
                if(y>270 && y<340)
                {
                    //Button 0 is clicked
                    //Stop timer and start again.
                    this.touchDisplayEmulator.resetTimer();
                    //Tell ATMSS which button is clicked
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "0"));
                }else if(y>340 && y<410)
                {
                    //Button 1 is clicked
                    //Stop timer and start again.
                    this.touchDisplayEmulator.resetTimer();
                    //Tell ATMSS which button is clicked
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "1"));
                }else if(y>410 && y<480)
                {
                    //Button 2 is clicked
                    //Stop timer and start again.
                    this.touchDisplayEmulator.resetTimer();
                    //Tell ATMSS which button is clicked
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "2"));
                }
            }else if(x>340 && x<640)
            {
                if(y>270 && y<340)
                {
                    //Button 3 is clicked
                    //Stop timer and start again.
                    this.touchDisplayEmulator.resetTimer();
                    //Tell ATMSS which button is clicked
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "3"));
                }else if(y>340 && y<410)
                {
                    //Button 4 is clicked
                    //Stop timer and start again.
                    this.touchDisplayEmulator.resetTimer();
                    //Tell ATMSS which button is clicked
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "4"));
                }else if(y>410 && y<480)
                {
                    //Button 5 is clicked
                    //Stop timer and start again.
                    this.touchDisplayEmulator.resetTimer();
                    //Tell ATMSS which button is clicked
                    touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "5"));
                }
            }
            break;
        case "TouchDisplayConfirmation.fxml":
            if(x>100 && x<260 && y>390 && y<430)
            {
                //Button 0 is clicked
                //Stop timer and start again.
                this.touchDisplayEmulator.resetTimer();
                //Tell ATMSS which button is clicked
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "0"));
            }else if(x>380 && x<540 && y>390 && y<430)
            {
                //Button 1 is clicked
                //Stop timer and start again.
                this.touchDisplayEmulator.resetTimer();
                //Tell ATMSS which button is clicked
                touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, "1"));
            }
    }

	log.fine(id + ": mouse clicked: -- (" + x + ", " + y + ")");
	//touchDisplayMBox.send(new Msg(id, touchDisplayMBox, Msg.Type.TD_MouseClicked, x + " " + y));
    } // td_mouseClick


    /**
     * Used merely for testing purpose. Currently, none will call it.
     */
    private void test()
    {
        this.mainText.setText("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa" +
                "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");
        this.inputFieldPrefix.setText("re-enter new PIN:");
        String inputPrefix=this.inputFieldPrefix.getText();
//        this.button0Text.setText("button0");
//        //To make it nicer, I add 20 more pixels at x-direction
//        double startXOfLine=220.0+this.pixelsPerChar(this.INPUT_FIELD_PREFIX_FONT_SIZE)*inputPrefix.length();
//        System.out.println("inputPrefix.length(): "+inputPrefix.length());
//        System.out.println("startXOfLine:"+startXOfLine);
//        this.inputFieldUnderline.setStartX(startXOfLine);
//        this.inputFieldUnderline.setEndX(startXOfLine+this.INPUT_FIELD_UNDERLINE_LENGTH);
        this.inputFieldPrefix.setVisible(true);
        this.inputFieldUnderline.setVisible(true);
        this.button0Text.setVisible(true);
        this.button1Text.setVisible(true);
    }

    /**
     * Set the content of the main text on the GUI
     * @param mainT The content of main text.
     */
    public void setMainText(String mainT)
    {
        this.mainText.setText(mainT);
        this.mainText.setVisible(true);
        this.log.fine(id+": Set main text to be: "+this.mainText.getText());
    }

    /**
     * Set the texts of 6 buttons in template 2.
     * @param buttonsText A string array with 1 to 6 elements, which are the texts on the buttons in order. See
     * {@link TouchDisplayEmulatorController#button0Text}, {@link TouchDisplayEmulatorController#button1Text},
     * {@link TouchDisplayEmulatorController#button2Text}, {@link TouchDisplayEmulatorController#button3Text},
     *  {@link TouchDisplayEmulatorController#button4Text}, {@link TouchDisplayEmulatorController#button5Text}, for the
     *  positions of buttons.
     */
    public void set6ButtonsText(String[] buttonsText)
    {
        int len=buttonsText.length;
        if(len<=0)
        {
            return;
        }
        this.button0Text.setText(buttonsText[0]);
        this.button0Text.setVisible(true);
        this.log.fine(id+": Set the text of button 0 to be: "+this.button0Text.getText());
        len--;
        if(len<=0)
        {
            return;
        }
        this.button1Text.setText(buttonsText[1]);
        this.button1Text.setVisible(true);
        this.log.fine(id+": Set the text of button 1 to be: "+this.button1Text.getText());
        len--;
        if(len<=0)
        {
            return;
        }
        this.button2Text.setText(buttonsText[2]);
        this.button2Text.setVisible(true);
        this.log.fine(id+": Set the text of button 2 to be: "+this.button2Text.getText());
        len--;
        if(len<=0)
        {
            return;
        }
        this.button3Text.setText(buttonsText[3]);
        this.button3Text.setVisible(true);
        this.log.fine(id+": Set the text of button 3 to be: "+this.button3Text.getText());
        len--;
        if(len<=0)
        {
            return;
        }
        this.button4Text.setText(buttonsText[4]);
        this.button4Text.setVisible(true);
        this.log.fine(id+": Set the text of button 4 to be: "+this.button4Text.getText());
        len--;
        if(len<=0)
        {
            return;
        }
        this.button5Text.setText(buttonsText[5]);
        this.button5Text.setVisible(true);
        this.log.fine(id+": Set the text of button 5 to be: "+this.button5Text.getText());
    }

    /**
     * Set the texts of 2 buttons in template 3.
     * @param buttonsText A string array with exact 2 elements, which are the texts on the buttons in order.
     *                    {@link TouchDisplayEmulatorController#button0Text}, {@link TouchDisplayEmulatorController#button1Text}
     *                     for the positions of buttons.
     */
    public void set2ButtonsText(String[] buttonsText)
    {
        this.button0Text.setText(buttonsText[0]);
        this.button0Text.setVisible(true);
        this.log.fine(id+": Set the text of button 0 to be: "+this.button0Text.getText());
        this.button1Text.setText(buttonsText[1]);
        this.button1Text.setVisible(true);
        this.log.fine(id+": Set the text of button 1 to be: "+this.button1Text.getText());
    }

    /**
     * Set the prefix of input field.
     * @param prefix The prefix of input field
     */
    public void setInputFieldPrefixAndUnderline(String prefix)
    {
        //According to our protocol, the ATMSS won't give me a colon, I have to add it here.
        this.inputFieldPrefix.setText(prefix+":");
        this.inputFieldPrefix.setVisible(true);
        this.log.fine(id+": Set input field prefix to be: "+this.inputFieldPrefix.getText());
        this.inputFieldUnderline.setVisible(true);
        this.log.fine(id+": Display input field underline");
    }

    /**
     * Set the content of input field.
     * @param content The content of input field.
     */
    //Other method must be called by reloadStage method, and that part of reloadStage has been surrounded by
    //runLater block, only this method need to be treated specially: add runLater in this method.
    public void setInputFieldContent(String content)
    {
        Platform.runLater(new Runnable() {
            @Override
            public void run() {
                try {
                    inputFieldContent.setText(content);
                    inputFieldContent.setVisible(true);
                    log.fine(id+": Set input field content to be: "+inputFieldContent.getText());
                } catch (Exception e) {
                    log.severe(id + ": failed to load ");
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Inform the controller about the filename of FXML file currently used.
     * @param nameOfFXML Filename of current FXML file.
     */
    //Used by TouchScreenEmulator to tell this controller what fxml file I am using
    public void setCurrentPage(String nameOfFXML)
    {
        this.currentFXML=nameOfFXML;
    }




} // TouchDisplayEmulatorController
