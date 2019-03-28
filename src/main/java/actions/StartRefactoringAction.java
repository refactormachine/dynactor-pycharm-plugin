package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.openapi.ui.Messages;
import config.ConfigFactory;
import config.PluginConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;
import org.json.simple.parser.ParseException;
import sender.FilesFinder;
import sender.HttpsSender;
import sender.Sender;
import util.Utils;

import javax.swing.*;
import java.io.IOException;

/**
 * Sends a snapshot of the code and all relevant files to the server.
 */
public class StartRefactoringAction extends AnAction {

    public static final String PASSWORD = "moshe:moshe";

    private static void run(String basePath) {
        try {
            startOperation(basePath);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        System.out.println("Sync with server...");
        @SystemIndependent String basePath = project.getBasePath();
        run(basePath);
    }

    public static void startOperation(@SystemIndependent String basePath) throws IOException, ParseException {
        PluginConfig config = new ConfigFactory().readProjectConfig(basePath);
        Sender sender = new HttpsSender(
                String.format("http://%s:%d/message", config.serverAddress, config.serverPort), PASSWORD);
        sendFiles(basePath, config, sender);
    }

    private static void sendFiles(@SystemIndependent String basePath, PluginConfig config, Sender sender) {
        FilesFinder finder = config.createFinder();
        StartDialog dialog = new StartDialog();
        FilesSender filesSender = new FilesSender(sender, basePath, finder);
        filesSender.setErrorCallback(s->{
            dialog.updateMessage("Error:\n" + s);
        });
        filesSender.setUpdateFunc(dialog::updateProcessBar);
        filesSender.setDoneFunc(()-> dialog.setOKActionEnabled(true));
        new Thread(filesSender).start();
        boolean result = dialog.showAndGet();
        if(result) {
            RefactoringState.getInstance().setOngoing();
        }else{
            filesSender.abort();
        }
    }

    public static void main(String[] argv) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        SwingUtilities.invokeLater(() -> run("/home/bugabuga/sample"));
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isEnabled = RefactoringState.getInstance().isStartEnabled();
        e.getPresentation().setEnabled(isEnabled);
        super.update(e);
    }
}
