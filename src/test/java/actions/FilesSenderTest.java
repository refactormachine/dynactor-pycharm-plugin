package actions;

import com.google.common.io.Files;
import org.apache.commons.io.FileUtils;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;
import sender.Sender;
import sender.TestWithServerMock;
import util.FilesFinder;
import util.Utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class FilesSenderTest extends TestWithServerMock {


    private void createFiles(String path, Map<String, String> pathToContent) throws FileNotFoundException, UnsupportedEncodingException {
        for (String relativePath : pathToContent.keySet()) {
            Utils.writeFile(
                    Paths.get(path, relativePath).toString(),
                    pathToContent.get(relativePath));
        }
    }

    private void assertFilesSent(Map<String, String> expectedPathsAndContents) {
        for (JSONObject message : getMessagesReceived()) {
            if(! "uploadFile".equals(message.get("command")))
                continue;
            String content = Utils.fromBase64((String) message.get("content"));
            ;
            String relativePath = Utils.fromBase64((String) message.get("relativePath"));
            Assert.assertTrue(expectedPathsAndContents.containsKey(relativePath));
            Assert.assertEquals(expectedPathsAndContents.get(relativePath), content);
        }
    }


    @Test
    public void testSendWholeDirectory() throws IOException {
        File tempDir = Files.createTempDir();
        byte[] nonASCII = {(byte)0xd2, (byte)0x99};
        String nonASCIIString = new String(nonASCII, StandardCharsets.UTF_8);

        HashMap<String, String> filesSpec = new HashMap<String, String>() {{
            put("someDir/foo.py", "content1");
            put("goo.py", "content2");
            put("do_not_send_me", "content3");

            put("nonASCII.py", nonASCIIString);
            put("nonASCII_" +nonASCIIString + ".py", "content4");
            put("largeFile.py", new String(new char[1 << 21]));
        }};

        createFiles(tempDir.getAbsolutePath(), filesSpec);

        Sender sender = createSender();
        FilesSender filesSender = new FilesSender(sender, tempDir.getAbsolutePath(),
                new FilesFinder() {
                    @Override
                    public List<String> findAllFiles(String root) throws IOException {
                        List<String> x = filesSpec.keySet().stream().filter(s -> !s.equals("do_not_send_me")).collect(Collectors.toList());
                        return x;
                    }
                });
        filesSender.run();

        assertFilesSent(filesSpec);

        FileUtils.deleteDirectory(tempDir);
    }

    @Override
    protected int getPort() {
        return 8801;
    }
}