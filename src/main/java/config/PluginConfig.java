package config;

import org.json.simple.JSONObject;
import sender.FilesFinder;
import sender.FindSmallFiles;
import sender.PythonicFilesFinder;
import util.Utils;

import java.io.FileNotFoundException;
import java.io.UnsupportedEncodingException;
import java.util.List;

public class PluginConfig {
    public int serverPort;
    public String serverAddress;
    public List<String> uploadIgnoreList;
    public long maxFileSizeBytes;

    public JSONObject toJson(){
        JSONObject obj = new JSONObject();
        obj.put("serverPort", serverPort);
        obj.put("serverAddress", serverAddress);
        obj.put("maxFileSizeBytes", maxFileSizeBytes);
        obj.put("uploadIgnoreList", Utils.jsonArrayFromList(uploadIgnoreList));
        return obj;
    }

    public void save(String path) throws FileNotFoundException, UnsupportedEncodingException {
        Utils.writeFile(path, toJson().toString());
    }

    public FilesFinder createFinder() {
        FilesFinder finder1 = new PythonicFilesFinder(uploadIgnoreList);
        return new FindSmallFiles(finder1, maxFileSizeBytes);
    }
}
