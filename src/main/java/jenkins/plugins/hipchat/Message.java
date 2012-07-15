package jenkins.plugins.hipchat;

public class Message {

    private final String color;
    private final String content;
    private final boolean notify;

    public Message(String content, String color, boolean notify) {
        this.content = content;
        this.color = color;
        this.notify = notify;
    }

    public String getColor() {
        return color;
    }

    public String getContent() {
        return content;
    }

    public String getNotify() {
        if (notify) {
            return "1";
        }
        return "0";
    }
}
