package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.SystemIndependent;
import sender.HttpsSender;
import sender.Sender;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;

/**
 * Sends a snapshot of the code and all relevant files to the server.
 */
public class StartRefactoringAction extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if (project == null) {
            return;
        }
        System.out.println("Sync with server...");
        @SystemIndependent String basePath = project.getBasePath();
        startOperation(basePath);
    }

    public static void startOperation(@SystemIndependent String basePath) {
        Sender sender = new HttpsSender(
                "http://localhost:8891/message","moshe:moshe");
        FilesSender filesSender = new FilesSender(sender, basePath, ".py");
        new Thread(filesSender).start();
        StartDialog dialog = new StartDialog();
        filesSender.setUpdateFunc(dialog::updateProcessBar);
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
                ()->startOperation("asd")
        );
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isEnabled = RefactoringState.getInstance().isStartEnabled();
        e.getPresentation().setEnabled(isEnabled);
        super.update(e);
    }
}
