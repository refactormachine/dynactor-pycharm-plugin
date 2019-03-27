package sender;

import org.json.simple.JSONObject;

public interface Sender {
    void sendMessage(JSONObject message);
}
