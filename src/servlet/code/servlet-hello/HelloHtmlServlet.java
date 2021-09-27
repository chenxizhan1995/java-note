// 导入Java库
import java.io.*;
import javax.servlet.*;
import javax.servlet.http.*;

public class HelloHtmlServlet extends HttpServlet {
    public void doGet(HttpServletRequest request,
                      HttpServletResponse response)
          throws ServletException, IOException{
          // 设置相应内容类型
          response.setContentType("text/html");
          // 返回响应数据
          PrintWriter writer = response.getWriter();
          writer.println("<h1> Hello, World</h1>");
    }
}
