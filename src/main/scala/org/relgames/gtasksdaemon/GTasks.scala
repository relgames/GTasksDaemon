package org.relgames.gtasksdaemon

import utils.{Configuration, Logging}
import com.google.api.client.http.javanet.NetHttpTransport
import com.google.api.client.json.jackson.JacksonFactory
import com.google.api.client.googleapis.auth.oauth2.draft10.{GoogleAccessProtectedResource, GoogleAccessTokenRequest}
import com.google.api.services.tasks.Tasks
import com.google.api.services.tasks.model.Task

object GTasks extends Logging {

  def tasks: Seq[String] = {
    val tasks = GoogleOAuth2.tasksService.tasks.list("@default").execute()

    import scala.collection.JavaConversions._
    tasks.getItems.map(_.getTitle)
  }

  def addTask(taskTitle: String) {
    val task =  new Task
    task.setTitle(taskTitle)
    GoogleOAuth2.tasksService.tasks.insert("@default", task).execute()
  }

}