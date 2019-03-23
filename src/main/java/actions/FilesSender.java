package actions;

import org.apache.commons.codec.binary.Base64;
import org.apache.http.entity.StringEntity;
import util.Utils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FilesSender implements Runnable{
    private final String root;
    private boolean aborted = false;
    private Consumer<Double> update;
    private String suffix;

    public FilesSender(String root, String suffix) {
        this.root = root;
        this.suffix = suffix;
    }

    @Override
    public void run() {
        List<String> filesToUpload = findAllFiles(root, suffix);
        send("resetFiles", "");
        for(int i = 0; i < filesToUpload.size(); ++i){
            if(aborted){
                send("abort", "");
                return;
            }
            String filename = filesToUpload.get(i);
            try {
                send("uploadFile", Utils.readFileContent(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateProgress(i / (double)filesToUpload.size());
        }
        updateProgress(1.0);
    }

    private void send(String command, String content) {
        String encodedContent = new String(Base64.encodeBase64(content.getBytes()));
        StringEntity message = null;
        try {
            message = new StringEntity(String.format(
                    "message={\"command\":\"%s\",\"content\":\"%s\"} ",
                    command, encodedContent));
        } catch (UnsupportedEncodingException e) {
            assert false;
            e.printStackTrace();
        }
        String url = "https://localhost:8812/message";
        try {
            Utils.sendHttpsPost(url, message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private List<String> findAllFiles(String root, String suffix) {
        return new ArrayList<>();
    }

    private void updateProgress(double percent){
        if(update != null) {
            update.accept(percent);
        }
    }

    public void setUpdateFunc(Consumer<Double> updateProcessBar) {
        this.update = updateProcessBar;
    }

    public void abort() {
        aborted = true;
    }
}
