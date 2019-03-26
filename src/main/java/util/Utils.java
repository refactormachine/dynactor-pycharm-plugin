package util;

import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Array;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class Utils {
    public static final String FILE_SUFFIX = getFileSuffix();

    private static String getFileSuffix() {
        try{
            String lang = System.getenv("refmachine_lang");
            if(lang != null){
                return lang;
            }
        }catch (Exception ignored){
        }
        return "py";
    }

    @NotNull
    public static <T> T[] toArray(List<T> elements, Class<T> clazz) {
        T[] names = (T[]) Array.newInstance(
                clazz, elements.size());
        return elements.toArray(names);
    }

    @NotNull
    public static <T> T[] toArrayNoPolymorphism(List<T> elements) {
        // Assumes the first element in the list is of type T.
        if(elements.isEmpty()){
            return (T[]) new Object[0];
        }else{
            T[] names = (T[]) Array.newInstance(
                    elements.get(0).getClass(), elements.size());
            return elements.toArray(names);
        }

    }

    // TODO(bugabuga): figure out how it works with Chinese.
    public static String readFileContent(String path) throws IOException {
        if(new File(path).exists()){
            return new String(Files.readAllBytes(Paths.get(path)));
        }else{
            return "";
        }
    }

    public static void writeFile(String filepath, String content) throws FileNotFoundException, UnsupportedEncodingException {
        new File(filepath).getParentFile().mkdirs();
        System.out.println("filepath = " + filepath);
        PrintWriter writer = new PrintWriter(filepath, "UTF-8");
        writer.print(content);
        writer.close();
    }

    public static <T extends Comparable<? super T>> List<T> sorted(Collection<T> elements) {
        ArrayList<T> arr = new ArrayList<>(elements);
        Collections.sort(arr);
        return arr;
    }

    public static String relativePath(String basePath, String path) {
        return new File(basePath).toURI().relativize(new File(path).toURI()).getPath();
    }

    public static <T> Iterable<T> itemsAfter(List<T> elements, int index) {
        if(index + 1 < elements.size()) {
            return elements.subList(index + 1, elements.size());
        }else{
            return Arrays.asList();
        }
    }

    public static <T> Iterable<T> itemsBefore(List<T> elements, int index) {
        if(index > 0) {
            return elements.subList(0, index);
        }else{
            return Arrays.asList();
        }
    }


    @NotNull
    public static String getContentByLanguage(String suffix) {
        String newContent2 = "import re\n\n\ndef foo():\n    return re.compile('a')";
        if(suffix.equals("java")){
            newContent2 = "class A{\n    void foo(){}\n}";
        }
        return newContent2;
    }

    public static String readStream(InputStream requestBody) throws IOException {
        StringWriter writer = new StringWriter();
        IOUtils.copy(requestBody, writer, "base64");
        return writer.toString();
    }
}
