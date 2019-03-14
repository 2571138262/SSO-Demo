package com.imooc.sso.servlet;

import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;

import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MianSevlet extends HttpServlet {

    private ExecutorService service = Executors.newFixedThreadPool(10);

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // request.getServletPath()获取能够与“url-pattern”中匹配的路径，注意是完全匹配的部分，*的部分不包括
        if (Objects.equals("/main", request.getServletPath())){
            String domains = request.getParameter("domains");
            String ticket = request.getParameter("ticket");
            for(String server : domains.split(",")){
                if (!Objects.equals(null, server) && !Objects.equals("", server.trim())){
                    // 该方法的作用就是为server参数表示的应用设置cookie也就是一个应用登录后，会调用setCookie方法为另一个应用设置Cookie
                    setCookie(server, ticket);
                }
            }
            request.getRequestDispatcher("WEB-INF/views/main.jsp").forward(request, response);
        }else if(Objects.equals("/setCookie", request.getServletPath())){
            // 也就是说，当一个应用登录成功后调用setCookie()方法，会触发一个请求的执行，就会执行另一个应用这段else if后的代码]
            String ticket = request.getParameter("ticket");
            response.addCookie(new Cookie("Tickect_Granting_Ticket", ticket));
            response.setCharacterEncoding("utf-8");
            response.setContentType("application/text; charset=utf-8");
            PrintWriter out = null;
            try {
                out = response.getWriter();
                out.write("ok");//ok的值会在另一个应用的setCookie方法中获得，得到ok代表cookie添加成功了
            } catch (IOException e) {
                e.printStackTrace();
            }finally {
                if (null != out){
                    out.close();
                }
            }
        }
    }

    private void setCookie(String server, String ticket){
        //submit方法的作用是向线程池提交一个Runnable任务用于执行
        service.submit(new Runnable() {
            @Override
            public void run() {
                //该语句的作用是：创建请求方法的实例，并指定请求的URL。如果需要发送post请求，就创建HttpPost对象
                HttpPost httpPost = new HttpPost(server + "setCookie?ticket=" + ticket);
                CloseableHttpClient httpClient = null;
                CloseableHttpResponse response = null;
                try {
                    httpClient = HttpClients.createDefault();
                    // 将请求发出去了，就会触发另一个应用的Servlet中的servletPath为/setCookie的代码的执行，也就是为了另一个应用添加了cookie
                    response = httpClient.execute(httpPost);
                    //此处的entity 就是添加cookie的同时存放到response中的ok， 得到该值就说明cookie添加成功了
                    HttpEntity entity = response.getEntity();
                    String responseContent = EntityUtils.toString(entity, "utf-8");
                    System.out.println("=================================" + responseContent);
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    if (null != response){
                        try {
                            response.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    if (null != null){
                        try {
                            httpClient.close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
    }
}
