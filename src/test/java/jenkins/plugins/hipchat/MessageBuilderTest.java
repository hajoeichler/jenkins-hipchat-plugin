package jenkins.plugins.hipchat;

import static org.junit.Assert.assertEquals;
import hudson.model.Result;
import hudson.model.AbstractBuild;
import hudson.model.AbstractProject;

import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings("rawtypes")
public class MessageBuilderTest {

    private HipChatPublisher hipChatPublisher;
    HipChatPublisher.DescriptorImpl descriptorImpl;
    private AbstractBuild build;
    private AbstractProject project;

    private void createMocks() {
        hipChatPublisher = Mockito.mock(HipChatPublisher.class);
        descriptorImpl = Mockito.mock(HipChatPublisher.DescriptorImpl.class);
        Mockito.when(hipChatPublisher.getDescriptor()).thenReturn(descriptorImpl);

        build = Mockito.mock(AbstractBuild.class);
        project = Mockito.mock(AbstractProject.class);
        Mockito.when(build.getProject()).thenReturn(project);
    }

    private void setBuildServerUrl(String url) {
        Mockito.when(descriptorImpl.getBuildServerUrl()).thenReturn(url);
    }

    private void setBuildName(String name) {
        Mockito.when(build.getDisplayName()).thenReturn(name);
    }

    private void setJobName(String name) {
        Mockito.when(project.getDisplayName()).thenReturn(name);
    }

    private void setDurationString(String duration) {
        Mockito.when(build.getDurationString()).thenReturn(duration);
    }

    private void setResult(Result result) {
        Mockito.when(build.getResult()).thenReturn(result);
    }

    private void setBuildUrl(String url) {
        Mockito.when(build.getUrl()).thenReturn(url);
    }

    @Test
    public void testName() throws Exception {
        createMocks();
        String url = "http://example.com";
        setBuildServerUrl(url);
        String jobName = "TestJob";
        setJobName(jobName);
        String buildName = "#0";
        setBuildName(buildName);
        String duration = "1ms";
        setDurationString(duration);
        setBuildUrl("/123/");
        setResult(Result.SUCCESS);

        MessageBuilder mb = new MessageBuilder(hipChatPublisher, build);
        Message message = mb.getMessage();
        assertEquals("Unexpected color for message", "green", message.getColor());
        assertEquals("Message should not notify", "0", message.getNotify());
        assertEquals(
                "Unexpected message content",
                "TestJob - #0 Success after 1ms (<a href='http://example.com/123/'>Open</a> <a href='http://example.com/123/console'>Console</a>)",
                message.getContent());
    }
}
