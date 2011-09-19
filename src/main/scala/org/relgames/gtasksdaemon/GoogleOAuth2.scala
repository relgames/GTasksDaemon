package org.relgames.gtasksdaemon

import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.client.googleapis.auth.oauth2.draft10.GoogleAccessProtectedResource
import utils.{Logging, Configuration}
import com.google.api.services.tasks.Tasks
import com.google.api.client.auth.oauth2.draft10.AccessProtectedResource
import com.google.api.client.http.GenericUrl

object GoogleOAuth2 extends Logging {
  val clientId = Configuration.getProperty("client.id")
  val clientSecret = Configuration.getProperty("client.secret")
  val refreshToken = Configuration.getProperty("refresh.token")
  val scope = Configuration.getProperty("scope")
  val REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob"

  val TRANSPORT = new NetHttpTransport
  val JSON_FACTORY = new JacksonFactory

  lazy val access: AccessProtectedResource = new GoogleAccessProtectedResource(
      "",
      TRANSPORT,
      JSON_FACTORY,
      clientId,
      clientSecret,
      refreshToken)

  def login() {
    access.refreshToken()
    log.info("Got new access token: {}", access.getAccessToken)
  }

  lazy val rf = GoogleOAuth2.TRANSPORT.createRequestFactory(GoogleOAuth2.access);

  def get(url:String) = rf.buildGetRequest(new GenericUrl(url)).execute().getContent

  val tasksService = new Tasks(TRANSPORT, access, JSON_FACTORY)

}