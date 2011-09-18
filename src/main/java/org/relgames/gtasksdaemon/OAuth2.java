package org.relgames.gtasksdaemon;

import com.google.api.client.auth.oauth2.draft10.AccessProtectedResource;
import com.google.api.client.auth.oauth2.draft10.AccessTokenRequest.AuthorizationCodeGrant;
import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.auth.oauth2.draft10.AuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson.JacksonFactory;
import com.google.api.services.tasks.Tasks;
import com.google.api.services.tasks.model.TaskList;
import com.google.api.services.tasks.model.TaskLists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.bridge.SLF4JBridgeHandler;

import java.io.IOException;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;

/**
 * @author Oleg Poleshuk
 */
public class OAuth2 {
    private static final String SCOPE = "https://www.googleapis.com/auth/tasks";
    private static final String AUTH_URL = "https://accounts.google.com/o/oauth2/auth";
    private static final String REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob";

    private static final String CLIENT_ID = "495463767429.apps.googleusercontent.com";
    private static final String CLIENT_SECRET = "yNuz9Iq1M4JZm61TAb2sVTu0";

    private static final HttpTransport TRANSPORT = new NetHttpTransport();
    private static final JsonFactory JSON_FACTORY = new JacksonFactory();

    public static void requestCode() throws IOException {
        AuthorizationRequestUrl builder = new AuthorizationRequestUrl(AUTH_URL, CLIENT_ID);
        builder.redirectUri = REDIRECT_URL;
        builder.scope = SCOPE;

        String authorizationUrl = builder.build();
        System.out.println("Go to the following link in your browser:");
        System.out.println(authorizationUrl);
    }

    private static final String CODE = "4/hMGkDFvZyGAGI2S28qydorJb4FmI";

    private static final Logger log = LoggerFactory.getLogger(OAuth2.class);

    public static void main(String[] args) throws IOException {
        java.util.logging.Logger rootLogger = LogManager.getLogManager().getLogger("");
        rootLogger.setLevel(Level.ALL);
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }
        SLF4JBridgeHandler.install();

        log.debug("OAuth2");

        //requestCode();

        AccessTokenResponse response = new AuthorizationCodeGrant(
                TRANSPORT,
                JSON_FACTORY,
                AUTH_URL,
                CLIENT_ID,
                CLIENT_SECRET,
                CODE,
                REDIRECT_URL).execute();

        AccessProtectedResource accessProtectedResource = new AccessProtectedResource(
                response.accessToken,
                AccessProtectedResource.Method.AUTHORIZATION_HEADER,
                TRANSPORT,
                JSON_FACTORY,
                AUTH_URL,
                CLIENT_ID,
                CLIENT_SECRET,
                response.refreshToken);

        Tasks service = new Tasks(TRANSPORT, accessProtectedResource, JSON_FACTORY);

        TaskLists taskLists = service.tasklists.list().execute();

        for (TaskList taskList : taskLists.getItems()) {
            System.out.println(taskList.getTitle());
        }
    }

}
