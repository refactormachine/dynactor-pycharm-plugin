package sender;

import java.io.IOException;
import java.util.List;

public interface FilesFinder {
    List<String> findAllFiles(String root) throws IOException;
}
