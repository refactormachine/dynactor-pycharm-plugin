package actions;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import org.jetbrains.annotations.NotNull;

public class RestartRefactoring extends AnAction {

    @Override
    public void actionPerformed(@NotNull AnActionEvent e) {
    }

    @Override
    public void update(@NotNull AnActionEvent e) {
        boolean isEnabled = RefactoringState.getInstance().isRestartEnabled();
        e.getPresentation().setEnabled(isEnabled);
        super.update(e);
    }
}
