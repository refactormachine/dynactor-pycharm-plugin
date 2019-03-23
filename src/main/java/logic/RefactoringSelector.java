package logic;

import org.jetbrains.annotations.SystemIndependent;
import util.Utils;
import util.tree.TreeNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class RefactoringSelector {
    private RefactoringsTreeView refactoringsTreeView;

    private List<TreeNode<RefactoringSuggestion>> refactorings;
    private int currentRefactoringIndex;
    private Set<String> rejectedRefactorings;

    private int totalCount;
    private String basePath;

    public RefactoringSelector() {
        rejectedRefactorings = new HashSet<>();
    }

    private RefactoringSuggestion currentRefactoring() {
        if (refactorings.isEmpty()) {
            return null;
        } else {
            return currentTree().getData();
        }
    }

    private TreeNode<RefactoringSuggestion> currentTree() {
        return refactorings.get(currentRefactoringIndex % refactorings.size());
    }

    public void showRefactoringsView(@SystemIndependent String basePath, String currentFilePath,
                                     List<TreeNode<RefactoringSuggestion>> suggestions, RefactoringsTreeView refactoringsTreeView) {
        resetRefactorings(suggestions);
        this.basePath = basePath;
        this.refactoringsTreeView = refactoringsTreeView;
        this.refactoringsTreeView.setOperations(
                this::apply,
                this::next,
                this::previous,
                this::reject);

        showCurrentRefactoring();
        refactoringsTreeView.show();
    }

    private void showCurrentRefactoring() {
        RefactoringSuggestion refactoring = currentRefactoring();
        if (refactoring == null || !isRefactoringNonRejectedAndValid(refactoring)) {
            if (refactoring != null) {
                System.out.println("Skipping rejected/invalid : refactoring.id() +  = " + refactoring.id() + " rejected");
            }
            if (nonRejectedValidRefactoringExists(refactorings)) {
                next();
            } else {
                refactoringsTreeView.showCompletionMessage(totalCount);
                refactoringsTreeView.close();
            }
        } else {
            boolean nextEnabled = nonRejectedValidRefactoringExists(Utils.itemsAfter(refactorings, currentRefactoringIndex));
            boolean prevEnabled = nonRejectedValidRefactoringExists(Utils.itemsBefore(refactorings, currentRefactoringIndex));
            refactoringsTreeView.setCurrentRefactoring(refactoring, nextEnabled, prevEnabled);
        }
    }

    private void apply() {
        RefactoringSuggestion refactoring = currentRefactoring();
        if (refactoring == null) {
            return;
        }
        RefactoringApplier applier = new RefactoringApplier(refactoring, basePath);
        try {
            applier.apply();
            totalCount++;
            currentRefactoringIndex = 0;
            refactorings = currentTree().getChildren();
            showCurrentRefactoring();
        } catch (InvalidRefactoringSuggestion invalidRefactoringSuggestion) {
            invalidRefactoringSuggestion.printStackTrace();
            System.out.println("cancelling refactoring");
            applier.undoApply();
            showCurrentRefactoring();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private boolean isRefactoringNonRejectedAndValid(RefactoringSuggestion refactoring) {
        try {
            return !rejectedRefactorings.contains(refactoring.id()) &&
                    refactoring.isRefactoringValid(basePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void reject() {
        RefactoringSuggestion r = currentRefactoring();
        if (r == null) {
            return;
        }
        rejectedRefactorings.add(r.id());
        next();
    }

    private boolean nonRejectedValidRefactoringExists(Iterable<TreeNode<RefactoringSuggestion>> refactorings) {
        for (TreeNode<RefactoringSuggestion> t : refactorings) {
            if (isRefactoringNonRejectedAndValid(t.getData())) {
                return true;
            }
        }
        return false;
    }

    private void resetRefactorings(List<TreeNode<RefactoringSuggestion>> suggestions) {
        refactorings = suggestions;
        currentRefactoringIndex = 0;
        totalCount = 0;
    }

    private void next() {
        if (refactorings.size() > 0) {
            currentRefactoringIndex = (currentRefactoringIndex + 1) % refactorings.size();
        }
        showCurrentRefactoring();

    }

    private void previous() {
        if (refactorings.size() > 0) {
            currentRefactoringIndex = (currentRefactoringIndex - 1 + refactorings.size()) % refactorings.size();
        }
        showCurrentRefactoring();
    }
}