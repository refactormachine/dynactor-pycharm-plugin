package sender;


import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;


public class HttpsSenderTest extends TestWithServerMock{
    @Override
    protected int getPort() {
        return 8802;
    }

    @Test
    public void testSendMessage() throws IOException {
        Sender s = createSender();
        JSONObject message = Commands.createCommandMessage("cmd1");
        s.sendMessage(message);
        Assert.assertEquals("Bad request: " + getErroneousMessage(), getErroneousMessage(), "");
        Assert.assertEquals(1, getMessagesReceived().size());
        Assert.assertEquals(message, getMessagesReceived().get(0));
    }
}

