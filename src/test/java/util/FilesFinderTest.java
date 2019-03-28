package util;

import com.intellij.openapi.util.io.FileUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.*;

public class FilesFinderTest {
    @Test
    public void testFindFiles() throws IOException {
        File dir = FileUtil.createTempDirectory("projectRoot", "", true);
        List<String> filesToIgnore = Arrays.asList("moshe", ".*", "*.pyc", "dev/");
        List<String> namesExpectedFind = Arrays.asList("abc/flux", "moshe.moshe", "t");
        List<String> namesExpectedIgnore = Arrays.asList("moshe", "x.pyc", "dev/xyz");
        List<String> files2create = Utils.joinLists(namesExpectedFind, namesExpectedIgnore);
        for(String fileToCreate: files2create) {
            Utils.writeFile(Paths.get(dir.getAbsolutePath(), fileToCreate).toString(), "content");
        }
        List<String> actualFilesFound = new PythonicFilesFinder(filesToIgnore).
                findAllFiles(dir.getAbsolutePath());
        assertEquals(namesExpectedFind, actualFilesFound);
    }
}