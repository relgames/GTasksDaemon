package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory
import javax.xml.parsers.{SAXParser, SAXParserFactory}
import java.net.{URL, URLEncoder}
import java.io.OutputStreamWriter
import io.Source

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

object Http {
  import EncodableMap._

  def post(url: String, params: Map[String, String]): String = {
    val postConnection = new URL(url).openConnection
    postConnection.setDoOutput(true)

    val writer = new OutputStreamWriter(postConnection.getOutputStream)
    writer.write(params.urlEncode)
    writer.flush

    val result = Source.fromInputStream(postConnection.getInputStream).mkString

    //postConnection.getInputStream.close

    result
  }
}

/**
 * Contains definition of a custom SAX parser with a set of options to prevent external DTD loading
 * http://stackoverflow.com/questions/1096285/is-scala-java-not-respecting-w3-excess-dtd-traffic-specs
 * http://lampsvn.epfl.ch/trac/scala/ticket/2725
 */
object DTDFix {
  def parser:SAXParser = {
    val f = SAXParserFactory.newInstance()
    f.setNamespaceAware(false)
    //f.setFeature("http://apache.org/xml/features/disallow-doctype-decl", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
    f.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
    f.newSAXParser()
  }

}