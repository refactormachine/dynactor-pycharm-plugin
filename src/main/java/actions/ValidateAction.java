package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.Project;
import com.intellij.refactoring.Refactoring;
import org.jetbrains.annotations.NotNull;

/**
 * Sends a snapshot of the code diff and all relevant files to the server.
 * Waits for execution diff from the server.
 * Displays the diff analysis.
 */
public class ValidateAction extends AnAction {
    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if(project == null){
            return;
        }
        System.out.println("Validating...");
        RefactoringState.getInstance().setInit();
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isEnabled = RefactoringState.getInstance().isValidateEnalbed();
        e.getPresentation().setEnabled(isEnabled);
        super.update(e);
    }
}
