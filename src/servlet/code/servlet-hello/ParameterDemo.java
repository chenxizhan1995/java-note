package cc.xizhan.demo.servlethello;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.annotation.*;
import java.io.IOException;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.logging.Logger;

@WebServlet(name = "parameter-demo", value = "/parameter-demo")
public class ParameterDemo extends HttpServlet {
    private static Logger logger = Logger.getLogger(ParameterDemo.class.getCanonicalName());
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Enumeration<String> names = request.getParameterNames();
        StringBuilder sb = new StringBuilder();
        while (names.hasMoreElements()){
            String name = names.nextElement();
            String valueStr = Arrays.asList(request.getParameterValues(name)).toString();
            sb.append("\n").append(name).append(":").append(valueStr);
            logger.info(name + ":" + valueStr);
        }
        response.getWriter().println(sb.toString());
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }
}
/**
$ curl "localhost:8080/servlet-hello/parameter-demo?a=foo&b=bar"   -d a=bye -d a=ok -d hello=world

a:[foo, bye, ok]
b:[bar]
hello:[world]

$ curl "localhost:8080/servlet-hello/parameter-demo" -d a=x -d a -d hello

a:[x, ]
hello:[]
*/
