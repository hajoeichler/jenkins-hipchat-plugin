package jenkins.plugins.hipchat;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class MessageTest {

    @Test
    public void testNotify() throws Exception {
        assertEquals("1 indicates that hipcaht notifies the user.", "1", new Message("", "", true).getNotify());
    }

    @Test
    public void testNotNotify() throws Exception {
        assertEquals("0 indicates that the message is writen in the room without notification to the user.", "0",
                new Message("", "", false).getNotify());
    }

}
