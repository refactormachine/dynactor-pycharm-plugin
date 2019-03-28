package sender;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class FindSmallFiles implements FilesFinder {
    private final FilesFinder finder;
    private final long maxFileSizeBytes;

    public FindSmallFiles(FilesFinder finder, long maxFileSizeBytes) {
        this.finder = finder;
        this.maxFileSizeBytes = maxFileSizeBytes;
    }

    @Override
    public List<String> findAllFiles(String root) throws IOException {
        ArrayList<String> res = new ArrayList();
        for (String filename : finder.findAllFiles(root)) {
            long fileSize = new File(filename).length();
            if (fileSize <= maxFileSizeBytes) {
                res.add(filename);
            }else{
                System.out.printf("Skipping large file (size=%d KB)", fileSize / 1024);
                System.out.printf("Max file size=%d KB\n", maxFileSizeBytes / 1024);
            }
        }
        return res;
    }
}
