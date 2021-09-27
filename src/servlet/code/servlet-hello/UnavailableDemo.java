package cc.xizhan.demo.servlethello;

import javax.servlet.ServletException;
import javax.servlet.UnavailableException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 * 根据 Servlet 规范，servlet 处理响应过程中可以抛出 UnavailableException 异常，表示
 * 服务不可用，或者抛出 ServletException 表示出错，这里测试一下,
 * 访问 .../not-available，提供不同参数值，控制对应的行为
 */
@WebServlet(name = "not-available", value = "/not-available")
public class UnavailableDemo extends HttpServlet {
    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String msg = request.getParameter("message");
        if (msg == null) {
            sendMessage(response, "hello,world");
            return;
        }
        switch (msg) {
            case "error":
                throw new ServletException("error test");
            case "temp":
                throw new UnavailableException("unavailable test, temp", 3);
            case "perm":
                throw new UnavailableException("unavailable test, perm");
            default:
                sendMessage(response, "hello, world");
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        this.doGet(request, response);
    }

    private void sendMessage(HttpServletResponse response, String message) throws IOException {
        response.getWriter().println(message);
    }
}
/*
$ curl localhost:8080/servlet-hello/not-available
hello,world

$ curl localhost:8080/servlet-hello/not-available -d message=error
500 - 内部服务器错误

$ curl localhost:8080/servlet-hello/not-available -d message=temp  -v
< HTTP/1.1 503
< Retry-After: Mon, 27 Sep 2021 08:37:47 GMT
< Content-Type: text/html;charset=utf-8
< Content-Language: zh-CN
< Content-Length: 743
< Date: Mon, 27 Sep 2021 08:37:44 GMT
< Connection: close
$ curl localhost:8080/servlet-hello/not-available -d message
503
$ curl localhost:8080/servlet-hello/not-available -d message
hello, world
$ curl localhost:8080/servlet-hello/not-available -d message=perm
HTTP状态 404 - 未找到
之后再试都是 404
*/
