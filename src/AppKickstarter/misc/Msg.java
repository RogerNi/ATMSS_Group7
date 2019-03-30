package AppKickstarter.misc;


//======================================================================
// Msg
public class Msg {
    private String sender;
    private MBox senderMBox;
    private Type type;
    private String details;

    //------------------------------------------------------------
    // Msg
    public Msg(String sender, MBox senderMBox, Type type, String details) {
        this.sender = sender;
        this.senderMBox = senderMBox;
        this.type = type;
        this.details = details;
    } // Msg


    //------------------------------------------------------------
    // getters
    public String getSender() {
        return sender;
    }

    public MBox getSenderMBox() {
        return senderMBox;
    }

    public Type getType() {
        return type;
    }

    public String getDetails() {
        return details;
    }


    //------------------------------------------------------------
    // toString
    public String toString() {
        return sender + " (" + type + ") -- " + details;
    } // toString


    //------------------------------------------------------------
    // Msg Types
    public enum Type {
        Terminate,        // Terminate the running thread
        SetTimer,        // Set a timer
        CancelTimer,        // Set a timer
        Tick,            // Timer clock ticks
        TimesUp,        // Time's up for the timer
        Poll,            // Health poll
        PollAck,        // Health poll acknowledgement
        HW_Shutdown,    // Hardware Shutdown

        TD_UpdateDisplay,    // Update Display
        TD_MouseClicked,    // Mouse Clicked
        TD_TimeOut,
        CR_CardInserted,    // Card inserted
        CR_CardRemoved,        // Card removed
        CR_EjectCard,        // Eject card
        //        CR_CardError,       // Cannot read card info
        CR_MachError,       // Mechanical Error
        CR_Retain,          // Retain Card
        CR_Info,            // Card Info    Detail: Card Info.
        CR_TimeOut,
        KP_KeyPressed,        // Key pressed
        AP_Print,           // Print something
        AP_PrintStarted,    // Print start
        AP_PrintCompleted,  // Print complete
        AP_OutOfPaper,
        AP_Jam,
        BZ_ShortBuzz,
        BZ_LongBuzz,
        BZ_Stop,
        CDC_Ready,
        CDC_TimeOut,
        CDC_CashIn,
        CDC_Complete,       // All invalid cash taken away
        CD_CashOut,         // Amount of money
        CD_CashAmountLeft,        //
        CD_Complete,
        CD_TimeOut,
        CD_Insufficient,
        ACT_Start,          // Activity Start
        ACT_Abort,          // Activity Stop
        ACT_AbortNow,       // Directly Abort, delete Queue
        ACT_CRED,           // Change Cred
        ACT_SUBENDS,
        BAMS,               // BAMS Message
    } // Type
} // Msg
