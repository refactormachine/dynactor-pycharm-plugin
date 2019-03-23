package logic;

import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import util.tree.TreeNode;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class RefactoringSelectorTest extends TestWithProjectSetup {
    private RefactoringsTreeView treeMock;
    private ArgumentCaptor<MyAction> reject;
    private ArgumentCaptor<MyAction> apply;
    private ArgumentCaptor<MyAction> next;
    private ArgumentCaptor<MyAction> prev;
    private ArgumentCaptor<RefactoringSuggestion> setCurrent;
    private ArgumentCaptor<Boolean> nextButtonEnabled;
    private ArgumentCaptor<Boolean> prevButtonEnabled;

    private void setupMocks() {
        treeMock = Mockito.mock(RefactoringsTreeView.class);

        apply = ArgumentCaptor.forClass(MyAction.class);
        next = ArgumentCaptor.forClass(MyAction.class);
        prev = ArgumentCaptor.forClass(MyAction.class);
        reject = ArgumentCaptor.forClass(MyAction.class);

        setCurrent = ArgumentCaptor.forClass(RefactoringSuggestion.class);
        nextButtonEnabled = ArgumentCaptor.forClass(Boolean.class);
        prevButtonEnabled = ArgumentCaptor.forClass(Boolean.class);
    }

    @Test
    public void testRejectedRefactoringNotShown() throws IOException {
        setupProjectFiles(Arrays.asList("content0"));
        List<TreeNode<RefactoringSuggestion>> suggestions = createSuggestions(
                Arrays.asList(FileDiff.create("content0", "newContent0")), "refactor-1"
        );

        setupMocks();
        RefactoringSelector selector = new RefactoringSelector();

        selector.showRefactoringsView(getRoot(), relativePath(0),
                suggestions, treeMock);
        captureOperationMocks();

        Mockito.verify(treeMock, Mockito.times(1)).show();
        Mockito.verify(treeMock, Mockito.times(0)).close();
        reject.getValue().run();
        Mockito.verify(treeMock, Mockito.times(1)).close();

        // Refactoring rejected once - now the view is not even shown.
        selector.showRefactoringsView(getRoot(), relativePath(0),
                suggestions, treeMock);

        Mockito.verify(treeMock, Mockito.times(2)).show();
        Mockito.verify(treeMock, Mockito.times(2)).close();
    }

    @Test
    public void testNavigatingTheTree() throws IOException {
        setupForTreeNavigation();
        assertRefactoringIdShown("refactor-1", false, false);
        apply.getValue().run();
        assertRefactoringIdShown("refactor-2", true, false);
        next.getValue().run();
        assertRefactoringIdShown("refactor-3", false, true);
        prev.getValue().run();
        assertRefactoringIdShown("refactor-2", true, false);
    }

    private void setupForTreeNavigation() throws IOException {
        setupProjectFiles(Arrays.asList("content0"));
        List<TreeNode<RefactoringSuggestion>> suggestions = createSuggestions(
                Arrays.asList(FileDiff.create("content0", "newContent0")), "refactor-1"
        );
        suggestions.get(0).addChild(createSuggestion(Arrays.asList(FileDiff.create(
                "newContent0", "newContent1"
        )), "refactor-2"));
        suggestions.get(0).addChild(createSuggestion(Arrays.asList(FileDiff.create(
                "newContent0", "newContent2"
        )), "refactor-3"));

        setupMocks();
        RefactoringSelector selector = new RefactoringSelector();

        selector.showRefactoringsView(getRoot(), relativePath(0),
                suggestions, treeMock);

        captureOperationMocks();
    }

    private void captureOperationMocks() {
        Mockito.verify(treeMock).setOperations(apply.capture(), next.capture(), prev.capture(), reject.capture());
    }

    private void assertRefactoringIdShown(String expectedId, boolean isNextEnabled, boolean isPrevEnabled){
        Mockito.verify(treeMock, Mockito.atLeastOnce()).setCurrentRefactoring(
                setCurrent.capture(), nextButtonEnabled.capture(), prevButtonEnabled.capture());
        Assert.assertEquals(expectedId, setCurrent.getValue().id());
        Assert.assertEquals(nextButtonEnabled.getValue(), isNextEnabled);
        Assert.assertEquals(prevButtonEnabled.getValue(), isPrevEnabled);
    }

    @Test
    public void testInvalidRefactoringsNotShown() throws IOException {
        setupProjectFiles(Arrays.asList("content0"));
        List<TreeNode<RefactoringSuggestion>> suggestions = createSuggestions(
                Arrays.asList(FileDiff.create("invalidContnet", "newContent0")), "refactor-1"
        );

        setupMocks();
        RefactoringSelector selector = new RefactoringSelector();

        selector.showRefactoringsView(getRoot(), relativePath(0),
                suggestions, treeMock);
        Mockito.verify(treeMock, Mockito.times(1)).close();
    }

    @NotNull
    private List<TreeNode<RefactoringSuggestion>> createSuggestions(List<FileDiff> diffs, String id) {
        RefactoringSuggestion suggestion = createSuggestion(diffs, id);
        return Arrays.asList(new TreeNode<>(suggestion));
    }

    @NotNull
    private RefactoringSuggestion createSuggestion(List<FileDiff> diffs, String id) {
        HashMap<String, FileDiff> m = new HashMap<>();
        for(int i = 0; i < diffs.size(); ++i) {
            m.put(relativePath(i), diffs.get(i));
        }
        return RefactoringSuggestion.create(
                m, "line A\nline B", "desc",
                12, id
        );
    }
}
