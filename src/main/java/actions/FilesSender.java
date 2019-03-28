package actions;

import sender.Sender;
import util.FilesFinder;
import util.Utils;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class FilesSender implements Runnable {
    private final String root;
    private final Sender sender;
    private final FilesFinder finder;
    private Consumer<String> errorCallback;
    private boolean aborted = false;
    private Consumer<Double> update;
    private Runnable doneCallback;

    public FilesSender(Sender sender, String root, FilesFinder finder) {
        this.sender = sender;
        this.root = root;
        this.finder = finder;
    }

    @Override
    public void run() {
        List<String> filesToUpload;
        try {
            filesToUpload = finder.findAllFiles(root);
        } catch (IOException e) {
            errorCallback.accept(Arrays.toString(e.getStackTrace()));
            return;
        }
        sender.sendMessage(Utils.createCommandMessage("resetFiles"));
        for (int i = 0; i < filesToUpload.size(); ++i) {
            if (aborted) {
                sender.sendMessage(Utils.createCommandMessage("abort"));
                return;
            }
            String filename = filesToUpload.get(i);
            try {
                sender.sendMessage(Utils.createFileMessage(
                        Utils.relativePath(root, filename),
                        Utils.readFileContent(filename)
                ));
            } catch (IOException e) {
                e.printStackTrace();
            }
            updateProgress(i / (double) filesToUpload.size());
        }
        sender.sendMessage(Utils.createCommandMessage("done"));
        updateProgress(1.0);
        done();
    }

    private void done() {
        if (doneCallback != null) {
            doneCallback.run();
        }
    }

    private void updateProgress(double percent) {
        if (update != null) {
            update.accept(percent);
        }
    }

    public void setUpdateFunc(Consumer<Double> updateProcessBar) {
        this.update = updateProcessBar;
    }

    public void setErrorCallback(Consumer<String> errorCallback){
        this.errorCallback = errorCallback;
    }

    public void setDoneFunc(Runnable doneCallback) {
        this.doneCallback = doneCallback;
    }

    public void abort() {
        aborted = true;
    }
}
