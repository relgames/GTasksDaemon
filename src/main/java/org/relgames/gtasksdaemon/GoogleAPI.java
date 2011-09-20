package org.relgames.gtasksdaemon;

import com.google.api.client.auth.oauth2.draft10.AccessProtectedResource;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.GenericUrl;
import com.google.api.client.http.HttpRequestFactory;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.Task;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Properties;

/**
 * @author Oleg Poleshuk
 */
public class GoogleAPI {
    private static final Logger log = LoggerFactory.getLogger(GoogleAPI.class);

    public static final Properties config;

    static {
        config  =  new Properties();
        try {
            config.load(GoogleAPI.class.getClassLoader().getResourceAsStream("auth.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Jul.replaceWithSLF4J();
    }

    private static final String CLIENT_ID = config.getProperty("client.id");
    private static final String CLIENT_SECRET = config.getProperty("client.secret");
    private static final String REFRESH_TOKEN = config.getProperty("refresh.token");
    private static final String SCOPE = config.getProperty("scope");
    private static final String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";

    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    private static final AccessProtectedResource access = new GoogleAccessProtectedResource(
        "",
        TRANSPORT,
        JSON_FACTORY,
        CLIENT_ID,
        CLIENT_SECRET,
        REFRESH_TOKEN);

    private static final HttpRequestFactory rf = TRANSPORT.createRequestFactory(access);

    public static void requestCode() throws IOException {
        String authorizationUrl = new GoogleAuthorizationRequestUrl(CLIENT_ID, REDIRECT_URL, SCOPE).build();
        log.info("Go to the following link in your browser:");
        log.info(authorizationUrl);

        log.info("Type the code you received here: ");
        BufferedReader in = new BufferedReader(new InputStreamReader(System.in));
        String authorizationCode = in.readLine();

        AccessTokenResponse response = new GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant(
                TRANSPORT,
                JSON_FACTORY,
                CLIENT_ID,
                CLIENT_SECRET,
                authorizationCode,
                REDIRECT_URL).execute();

        log.info("Refresh token: {}", response.refreshToken);
    }

    public static void login() {
        try {
            access.refreshToken();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        log.info("Got new access token: {}", access.getAccessToken());
    }


    public static InputStream get(String url) {
        try {
            return rf.buildGetRequest(new GenericUrl(url)).execute().getContent();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static final Tasks tasksService = new Tasks(TRANSPORT, access, JSON_FACTORY);

    public static List<Task> getTasks() {
        try {
            return tasksService.tasks.list("@default").execute().getItems();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void addTask(Task task) {
        try {
            tasksService.tasks.insert("@default", task).execute();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws IOException {
        requestCode();
    }
}
