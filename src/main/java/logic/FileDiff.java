package logic;

import com.github.difflib.DiffUtils;
import com.github.difflib.algorithm.DiffException;
import com.github.difflib.patch.Delta;
import com.github.difflib.patch.Patch;
import com.google.auto.value.AutoValue;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


@AutoValue
public abstract class FileDiff {
    public abstract String oldContent();

    public abstract String newContent();

    public static FileDiff create(String oldContent, String newContent) {
        return new AutoValue_FileDiff(oldContent, newContent);
    }

    public Map<String, FileDiff> createFromFilesDiff(String rootPathBefore,
                                                     Iterable<String> filesToConsider,
                                                     String rootPathAfter) {
        // TODO(bugabuga): implement
        return new HashMap<>();
    }

    public boolean isDiffValid(String currentContent) {
        Patch<String> patch;
        try {
            patch = DiffUtils.diff(oldContent(), currentContent);
        } catch (DiffException e) {
            return false;
        }
        for (Delta<String> delta : patch.getDeltas()) {
            if (containsNonBlankLine(delta.getOriginal().getLines()) ||
                    containsNonBlankLine(delta.getRevised().getLines())) {
                return false;
            }
        }
        return true;
    }

    private boolean containsNonBlankLine(List<String> lines) {
        for (String line : lines) {
            if (!line.matches("\\s*")) {
                return true;
            }
        }
        return false;
    }
}
