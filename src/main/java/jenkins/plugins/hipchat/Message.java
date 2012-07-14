package jenkins.plugins.hipchat;

public class Message {

    private String color;
    private String content;

    public Message(String content, String color) {
        this.content = content;
        this.color = color;
    }

    public String getColor() {
        return color;
    }

    public String getContent() {
        return content;
    }

}
