package cc.xizhan.demo.servlethello;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import java.util.Enumeration;
import java.util.logging.Logger;

@WebServlet(name = "encoding-test", value = "/encoding-test")
public class EncodingTest extends HttpServlet {
    private String reqEnc = null;
    private String resEnc = null;
    private static Logger logger = Logger.getLogger(EncodingTest.class.getCanonicalName());

    @Override
    public void init() throws ServletException {
        super.init();
        logger.info("Sytem Default Charset is:" + Charset.defaultCharset().toString());
    }

    @Override
    protected void doGet(HttpServletRequest request
            , HttpServletResponse response) throws ServletException, IOException {
        this.doPost(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // 调整解析请求使用的编码
        this.setReqEnc(request);

        // 读取所有请求参数
        StringBuilder sb = new StringBuilder();
        Enumeration<String> params = request.getParameterNames();
        while (params.hasMoreElements()) {
            String name = params.nextElement();
            String value = request.getParameter(name);
            sb.append(name + ":" + value).append("\n");
            logger.info(name + ":" + value);
            System.out.println(name + ":" + value);
        }
        if (sb.length() > 0) {
            sb.delete(sb.length() - 1, sb.length());
        }

        // 调整响应编码
        setResEnc(response);
        // 返回响应数据
        response.setContentType("text/plain");
        response.getWriter().print(sb.toString());
        // 如果有设置编码的指令，就调整编码
        changeReqEncoding(request.getParameter("request-encoding"));
        changeResEncoding(request.getParameter("response-encoding"));
    }

    // 调整解析请求使用的编码
    private void setReqEnc(HttpServletRequest request) throws UnsupportedEncodingException {
        if (this.reqEnc != null) {
            request.setCharacterEncoding(this.reqEnc);
            logger.info("set request encoding to:" + this.reqEnc);
        } else {
            logger.info("current request encoding is default encoding");
        }
    }

    // 调整响应编码
    private void setResEnc(HttpServletResponse res) {
        if (this.resEnc != null) {
            res.setCharacterEncoding(this.resEnc);
            logger.info("set response encoding to:" + this.resEnc);
        } else {
            logger.info("current response encoding is default encoding");
        }
    }

    private void changeReqEncoding(String encoding) {
        if (encoding == null) return;
        encoding = encoding.toLowerCase();
        switch (encoding) {
            case "reset":
                this.reqEnc = null;
                break;
            default:
                this.reqEnc = encoding;
        }
        logger.info("change request encoding to:" + encoding);
    }

    private void changeResEncoding(String encoding) {
        if (encoding == null) return;
        encoding = encoding.toLowerCase();
        switch (encoding) {
            case "reset":
                this.resEnc = null;
                break;
            default:
                this.resEnc = encoding;
        }
        logger.info("change response encoding to:" + encoding);
    }
}
