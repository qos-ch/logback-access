package ch.qos.logback.access.jetty12.blackbox;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

public class FormServlet extends HttpServlet {
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse response) throws ServletException, IOException {
        req.getParameterNames(); // Throws BadMessageException if form invalid
        response.setStatus(200);
        response.getWriter().print("OK");
    }
}
