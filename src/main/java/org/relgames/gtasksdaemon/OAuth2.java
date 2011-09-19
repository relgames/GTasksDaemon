package org.relgames.gtasksdaemon;

import com.google.api.client.auth.oauth2.draft10.AccessTokenResponse;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessTokenRequest;
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAuthorizationRequestUrl;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.JsonFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * @author Oleg Poleshuk
 */
public class OAuth2 {
    private static final Logger log = LoggerFactory.getLogger(OAuth2.class);

    private static final String SCOPE = GoogleOAuth2.scope();
    private static final String REDIRECT_URL = GoogleOAuth2.REDIRECT_URL();

    private static final String CLIENT_ID = GoogleOAuth2.clientId();
    private static final String CLIENT_SECRET = GoogleOAuth2.clientSecret();

    private static final HttpTransport TRANSPORT = GoogleOAuth2.TRANSPORT();
    private static final JsonFactory JSON_FACTORY = GoogleOAuth2.JSON_FACTORY();

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

    public static void main(String[] args) throws IOException {
        Jul.replaceWithSLF4J();
        requestCode();
    }
}
