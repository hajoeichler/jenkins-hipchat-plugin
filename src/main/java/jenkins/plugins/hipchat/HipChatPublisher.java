package jenkins.plugins.hipchat;

import hudson.Extension;
import hudson.Launcher;
import hudson.model.Build;
import hudson.model.BuildListener;
import hudson.model.AbstractProject;
import hudson.tasks.BuildStepDescriptor;
import hudson.tasks.BuildStepMonitor;
import hudson.tasks.Publisher;
import hudson.tasks.Recorder;
import hudson.util.FormValidation;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;

import net.sf.json.JSONObject;

import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

/**
 * @author Hajo Eichler
 */
@SuppressWarnings("rawtypes")
public class HipChatPublisher extends Recorder {

    static Logger logger = Logger.getLogger(HipChatPublisher.class.getSimpleName());

    private final String room;

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
    public boolean perform(Build build, Launcher launcher, BuildListener listener) throws InterruptedException,
            IOException {
        HipChatClient hipChatClient = new HipChatClient();
        hipChatClient.publish(this, new MessageBuilder(this, build).getMessage());
        return true;
    }

    @Override
    public DescriptorImpl getDescriptor() {
        return (DescriptorImpl) super.getDescriptor();
    }

    @Extension
    public static final class DescriptorImpl extends BuildStepDescriptor<Publisher> {

        private String token;
        private String room;
        private String buildServerUrl;
        private String sendAs;

        public FormValidation doCheckName(@QueryParameter String value) throws IOException, ServletException {
            if (value.length() == 0) {
                return FormValidation.error("Please set the room");
            }
            if (value.length() > 15) {
                return FormValidation.warning("Isn't the name too short?");
            }
            return FormValidation.ok();
        }

        @Override
        public boolean isApplicable(Class<? extends AbstractProject> aClass) {
            return true;
        }

        @Override
        public String getDisplayName() {
            return "HipChat Publisher";
        }

        @Override
        public boolean configure(StaplerRequest req, JSONObject formData) throws FormException {
            token = formData.getString("hipChatToken");
            room = formData.getString("hipChatRoom");
            buildServerUrl = formData.getString("hipChatBuildServerUrl");
            sendAs = formData.getString("hipChatSendAs");
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
    }

    public BuildStepMonitor getRequiredMonitorService() {
        return BuildStepMonitor.BUILD;
    }
}
