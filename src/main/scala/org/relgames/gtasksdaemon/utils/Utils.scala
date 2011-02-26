package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory
import java.net.URLEncoder

trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}

class EncodableMap(m: Map[String, String]) {
  def urlEncode: String = {
    for ((k, v) <- m) yield URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")
  }  mkString "&"
}

object EncodableMap {
  implicit def encodableMap(m: Map[String, String]): EncodableMap = new EncodableMap(m)
}
