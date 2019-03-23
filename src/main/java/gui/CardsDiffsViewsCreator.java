package gui;

import com.intellij.diff.DiffContentFactory;
import com.intellij.diff.DiffManager;
import com.intellij.diff.DiffRequestPanel;
import com.intellij.diff.contents.DiffContent;
import com.intellij.diff.requests.SimpleDiffRequest;
import com.intellij.ide.highlighter.JavaFileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import logic.FileDiff;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class CardsDiffsViewsCreator implements DiffViewCreator {
    private Project project;

    public CardsDiffsViewsCreator(Project project){
        this.project = project;
    }

    private JComponent createDiffPanel(VirtualFile currentFile, String suggestedContent) {
        DiffContent originalContent = DiffContentFactory.getInstance().create(project, currentFile);
        String filename = currentFile.getName();
        return createDiffPanel(suggestedContent, originalContent, filename);
    }

    @NotNull
    private JComponent createDiffPanel(String suggestedContent, DiffContent originalContent, String filename) {
        DiffContent newContent = DiffContentFactory.getInstance().create(
                suggestedContent, originalContent.getContentType());
        SimpleDiffRequest request = new SimpleDiffRequest("Compare " + filename, originalContent, newContent, "Original " + filename, "Modified " + filename);

        DiffRequestPanel diffPanel = DiffManager.getInstance().createRequestPanel(project, () -> {
            System.out.println("Dispose!");
        }, null);
        diffPanel.setRequest(request);
        // Note: here - we might configure the panel some more before retrieving
        // the JComponent.
        return diffPanel.getComponent();
    }

    public List<JComponent> createDiffViews(Map<String, FileDiff> filesDiff) {
        List<JComponent> views = new ArrayList<>();
        for(String filePath: filesDiff.keySet()){
            String basePath = project.getBasePath();
            assert basePath != null;
            Path fullPath = Paths.get(basePath, filePath);
            VirtualFile f = LocalFileSystem.getInstance().refreshAndFindFileByPath(fullPath.toString());
            String newContent = filesDiff.get(filePath).newContent();
            views.add(createDiffView(filePath, f, newContent));
        }
        return views;
    }

    @NotNull
    private JComponent createDiffView(String filePath, VirtualFile f, String newContent) {
        JComponent view;
        if(f == null) {
            String filename = Paths.get(filePath).getFileName().toString();
            view = createDiffPanelAgainstEmptyFile(filename, newContent);
        }else{
            view = createDiffPanel(f, newContent);
        }
        view.setName(filePath);
        return view;
    }

    private JComponent createDiffPanelAgainstEmptyFile(String filename, String newContent) {
        // TODO(bugabuga): change this file-type
        DiffContent oldContent = DiffContentFactory.getInstance().create("", JavaFileType.INSTANCE);
        return createDiffPanel(newContent, oldContent, filename);
    }
}
