package logic;

import org.jetbrains.annotations.NotNull;
import util.Utils;
import util.tree.TreeNode;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;


/**
 * Fetches refactorings from the server.
 */
class RefactoringsFetcher implements Runnable {
    private String fileToCreate;
    private int counter;

    public RefactoringsFetcher(String fileToCreate) {
        counter = 0;
        this.fileToCreate = fileToCreate;
    }

    public int getCounter() {
        return counter;
    }

    private int getRandomInt() {
        String httpsURL = "https://www.random.org/cgi-bin/randbyte?nbytes=1&format=b";
        URL url = null;
        try {
            url = new URL(httpsURL);
        } catch (MalformedURLException e) {
            e.printStackTrace();

            return 0;
        }
        HttpsURLConnection con = null;
        try {
            con = (HttpsURLConnection)url.openConnection();
        } catch (IOException e) {
            e.printStackTrace();
        }

        if(con!=null){
            try {
                BufferedReader br = new BufferedReader( new InputStreamReader(con.getInputStream()) );

                return br.read();
            }
            catch (IOException e) {
                e.printStackTrace();
            }
        }
        return 0;
    }

    public void run() {
        for (int i = 0; i < 100000; ++i) {
            counter = getRandomInt();
            try {
                PrintWriter writer = new PrintWriter(fileToCreate, "UTF-8");
                writer.println(i);
                writer.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @NotNull
    List<TreeNode<RefactoringSuggestion>> getRefactoringSuggestions() {
        String newContent2 = Utils.getContentByLanguage(Utils.FILE_SUFFIX);
        TreeNode<RefactoringSuggestion> r1 = new TreeNode<>(
                RefactoringSuggestion.specificExampleRefactoringSuggestion(
                        "ref1", "content1", newContent2));
        TreeNode<RefactoringSuggestion> r2 = new TreeNode<>(
                RefactoringSuggestion.specificExampleRefactoringSuggestion(
                        "ref2", "content1", "content5")
        );
        r1.addChild(
                RefactoringSuggestion.specificExampleRefactoringSuggestion(
                        "ref3", newContent2, "content3")
        );
        r1.addChild(
                RefactoringSuggestion.specificExampleRefactoringSuggestion(
                        "ref4", newContent2, "content4")
        );
        return Arrays.asList(r1, r2);
    }

}
