package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory
import javax.xml.parsers.{SAXParser, SAXParserFactory}
import java.io.OutputStreamWriter
import io.Source
import java.net.{URLConnection, URL, URLEncoder}

trait Logging {
  val log = LoggerFactory.getLogger(this.getClass)
}

object Http extends Logging{
  private class EncodableMap(m: Map[String, String]) {
    def urlEncode: String = {
      for ((k, v) <- m) yield URLEncoder.encode(k, "UTF-8") + "=" + URLEncoder.encode(v, "UTF-8")
    }  mkString "&"
  }

  private implicit def encodableMap(m: Map[String, String]): EncodableMap = new EncodableMap(m)

  def cookies = _cookies

  private var _cookies = Map[String, String]()

  def parseCookie(header: String): (String, String) = {
    val regexp = """([a-zA-Z0-9]+)=([^;]*);.*""".r
    val regexp(key, value) = header
    (key, value)
  }

  def storeCookies(cookieHeaders: java.util.List[String]): Unit = {
    if (cookieHeaders!=null) {
      import collection.JavaConversions._
      for (s <- cookieHeaders) {
        _cookies += parseCookie(s)
      }
    }
  }

  def storeCookies(connection: URLConnection): Unit = {
    storeCookies(connection.getHeaderFields.get("Set-Cookie"))
    storeCookies(connection.getHeaderFields.get("Set-Cookie2"))
  }

  def get(url: String): String = {
    val connection = new URL(url).openConnection
    connection.setDoOutput(true)

    storeCookies(connection)

    Source.fromInputStream(connection.getInputStream).mkString
  }

  def post(url: String, params: Map[String, String]): String = {
    val connection = new URL(url).openConnection
    connection.setDoOutput(true)

    val writer = new OutputStreamWriter(connection.getOutputStream)
    writer.write(params.urlEncode)
    writer.flush

    val result = Source.fromInputStream(connection.getInputStream).mkString

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