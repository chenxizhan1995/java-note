package cc.xizhan.demo.servlethello;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.Enumeration;

@WebServlet(name = "path", value = "/path")
public class PathDemo extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getCharacterEncoding() == null){
            request.setCharacterEncoding("UTF-8");
        }
        if (response.getCharacterEncoding().equals("ISO-8859-1")){
            response.setCharacterEncoding("UTF-8");
        }
        PrintWriter out = response.getWriter();
        out.println("requestURI:" + request.getRequestURI());
        out.println("requestURL:" + request.getRequestURL());
        out.println("contextPath:" + request.getContextPath());
        out.println("servletPath:" + request.getServletPath());
        out.println("pathInfo:" + request.getPathInfo());
        out.println("queryString:" + request.getQueryString());
        out.println();
        out.println("headers:");
        Enumeration<String> headerNames = request.getHeaderNames();
        while(headerNames.hasMoreElements()){
            String name = headerNames.nextElement();
            Enumeration<String> headers = request.getHeaders(name);
            while (headers.hasMoreElements()){
                out.println(name + ": " + headers.nextElement());
            }
        }
        out.println();
    }
}
