package org.relgames.gtasksdaemon.utils

import org.slf4j.LoggerFactory
import javax.xml.parsers.{SAXParser, SAXParserFactory}
import java.io.OutputStreamWriter
import io.Source
import java.net.{HttpURLConnection, URLConnection, URL, URLEncoder}

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

  var cookies = Map[String, String]()

  def parseCookie(header: String): (String, String) = {
    val regexp = """([a-zA-Z0-9]+)=([^;]*);.*""".r
    val regexp(key, value) = header
    (key, value)
  }

  def saveCookies(cookieHeaders: java.util.List[String]): Unit = {
    if (cookieHeaders!=null) {
      import collection.JavaConversions._
      for (s <- cookieHeaders) {
        cookies += parseCookie(s)
      }
    }
  }

  def saveCookiesFrom(connection: URLConnection): Unit = {
    saveCookies(connection.getHeaderFields.get("Set-Cookie"))
    saveCookies(connection.getHeaderFields.get("Set-Cookie2"))
  }

  def mkCookieHeader(m: Map[String, String]): String = {
    m.map{ case (k, v) => k + "=" + v }.mkString("; ")
  }

  def addCookiesTo(connection: URLConnection): Unit = {
    connection.setDoOutput(true)
    connection.addRequestProperty("Cookie", mkCookieHeader(cookies))
  }

  def get(url: String): String = {
    val connection = new URL(url).openConnection
    addCookiesTo(connection)
    saveCookiesFrom(connection)

    Source.fromInputStream(connection.getInputStream).mkString
  }

  def post(url: String, params: Map[String, String]): String = {
    val connection:HttpURLConnection = new URL(url).openConnection match{ case c:HttpURLConnection => c}
    connection.setDoOutput(true)
    connection.setRequestMethod("POST")

    val query = params.urlEncode
    connection.setRequestProperty("Content-Length", query.length.toString)
    addCookiesTo(connection)

    val writer = new OutputStreamWriter(connection.getOutputStream)
    writer.write(query)
    writer.flush

    saveCookiesFrom(connection)
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