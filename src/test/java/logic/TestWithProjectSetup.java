package logic;

import util.Utils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public abstract class TestWithProjectSetup  {
    private List<String> relativePaths = new ArrayList<>();
    private String root;

    protected void setupProjectFiles(List<String> filesContents) throws IOException {
        root = Files.createTempDirectory("tempProject").toString();
        relativePaths.clear();
        for (int i = 0; i < filesContents.size(); ++i) {
            String relative = Paths.get("src", String.format("file%d.%s", i, Utils.FILE_SUFFIX)).toString();
            String path = Paths.get(root, relative).toString();
            relativePaths.add(relative);
            String content = filesContents.get(i);
            if (!content.isEmpty()) {
                Utils.writeFile(path, content);
            }
        }
    }

    protected String path(int i){
        return Paths.get(root, relativePath(i)).toString();
    }

    public String relativePath(int i){
        return relativePaths.get(i);
    }

    protected String getRoot() {
        return root;
    }
}
