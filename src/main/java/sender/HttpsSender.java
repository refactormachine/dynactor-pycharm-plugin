package sender;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.simple.JSONObject;
import util.Utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class HttpsSender implements Sender{
    private final String httpsURL;
    private final String authorization;

    public HttpsSender(String httpsURL, String authorization){
        this.httpsURL = httpsURL;
        this.authorization = authorization;
    }

    private void httpsPost(StringEntity json) throws IOException {
        HttpClient client = HttpClientBuilder.create().build();
        HttpPost post = new HttpPost(httpsURL);
        post.setHeader("Authorization", authorization);
        post.addHeader("content-type", "application/json");
        post.addHeader("Accept","application/json");
        post.setEntity(json);
        HttpResponse response = null;
        try {
             response = client.execute(post);
        }catch(Exception e){
            e.printStackTrace();
        }

        BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = rd.readLine()) != null) {
            result.append(line);
        }
        System.out.println("result = " + result);
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
