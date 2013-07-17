package jenkins.plugins.hipchat;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.BuildListener;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Notifier;
import hudson.tasks.Publisher;

import java.util.logging.Logger;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Hajo Eichler
 */
@SuppressWarnings("rawtypes")
public class HipChatPublisher extends Notifier {

    static Logger logger = Logger.getLogger(HipChatPublisher.class.getSimpleName());

    public String room;

    @DataBoundConstructor
    public HipChatPublisher(String room) {
        this.room = room;
    }

    public String getRoom() {
        return room;
    }

    @Override
    public boolean needsToRunAfterFinalized() {
        return true;
    }

    @Override
    public boolean perform(AbstractBuild<?, ?> build, Launcher launcher, BuildListener listener) {
        Result result = build.getResult();
        Result previousResult = Result.NOT_BUILT;
        try {
            previousResult = build.getPreviousBuild().getResult();
        } catch (Exception e) { // ignore
        }
        if (getDescriptor().getOnlyOnStateChanges()) {
            if (result != previousResult) {
                sendMessage(build);
            }
        } else {
            if (result == Result.SUCCESS && getDescriptor().getOnSuccess()) {
                sendMessage(build);
            } else if (result != Result.SUCCESS) {
                sendMessage(build);
            }
        }
        return true;
    }

    private void sendMessage(AbstractBuild<?, ?> build) {
        HipChatClient hipChatClient = new HipChatClient();
        hipChatClient.publish(this, new MessageBuilder(this, build).getMessage());
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String token;
        private String room;
        private String buildServerUrl;
        private String sendAs;
        private boolean onlyOnStateChanges;
        private boolean onSuccess;
        private boolean notifySuccess;

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "HipChat Notifications";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            token = formData.getString("token");
            room = formData.getString("room");
            buildServerUrl = formData.getString("buildServerUrl");
            sendAs = formData.getString("sendAs");
            onlyOnStateChanges = formData.getBoolean("onlyOnStateChanges");
            onSuccess = formData.getBoolean("onSuccess");
            notifySuccess = formData.getBoolean("notifySuccess");
            save();
            return super.configure(req, formData);
        }

        public String getToken() {
            return token;
        }

        public String getRoom() {
            return room;
        }

        public String getBuildServerUrl() {
            return buildServerUrl;
        }

        public String getSendAs() {
            return sendAs;
        }

        public boolean getOnSuccess() {
            return onSuccess;
        }

        public boolean getOnlyOnStateChanges() {
            return onlyOnStateChanges;
        }

        public boolean getNotifySuccess() {
            return notifySuccess;
        }

    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}
