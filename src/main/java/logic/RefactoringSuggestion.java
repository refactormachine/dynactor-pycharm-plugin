package logic;

import com.google.auto.value.AutoValue;
import org.jetbrains.annotations.NotNull;
import util.Utils;

import java.io.IOException;
import java.io.Serializable;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.Map;


@AutoValue
public abstract class RefactoringSuggestion implements Serializable {
    @NotNull
    public static RefactoringSuggestion exampleRefactoringSuggestion(String oneLineDescription) {
        return specificExampleRefactoringSuggestion(oneLineDescription, "hello", "newContent1");
    }

    @NotNull
    public static RefactoringSuggestion specificExampleRefactoringSuggestion(String oneLineDescription, final String oldContent, final String newContent) {
        System.out.println("Utils.FILE_SUFFIX = " + Utils.FILE_SUFFIX);
        Map<String, FileDiff> m = new HashMap<String, FileDiff>() {{
            put(String.format("src/C.%s", Utils.FILE_SUFFIX), FileDiff.create(oldContent, newContent));
            put(String.format("src/D.%s", Utils.FILE_SUFFIX), FileDiff.create(oldContent, newContent));
        }};
        return create(
                m, "line A\nline B", oneLineDescription,
                12, "refactor-1"+ oneLineDescription
        );
    }

    public abstract Map<String, FileDiff> filesDiff();
    public abstract String fullDescription();
    public abstract String oneLineDescription();

    public abstract double score();
    public abstract String id();

    public static RefactoringSuggestion create(Map<String, FileDiff> filesDiff, String fullDescription, String oneLineDescription,
                                        double score, String id){
        return new AutoValue_RefactoringSuggestion(filesDiff,
                fullDescription, oneLineDescription, score, id);
    }

    public boolean isRefactoringValid(String rootPath) throws IOException {
        for(String relativePath: filesDiff().keySet()){
            String path = Paths.get(rootPath, relativePath).toString();
            FileDiff diff = filesDiff().get(relativePath);
            if(!diff.isDiffValid(Utils.readFileContent(path))){
                return false;
            }
        }
        return true;
    }
}
