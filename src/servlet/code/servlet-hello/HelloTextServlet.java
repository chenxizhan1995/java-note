import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HelloTextServlet extends HttpServlet{

    public void doGet(HttpServletRequest request,
                    HttpServletResponse response)
                    throws ServletException, IOException{
        response.setContentType("text/plain");

        PrintWriter out = response.getWriter();
        out.println("Hello, World");
    }
    public void doPost(HttpServletRequest request,
                      HttpServletResponse response)
                      throws ServletException, IOException{
        this.doGet(request, response);
    }
}
