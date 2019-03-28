package sender;

import org.json.simple.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

public interface Sender {
    void sendMessage(JSONObject message) throws IOException;
}
