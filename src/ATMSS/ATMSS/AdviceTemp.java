package ATMSS.ATMSS;

public class AdviceTemp {
    String content = "";
    String type;
    String account;
    String amount;
    public AdviceTemp(String type){
        content += "Bank of Group_7\n\nAdvice\n";
        this.type = type;
    }
    public void setAccount(String account){
        this.account = account;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    public String generate(){
        content += "Account\t"+account+"\nOperation\t"+type+"\nAmount\t"+amount;
        content += "\nThank you for using our ATM service.\nSee you next time!";
        return content;
    }
}
