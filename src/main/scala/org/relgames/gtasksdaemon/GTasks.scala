package org.relgames.gtasksdaemon

import utils.{Configuration, Logging}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.client.googleapis.auth.oauth2.draft10.{GoogleAccessProtectedResource, GoogleAccessTokenRequest}
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task

object GTasks extends Logging {
  val clientId = Configuration.getProperty("client.id")
  val clientSecret = Configuration.getProperty("client.secret")

  val token = Configuration.getProperty("token")

  val SCOPE = "https://www.googleapis.com/auth/tasks"
  val REDIRECT_URL = "urn:ietf:wg:oauth:2.0:oob"
  val TRANSPORT = new NetHttpTransport
  val JSON_FACTORY = new JacksonFactory

  var service: Tasks = null

  def login() {
    val response = new GoogleAccessTokenRequest.GoogleAuthorizationCodeGrant(
      TRANSPORT,
      JSON_FACTORY,
      clientId,
      clientSecret,
      token,
      REDIRECT_URL).execute


    val accessProtectedResource = new GoogleAccessProtectedResource(
      response.accessToken,
      TRANSPORT,
      JSON_FACTORY,
      clientId,
      clientSecret,
      response.refreshToken)

    service = new Tasks(TRANSPORT, accessProtectedResource, JSON_FACTORY)

    log.info("Logged in")
  }

  def tasks: Seq[String] = {
    if (service==null) login()

    val tasks = service.tasks.list("@default").execute()

    import scala.collection.JavaConversions._
    tasks.getItems.map(_.getTitle)
  }

  def addTask(task: String) {
    if (service==null) login()
  }

}