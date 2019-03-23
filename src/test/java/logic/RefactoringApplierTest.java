package logic;

import org.junit.Assert;
import org.junit.Test;
import util.Utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class RefactoringApplierTest extends TestWithProjectSetup {
    @Test
    public void testApplyModifiesFiles() throws IOException, InvalidRefactoringSuggestion {
        setupProjectFiles(Arrays.asList("content0", "content1"));
        RefactoringSuggestion refactor = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("content0", "newContent0"));
                    put(relativePath(1), FileDiff.create("content1", "newContent1"));
                }}, "description", "oneLineDescription",
                1.23, "someId"
        );
        RefactoringApplier a = new RefactoringApplier(refactor, getRoot());
        a.apply();
        Assert.assertEquals("newContent0", Utils.readFileContent(path(0)));
        Assert.assertEquals("newContent1", Utils.readFileContent(path(1)));
    }

    @Test
    public void testApplyCreatesNewFile() throws IOException, InvalidRefactoringSuggestion {
        setupProjectFiles(Arrays.asList(""));
        RefactoringSuggestion refactor = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("", "newContent0"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );
        RefactoringApplier a = new RefactoringApplier(refactor, getRoot());
        a.apply();
        Assert.assertEquals("newContent0", Utils.readFileContent(path(0)));
    }

    @Test(expected = InvalidRefactoringSuggestion.class)
    public void testApplyFailsFileNotExists() throws IOException, InvalidRefactoringSuggestion {
        setupProjectFiles(Arrays.asList(""));
        RefactoringSuggestion refactor = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("invalidContent", "newContent0"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );
        RefactoringApplier a = new RefactoringApplier(refactor, getRoot());
        a.apply();
    }

    @Test(expected = IndexOutOfBoundsException.class)
    public void testIndexOutOfBoundsException() {
        ArrayList emptyList = new ArrayList();
        Object o = emptyList.get(0);
    }

    @Test(expected = InvalidRefactoringSuggestion.class)
    public void testApplyFailsFileContentMismatch() throws IOException, InvalidRefactoringSuggestion {
        setupProjectFiles(Arrays.asList("someContent"));
        RefactoringSuggestion refactor = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("invalidContent", "newContent0"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );
        RefactoringApplier a = new RefactoringApplier(refactor, getRoot());
        a.apply();
    }

    @Test
    public void testApplyFailsButUndoSucceeds() throws IOException {
        setupProjectFiles(Arrays.asList("", "content1", ""));
        RefactoringSuggestion refactor = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("", "newContent0"));
                    put(relativePath(1), FileDiff.create("content1", "newContent1"));
                    put(relativePath(2), FileDiff.create("invalidContent", "newContent2"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );
        RefactoringApplier a = new RefactoringApplier(refactor, getRoot());
        boolean exceptionCaught = false;
        try {
            a.applyWithoutPriorValidation();
        } catch (InvalidRefactoringSuggestion invalidRefactoringSuggestion) {
            exceptionCaught = true;
        }
        Assert.assertTrue(exceptionCaught);
        Assert.assertEquals("newContent0", Utils.readFileContent(path(0)));
        Assert.assertEquals("newContent1", Utils.readFileContent(path(1)));
        Assert.assertFalse(new File(path(2)).exists());
        a.undoApply();
        Assert.assertEquals("content1", Utils.readFileContent(path(1)));
        Assert.assertFalse(new File(path(0)).exists());
    }

    @Test
    public void testRefactoringValidityCheck() throws IOException {
        setupProjectFiles(Arrays.asList("content1", "content2", "", ""));
        RefactoringSuggestion refactoring1 = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("content1", "newContent0"));
                    put(relativePath(1), FileDiff.create("content2", "newContent1"));
                    put(relativePath(2), FileDiff.create("", "newContent2"));
                    put(relativePath(3), FileDiff.create("", "newContent3"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );
        Assert.assertTrue(refactoring1.isRefactoringValid(getRoot()));

        RefactoringSuggestion refactoring2 = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("content1", "newContent0"));
                    put(relativePath(1), FileDiff.create("content2", "newContent1"));
                    put(relativePath(2), FileDiff.create("\n", "newContent2"));
                    put(relativePath(3), FileDiff.create("\n  \n", "newContent3"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );

        Assert.assertTrue(refactoring2.isRefactoringValid(getRoot()));

        RefactoringSuggestion refactoring3 = RefactoringSuggestion.create(
                new HashMap<String, FileDiff>() {{
                    put(relativePath(0), FileDiff.create("content1", "newContent0"));
                    put(relativePath(1), FileDiff.create("content2", "newContent1"));
                    put(relativePath(2), FileDiff.create("invalidContent", "newContent2"));
                    put(relativePath(3), FileDiff.create("\n  \n", "newContent3"));
                }}, "description", "oneLineDescription",
                1.1, "id1"
        );

        Assert.assertFalse(refactoring3.isRefactoringValid(getRoot()));
    }
}
