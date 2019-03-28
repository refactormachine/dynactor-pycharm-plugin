package config;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import util.Utils;

import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;

public class ConfigFactory {
    private static final String DEFAULT_CONFIG_FILENAME = ".dynactor_plugin_config";

    public PluginConfig defaultConfig(){
        PluginConfig config = new PluginConfig();
        config.serverPort = 8891;
        config.serverAddress = "localhost";
        config.uploadIgnoreList = Arrays.asList(
                ".*",
                "venv",
                ".pyc"
                );
        config.maxFileSizeBytes = 1024 * 1024; // 1 MB
        return config;
    }

    private PluginConfig fromJSON(JSONObject jsonObject){
        PluginConfig default_ = defaultConfig();
        PluginConfig config = new PluginConfig();
        config.serverPort = (int)jsonObject.getOrDefault("serverPort", default_.serverPort);
        config.serverAddress = (String) jsonObject.getOrDefault("serverAddress", default_.serverAddress);
        config.maxFileSizeBytes = (int) jsonObject.getOrDefault("maxFileSizeBytes", default_.maxFileSizeBytes);
        JSONArray arr = (JSONArray) jsonObject.get("uploadIgnoreList");
        if(arr != null){
            config.uploadIgnoreList = Utils.asStringList(arr);
        }else {
            config.uploadIgnoreList = default_.uploadIgnoreList;
        }
        return config;
    }

    public PluginConfig readDefaultProjectConfig(String projectRoot) throws IOException, ParseException {
        return readJSONFile(Paths.get(projectRoot, DEFAULT_CONFIG_FILENAME).toString());
    }

    public PluginConfig readJSONFile(String configPath) throws IOException, ParseException {
        JSONParser parser = new JSONParser();
        Object obj = parser.parse(new FileReader(configPath));
        return fromJSON((JSONObject)obj);
    }
}
