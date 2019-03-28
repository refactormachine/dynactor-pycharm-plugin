package util;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class PythonicFilesFinder implements FilesFinder {

    private final List<String> filesToIgnore;

    public PythonicFilesFinder(List<String> filesToIgnore) {
        this.filesToIgnore = new ArrayList<>(filesToIgnore);
    }

    @Override
    public List<String> findAllFiles(String root) throws IOException {
        assert new File(root).isDirectory();
        File tempFile = File.createTempFile("find_files_gitignore-", ".py");
        tempFile.deleteOnExit();

        String script = Utils.readResource("find_files_gitignore.py");
        Utils.writeFile(tempFile, script);

        ProcessBuilder pb = new ProcessBuilder("python3", tempFile.getAbsolutePath(), root);

        Process process = pb.start();
        OutputStreamWriter writer = new OutputStreamWriter(process.getOutputStream());
        for (String line : filesToIgnore) {
            writer.write(line);
            writer.write('\n');
        }
        writer.close();
        return new BufferedReader(new InputStreamReader(process.getInputStream())).lines().collect(Collectors.toList());

    }
}
