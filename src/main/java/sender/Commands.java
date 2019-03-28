package sender;

import org.json.simple.JSONObject;
import util.Utils;

public class Commands {
    public static JSONObject abortCommand() {
        return createCommandMessage("abort");
    }

    public static JSONObject resetCommand(){
        return createCommandMessage("resetFiles");
    }

    public static JSONObject doneCommand(){
        return createCommandMessage("done");
    }

    public static JSONObject createCommandMessage(String command) {
            JSONObject expected = new JSONObject();
            expected.put("command", command);
            return expected;
    }

    public static JSONObject uploadFileCommand(
            String relativePath, String content) {
        JSONObject expected = new JSONObject();
        expected.put("command", "uploadFile");
        expected.put("relativePath", Utils.toBase64(relativePath));
        expected.put("content", Utils.toBase64(content));
        return expected;
    }
}
