package com.imooc.sso.servlet;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;
import java.util.UUID;


public class LoginServlet extends HttpServlet {

    private String domains;

    @Override
    public void init(ServletConfig config) throws ServletException {
        super.init(config);
        domains = config.getInitParameter("domains");
    }

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        //此语句表示当request.getServletPath()的值是/login是就是登录操作
        if (Objects.equals("/login", request.getServletPath())){
            String username = request.getParameter("username");
            String password = request.getParameter("password");

            String source = request.getParameter("source");

            if (null == source || Objects.equals("", source)){
                String referer = request.getHeader("referer");
                source = referer.substring(referer.indexOf("source=") + 7);
            }

            // Objects类是Object类的帮助类：俩个参数的值如果相等就返回true，省去了判断俩个参数是否为空的过程
            if (Objects.equals(username, password)){
                // 通过UUID.randomUUID.toString()会得到一个由字母数字组成，用-连接的随机字符串，使用replace()方法去掉
                String ticket = UUID.randomUUID().toString().replace("-", "");
                System.out.println("****************************:" + ticket);
                response.sendRedirect(source + "main?ticket=" + ticket + "&domains="
                        + domains.replace(source + ",", "")
                        .replace("," + domains, "")
                        .replace(source, ""));
            } else {
                request.setAttribute("scource", source);
                request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
            }
        }else if (Objects.equals("/ssoLogin", request.getServletPath())){
            request.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(request, response);
        }
    }
}
