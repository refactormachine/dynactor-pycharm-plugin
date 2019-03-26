package sender;

import com.sun.net.httpserver.*;
import org.junit.*;
import org.junit.Assert;
import util.Utils;

import javax.net.ssl.*;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.security.KeyStore;
import java.util.concurrent.Executors;



public class HttpsSenderTest {

    public static final String MY_KEYSTORE_FNAME = "my_keystore.keystore";

    @Test
    public void testSendMessage(){
        int port = 8801;
        listenOnPort(port);

        String url = String.format("https://localhost:%d/foo", port);
        HttpsSender s = new HttpsSender(url, "goo:do");
        s.sendMessage("moshe", "content");

//        assert getMessgeKind() == "moshe" && getMessageContent == "content";
    }



    private void listenOnPort(int port) {
        Server s = new Server(port, request -> {
//            try {
//                String content = Utils.readStream(request.getRequestBody());
//                return content;
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
            return "response";
        });
        ClassLoader clsLoader = getClass().getClassLoader();
        String setupScriptPath = clsLoader.getResource("setup_truststore.sh").getPath();
        String resourcesBase = new File(setupScriptPath).getParent();
        URL keystoreURL = clsLoader.getResource(MY_KEYSTORE_FNAME);

        String errorMessage = "No keystore setup in the jdk truststore !\n" +
                "To fix this, execute:\n" +
                "\tcd " + resourcesBase + "\n" +
                "\tsource setup_truststore.sh";
        Assert.assertNotNull(errorMessage, keystoreURL);

        s.listen(keystoreURL);
    }
}
interface SimpleHandler{
    String response(HttpExchange request);
}

class Server{

    private final int port;
    private final SimpleHandler handler;

    public Server(int port, SimpleHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void listen(URL keystore_filename){
        HttpsServer server = null;
        SSLContext sslContext = null;
        try {
            server = HttpsServer.create(new InetSocketAddress(port), 0);
            sslContext = SSLContext.getInstance("TLS");
            char[] password = "password123".toCharArray();
            KeyStore ks = KeyStore.getInstance ("JKS");

            InputStream fis = keystore_filename.openStream();
            ks.load ( fis, password );

            KeyManagerFactory kmf = KeyManagerFactory.getInstance ( "SunX509" );
            kmf.init ( ks, password );

            TrustManagerFactory tmf = TrustManagerFactory.getInstance ( "SunX509" );
            tmf.init ( ks );

            sslContext.init ( kmf.getKeyManagers (), tmf.getTrustManagers (), null );

        } catch (Exception e) {
            e.printStackTrace();
        }

        HttpsConfigurator httpsConfigurator = new HttpsConfigurator(sslContext) {
            @Override
            public void configure(HttpsParameters httpsParameters) {
                SSLContext sslContext = getSSLContext();
                SSLParameters defaultSSLParameters = sslContext.getDefaultSSLParameters();
                httpsParameters.setSSLParameters(defaultSSLParameters);
            }
        };
        assert server != null;
        server.createContext("/", exchange -> {
            HttpsExchange s = (HttpsExchange)exchange;
            s.getSSLSession();
            String response = handler.response(exchange);
            exchange.sendResponseHeaders(200, response.length());
            OutputStream os = exchange.getResponseBody();
            os.write(response.getBytes());
            os.close();
        });
        server.setExecutor(Executors.newCachedThreadPool());
        System.out.println("Starting server on port " + port + "...");
        server.setHttpsConfigurator(httpsConfigurator);
        server.start();
        System.out.println("Server started successfully!");
    }
}
