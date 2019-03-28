package actions;

import config.ConfigFactory;
import config.PluginConfig;
import org.json.simple.parser.ParseException;
import sender.Commands;
import sender.Sender;
import sender.FilesFinder;
import util.Utils;

import java.io.IOException;
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
        update.accept(0.0);
        PluginConfig config = null;
        try {
            config = new ConfigFactory().readProjectConfig(root);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
            errorCallback.accept("Cannot load config");
            return;
        }
        List<String> filesToUpload;
        try {
            filesToUpload = finder.findAllFiles(root);
        } catch (IOException e) {
            e.printStackTrace();
            errorCallback.accept("Cannot find files to upload");
            return;
        }
        try {
            sender.sendMessage(Commands.resetCommand());
            for (int i = 0; i < filesToUpload.size(); ++i) {
                if (aborted) {
                    sender.sendMessage(Commands.abortCommand());
                    return;
                }
                String filename = filesToUpload.get(i);
                sender.sendMessage(Commands.uploadFileCommand(
                        Utils.relativePath(root, filename),
                        Utils.readFileContent(filename)
                ));
                update.accept(i / (double) filesToUpload.size());
            }
            sender.sendMessage(Commands.doneCommand());
        } catch (IOException e) {
            e.printStackTrace();
            errorCallback.accept("Failed to upload files to server");
            return;
        }
        update.accept(1.0);
        doneCallback.run();
    }

    public void setUpdateFunc(Consumer<Double> updateProcessBar) {
        this.update = updateProcessBar;
    }

    public void setErrorCallback(Consumer<String> errorCallback) {
        this.errorCallback = errorCallback;
    }

    public void setDoneFunc(Runnable doneCallback) {
        this.doneCallback = doneCallback;
    }

    public void abort() {
        aborted = true;
    }
}
