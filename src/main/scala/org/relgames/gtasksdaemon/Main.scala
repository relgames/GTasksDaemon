package org.relgames.gtasksdaemon

import io.Source
import book.scala.beginning.JSON
import utils.{JSON2, Logging}
import xml.XML

object Main extends Logging {
  def main(args: Array[String]):Unit = {
    log.info("Start")

    val str = Source.fromFile("2.json").mkString
    log.info("Loaded file")

    val t1 = System.currentTimeMillis
    //val result = JSON.run(str)
    //val result = XML.loadString(str)
    val result = JSON2.fromString(str)
    val t2 = System.currentTimeMillis

    log.info("Parsed in {} ms: {}", t2-t1, result)

    log.info("Done")
  }

}