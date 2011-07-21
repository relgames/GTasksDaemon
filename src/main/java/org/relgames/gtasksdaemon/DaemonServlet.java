package org.relgames.gtasksdaemon;

import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * @author Oleg Poleshuk
 */
public class DaemonServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        throw new UnsupportedOperationException();
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/plain");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write("Current time: " + new DateTime().toString(DateTimeFormat.shortDateTime()) + "\n");
        if (QuartzJob.lastExecution!=null) {
            response.getWriter().write("Last execution: " + QuartzJob.lastExecution.toString(DateTimeFormat.shortDateTime()) + "\n");
        } else {
            response.getWriter().write("Not executed yet");
        }
    }
}
