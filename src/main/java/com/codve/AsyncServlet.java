package com.codve;

import javax.servlet.AsyncContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet(
        name = "asyncServlet",
        urlPatterns = "/async",
        asyncSupported = true
)
public class AsyncServlet extends HttpServlet {
    private static volatile int ID = 1;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
        throws ServletException, IOException {
        final int id;
        synchronized (AsyncServlet.class) {
            id = ID ++;
        }
        String timeoutString = request.getParameter("timeout");
        long timeout;
        if (timeoutString == null) {
            timeout = 10_000L;
        } else {
            timeout = Integer.parseInt(timeoutString);
        }
        System.out.println("Entering AsyncServlet.doGet(), Request ID = " + id +
                ", isAsyncSupported = " + request.isAsyncSupported());

        String wrap = request.getParameter("wrap");
        final AsyncContext context;
        if (wrap != null) { // 使用包装
            context = request.startAsync(request, response);
        } else { // 使用原生
            context = request.startAsync();
        }
        context.setTimeout(timeout); // 设置异步请求超时

        System.out.println("Starting asynchronous thread, Request ID = " + id + ".");
        AsyncThread thread = new AsyncThread(id, context);
        context.start(thread::doWork);
        System.out.println("Leaving AsyncServlet.doGet(), Request ID = " + id +
                ", isAsyncStarted = " + request.isAsyncStarted());
    }

    private static class AsyncThread {
        private final int id;
        private final AsyncContext context;

        public AsyncThread(int id, AsyncContext context) {
            this.id = id;
            this.context = context;
        }

        public void doWork() {
            System.out.println("Asynchronous thread started. Request ID = " +
                    this.id + ".");
            try {
                Thread.sleep(5_000L);
            } catch (Exception e) {
                e.printStackTrace();
            }
            HttpServletRequest request =
                    (HttpServletRequest) this.context.getRequest();
            System.out.println("finish sleeping, Request ID = " + this.id +
                    ", URL = " + request.getRequestURL() + ".");
            this.context.dispatch("/WEB-INF/jsp/view/async.jsp");
            System.out.println("Asynchronous thread completed. Request ID = " +
                    this.id + ".");
        }
    }
}
