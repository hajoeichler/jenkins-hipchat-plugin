package jenkins.plugins.hipchat;

import hudson.model.Result;
import hudson.model.AbstractBuild;

@SuppressWarnings("rawtypes")
public class MessageBuilder {
    private StringBuffer message;
    private HipChatPublisher hipChatPublisher;
    private AbstractBuild build;

    public MessageBuilder(HipChatPublisher hipChatPublisher, AbstractBuild build) {
        this.hipChatPublisher = hipChatPublisher;
        this.message = new StringBuffer();
        this.build = build;
    }

    private MessageBuilder appendStatusMessage() {
        message.append(getStatusMessage());
        return this;
    }

    private String getStatusMessage() {
        if (build.isBuilding()) {
            return "Starting...";
        }
        Result result = build.getResult();
        if (result == Result.SUCCESS) {
            return "Success";
        }
        if (result == Result.FAILURE) {
            return "<b>FAILURE</b>";
        }
        if (result == Result.ABORTED) {
            return "ABORTED";
        }
        if (result == Result.NOT_BUILT) {
            return "Not built";
        }
        if (result == Result.UNSTABLE) {
            return "Unstable";
        }
        return "Unknown";
    }

    private MessageBuilder startMessage() {
        message.append(build.getProject().getDisplayName());
        message.append(" - ");
        message.append(build.getDisplayName());
        message.append(" ");
        return this;
    }

    private MessageBuilder appendOpenLink() {
        String url = hipChatPublisher.getDescriptor().getBuildServerUrl() + build.getUrl();
        message.append(" (<a href='").append(url).append("'>Open</a>").append(" <a href='").append(url)
                .append("/console").append("'>Console</a>)");
        return this;
    }

    private MessageBuilder appendDuration() {
        message.append(" after ");
        message.append(build.getDurationString());
        return this;
    }

    private String getBuildColor() {
        Result result = build.getResult();
        if (result == Result.SUCCESS) {
            return "green";
        } else if (result == Result.FAILURE) {
            return "red";
        } else if (result == Result.UNSTABLE) {
            return "yellow";
        }
        return "purple";
    }

    public Message getMessage() {
        startMessage();
        appendStatusMessage();
        appendDuration();
        appendOpenLink().toString();

        return new Message(message.toString(), getBuildColor());
    }
}
