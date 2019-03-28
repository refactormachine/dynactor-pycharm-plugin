package sender;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;

public class HttpsSender implements Sender{
    public static final String ERROR_RESPONSE = "!Error!";
    private final String httpsURL;
    private final String authorization;

    public HttpsSender(String httpsURL, String authorization){
        this.httpsURL = httpsURL;
        this.authorization = authorization;
    }

    private String httpsPost(StringEntity json) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(httpsURL);
        post.setHeader("Authorization", authorization);
        post.addHeader("content-type", "application/json");
        post.addHeader("Accept","application/json");
        post.setEntity(json);
        HttpResponse response;
        try {
             response = client.execute(post);
        }catch(Exception e){
            e.printStackTrace();
            return ERROR_RESPONSE;
        }
        InputStream x = response.getEntity().getContent();
        return Utils.readStream(x, StandardCharsets.UTF_8);
    }

    @Override
    public void sendMessage(JSONObject message) {
        String messageString = message.toString();
        try {
            httpsPost(new StringEntity(messageString));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
