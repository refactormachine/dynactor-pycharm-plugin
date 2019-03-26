package actions;

import sender.Sender;
import util.Utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class FilesSender implements Runnable{
    private final String root;
    private final Sender sender;
    private boolean aborted = false;
    private Consumer<Double> update;
    private String suffix;

    public FilesSender(Sender sender, String root, String suffix) {
        this.sender = sender;
        this.root = root;
        this.suffix = suffix;
    }

    @Override
    public void run() {
        List<String> filesToUpload = findAllFiles(root, suffix);
        sender.sendMessage("resetFiles", "");
        for(int i = 0; i < filesToUpload.size(); ++i){
            if(aborted){
                sender.sendMessage("abort", "");
                return;
            }
            String filename = filesToUpload.get(i);
            try {
                sender.sendMessage("uploadFile", Utils.readFileContent(filename));
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateProgress(i / (double)filesToUpload.size());
        }
        updateProgress(1.0);
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
