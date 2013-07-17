package jenkins.plugins.hipchat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;

public class HipChatClient {

    static Logger logger = Logger.getLogger(HipChatClient.class.getSimpleName());
    private final HttpClient httpClient;

    public HipChatClient() {
        this(new HttpClient());
    }

    public HipChatClient(HttpClient httpClient) {
        this.httpClient = httpClient;

    }

    public void publish(HipChatPublisher publisher, Message message) {
        String url = "https://api.hipchat.com/v1/rooms/message?auth_token=" + publisher.getDescriptor().getToken();
        PostMethod post = new PostMethod(url);

        String room = publisher.getRoom();
        if (room == null || "".equals(room)) {
            room = publisher.getDescriptor().getRoom();
        }

        try {
            post.addParameter("from", publisher.getDescriptor().getSendAs());
            post.addParameter("room_id", room);
            post.addParameter("message", message.getContent());
            post.addParameter("color", message.getColor());
            post.addParameter("notify", message.getNotify());

            post.getParams().setContentCharset("UTF-8");
            int status = httpClient.executeMethod(post);

            if (status != 200) {
                logger.log(Level.WARNING, "HipChat post failed. Response: " + post.getResponseBodyAsString());
            }
        } catch (Exception e) {
            logger.log(Level.WARNING, "Error posting to HipChat", e);
        } finally {
            post.releaseConnection();
        }
    }
}
