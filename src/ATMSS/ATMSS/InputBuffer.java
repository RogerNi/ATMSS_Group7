package ATMSS.ATMSS;

public class InputBuffer {
    private String buffer = "";

    public void buff(char input) {
        buffer += input;
    }

    public String pop() {
        String out = buffer;
        buffer = "";
        return out;
    }

    public void deleteLast() {
        if (buffer.length() == 0)
            return;
        buffer = buffer.substring(0, buffer.length() - 1);
    }

    public int getLength() {
        return buffer.length();
    }

    public void clear(){
        buffer = "";
    }

}