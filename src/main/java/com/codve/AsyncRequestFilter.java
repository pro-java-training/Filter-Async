package com.codve;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.IOException;

public class AsyncRequestFilter implements Filter {
    private String name;

    @Override
    public void init(FilterConfig config) {
        this.name = config.getFilterName();
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
        throws ServletException, IOException {
        System.out.println("Entering " + this.name + ".doFilter().");
        chain.doFilter(
                new HttpServletRequestWrapper((HttpServletRequest) request),
                new HttpServletResponseWrapper((HttpServletResponse) response)
        );
        if (request.isAsyncSupported() && request.isAsyncStarted()) {
            AsyncContext context = request.getAsyncContext();
            System.out.println("Leaving " + this.name + ".doFilter(), async " +
                    "context holds original request/response = " +
                    context.hasOriginalRequestAndResponse());
            // 如果是原生的request和response, 则返回true
        } else {
            System.out.println("Leaving " + this.name + ".doFilter().");
        }
    }

    @Override
    public void destroy() {

    }
}
