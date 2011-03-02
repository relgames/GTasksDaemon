package org.relgames.gtasksdaemon

import javax.servlet.http.{HttpServlet, HttpServletRequest => HSReq, HttpServletResponse => HSResp}
import GTasks._
import utils.Logging


class HelloServlet extends HttpServlet with Logging {
  override def doGet(req: HSReq, resp: HSResp) = {
    login()
    val taskList = tasks()
    log.info("Tasks: {}", taskList)

    val hello =
      <html>
        <head>
          <title>Google Tasks Daemon</title>
        </head>
        <body>{taskList.mkString(":")}</body>
      </html>

    resp.getWriter().print(hello)
  }
}