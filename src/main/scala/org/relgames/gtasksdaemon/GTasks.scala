package org.relgames.gtasksdaemon

import utils.Logging
import com.google.api.services.tasks.model.Task

object GTasks extends Logging {

  def tasks: Seq[String] = {
    val tasks = GoogleAPI.getTasks

    import scala.collection.JavaConversions._
    tasks.map(_.getTitle)
  }

  def addTask(taskTitle: String) {
    val task =  new Task
    task.setTitle(taskTitle)
    GoogleAPI.addTask(task)
  }

}