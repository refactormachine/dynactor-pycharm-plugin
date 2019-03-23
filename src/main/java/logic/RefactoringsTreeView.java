package logic;

import com.intellij.openapi.Disposable;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.Disposer;
import gui.MyIcons;
import gui.RefactoringView;
import gui.Strings;

public class RefactoringsTreeView {
    private DialogBuilder builder;
    private boolean alreadyClosed;

    private RefactoringView view;

    public RefactoringsTreeView(RefactoringView refactoringView) {
        view = refactoringView;
        alreadyClosed = false;
        createDialogBuilder();
        builder.setCenterPanel(view.getPanel());
    }

    private void createDialogBuilder() {
        builder = new DialogBuilder();
        builder.removeAllActions();
        builder.setTitle(Strings.DIFF_TITLE);
        Disposable parent = Disposer.newDisposable();
        builder.addDisposable(parent);
    }

    public void close() {
        if(builder.getDialogWrapper().isShowing()) {
            builder.getDialogWrapper().close(123);
        }
        alreadyClosed = true;
    }

    public void setOperations(MyAction apply, MyAction next, MyAction previous, MyAction reject) {
        view.setOperations(apply, next, previous, reject);
    }

    public void setCurrentRefactoring(RefactoringSuggestion refactoring, Boolean isNextButtonEnabled, Boolean isPrevButtonEnabled) {
        view.setCurrentRefactoring(refactoring);
        view.setNextEnabled(isNextButtonEnabled);
        view.setPrevEnabled(isPrevButtonEnabled);
    }

    public void show() {
        if(!alreadyClosed) {
            builder.show();
        }
    }

    public void showCompletionMessage(int totalCount) {
        Messages.showMessageDialog(String.format("Performed %d refactorings", totalCount),
                "Refactorings Summary", MyIcons.Robot);
    }
}