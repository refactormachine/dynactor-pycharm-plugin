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
import sender.HttpsSender;
import sender.Sender;
import util.FilesFinder;
import util.PythonicFilesFinder;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Sends a snapshot of the code and all relevant files to the server.
 */
public class StartRefactoringAction extends AnAction {

    public static final String PASSWORD = "moshe:moshe";

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        System.out.println("Sync with server...");
        @SystemIndependent String basePath = project.getBasePath();
        try {
            startOperation(basePath);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public static void startOperation(@SystemIndependent String basePath) throws IOException, ParseException {
        PluginConfig config = new ConfigFactory().readDefaultProjectConfig(basePath);
        Sender sender = new HttpsSender(
                String.format("http://%s:%d/message", config.serverAddress, config.serverPort), PASSWORD);
        FilesFinder finder = new PythonicFilesFinder(config.uploadIgnoreList);
        StartDialog dialog = new StartDialog();
        FilesSender filesSender = new FilesSender(sender, basePath, finder);
        filesSender.setErrorCallback(s->{
            dialog.close(DialogWrapper.CLOSE_EXIT_CODE);
            Messages.showErrorDialog(s, "Files Synch Error");
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

    public static void main(String[] argv) throws IOException {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        EventQueue.invokeLater(
                ()-> {
                    try {
                        startOperation("/home/bugabuga/sample");
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
        );
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isEnabled = RefactoringState.getInstance().isStartEnabled();
        e.getPresentation().setEnabled(isEnabled);
        super.update(e);
    }
}
