package sender;

import com.intellij.openapi.util.Pair;
import com.sun.net.httpserver.*;
import com.sun.tools.corba.se.idl.constExpr.GreaterEqual;
import org.jetbrains.annotations.NotNull;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.Assert;
import org.junit.Before;
import util.Utils;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLParameters;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public abstract class TestWithServerMock {
    protected List<JSONObject> getMessagesReceived() {
        return messagesReceived.stream().
        map(p -> p.getSecond()).collect(Collectors.toList());
    }

    protected void assertAuthIsCorrect() {
        if(messagesReceived.isEmpty()){
            return;
        }
        Set<String> passwords = messagesReceived.stream().map(
                p -> p.getFirst()
        ).collect(Collectors.toSet());
        Set<String> passwordSet = Stream.of(getPassword()).collect(Collectors.toSet());
        Assert.assertEquals(passwordSet, passwords);
    }

    protected String getErroneousMessage() {
        return erroneousMessage;
    }

    public static final String MY_KEYSTORE_FNAME = "my_keystore.keystore";

    private List<Pair<String, JSONObject>> messagesReceived = new ArrayList<>();
    private String erroneousMessage = "";


    protected abstract int getPort();

    @Before
    public void setupServer() {
        messagesReceived.clear();
        erroneousMessage = "";
        listenOnPort(getPort());
    }


    private void listenOnPort(int port) {
        Server s = new Server(port, this::handleRequest);
        URL keystoreURL = getUrl();
        s.listen(keystoreURL);
    }


    @NotNull
    private URL getUrl() {
        ClassLoader clsLoader = getClass().getClassLoader();
        String setupScriptPath = clsLoader.getResource("setup_truststore.sh").getPath();
        String resourcesBase = new File(setupScriptPath).getParent();
        URL keystoreURL = clsLoader.getResource(MY_KEYSTORE_FNAME);

        String errorMessage = "No keystore setup in the jdk truststore !\n" +
                "To fix this, execute:\n" +
                "\tcd " + resourcesBase + "\n" +
                "\tsource setup_truststore.sh";
        Assert.assertNotNull(errorMessage, keystoreURL);
        return keystoreURL;
    }

    private String handleRequest(HttpExchange request) {
        String content = "";
        try {
            content = Utils.readStream(request.getRequestBody(), StandardCharsets.UTF_8);
            Object obj = new JSONParser().parse(content);
            List<String> auth = request.getRequestHeaders().get("Authorization");
            String authString = "";
            if (auth != null && auth.size() == 1) {
                authString = auth.get(0);
            }
            messagesReceived.add(Pair.pair(
                    authString, (JSONObject) obj));
            return "good";
        } catch (IOException e) {
            assert false;
        } catch (ParseException e) {
            e.printStackTrace();
            erroneousMessage = content;
        }
        return "bad";
    }

    @NotNull
    protected Sender createSender() {
        String url = String.format("https://localhost:%d/message", getPort());
        return new HttpsSender(url, getPassword());
    }

    protected String getPassword() {
        return "username:password";
    }
}

interface SimpleHandler {
    String response(HttpExchange request);
}

class Server {

    private final int port;
    private final SimpleHandler handler;

    public Server(int port, SimpleHandler handler) {
        this.port = port;
        this.handler = handler;
    }

    public void listen(URL keystore_filename) {
        HttpsServer server = null;
        SSLContext sslContext = null;
        try {
            server = HttpsServer.create(new InetSocketAddress(port), 0);
            sslContext = SSLContext.getInstance("TLS");
            char[] password = "password123".toCharArray();
            KeyStore ks = KeyStore.getInstance("JKS");

            InputStream fis = keystore_filename.openStream();
            ks.load(fis, password);

            KeyManagerFactory kmf = KeyManagerFactory.getInstance("SunX509");
            kmf.init(ks, password);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance("SunX509");
            tmf.init(ks);

            sslContext.init(kmf.getKeyManagers(), tmf.getTrustManagers(), null);

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
            HttpsExchange s = (HttpsExchange) exchange;
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
