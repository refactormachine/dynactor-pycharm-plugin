package logic;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.PlatformDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import gui.CardsDiffsViewsCreator;
import gui.RefactoringView;
import org.jetbrains.annotations.NotNull;
import util.Utils;

import javax.swing.*;

public class HelloAction extends AnAction {
    private RefactoringsFetcher fetcher;
    private final RefactoringSelector refactoringSelector = new RefactoringSelector();

    public HelloAction() {
        super("Hello");
        fetcher = new RefactoringsFetcher("/tmp/counter.txt");
        new Thread(fetcher).start();
    }

    public void actionPerformed(@NotNull AnActionEvent event) {
        Project project = event.getProject();
        if(project == null){
            return;
        }
        VirtualFile currentFile = event.getData(PlatformDataKeys.VIRTUAL_FILE);
        if (currentFile == null) {
            Messages.showErrorDialog("Please open a file in the editor", "Refactoring Viewer Error");
            return;
        }
        System.out.println("currentFile.getPath() = " + currentFile.getPath());

        String currentFilePathRelative = Utils.relativePath(project.getBasePath(),
                currentFile.getPath());
        final CardsDiffsViewsCreator creator = new CardsDiffsViewsCreator(project);
        refactoringSelector.showRefactoringsView(project.getBasePath(), currentFilePathRelative, fetcher.getRefactoringSuggestions(), new RefactoringsTreeView(new RefactoringView(creator)));
    }

//    @Override
    public void update1(@NotNull AnActionEvent e) {
        final Presentation presentation = e.getPresentation();
        String iconFname = "/home/bugabuga/Downloads/home-icon.png";
        if (fetcher.getCounter() % 2 == 1) {
            iconFname = "/home/bugabuga/Downloads/access_20.png";
        }
        presentation.setIcon(new ImageIcon(iconFname));
    }

    public RefactoringsFetcher getFetcher() {
        return fetcher;
    }
}
