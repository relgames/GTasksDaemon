package org.relgames.gtasksdaemon

import javax.servlet.http.{HttpServlet, HttpServletRequest => HSReq, HttpServletResponse => HSResp}
import utils.Logging


class HelloServlet extends HttpServlet with Logging {
  override def doGet(req: HSReq, resp: HSResp) = {
    val hello =
      <html>
        <head>
          <title>Google Tasks Daemon</title>
        </head>
        <body>Hello</body>
      </html>

    resp.getWriter().print(hello)
  }
}