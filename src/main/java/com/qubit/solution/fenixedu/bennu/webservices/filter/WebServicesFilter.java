package com.qubit.solution.fenixedu.bennu.webservices.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import org.apache.commons.lang.StringUtils;

public class WebServicesFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        chain.doFilter(new SSLTerminationAwareRequestWrapper((HttpServletRequest) request), response);
    }

    @Override
    public void destroy() {

    }

    public static class SSLTerminationAwareRequestWrapper extends HttpServletRequestWrapper {

        public static final String X_FORWARD_PROTO = "x-forwarded-proto";
        public static final String X_FORWARD_PORT = "x-forwarded-port";

        private String protocol;
        private Integer port;

        public SSLTerminationAwareRequestWrapper(HttpServletRequest request) {
            super(request);
            this.protocol = request.getHeader(X_FORWARD_PROTO);
            String portNumber = request.getHeader(X_FORWARD_PORT);
            if (StringUtils.isNumeric(portNumber)) {
                port = Integer.valueOf(portNumber);
            }
        }

        @Override
        public String getScheme() {
            return StringUtils.isEmpty(protocol) ? super.getScheme() : protocol;
        }

        @Override
        public int getServerPort() {
            return this.port != null ? this.port : super.getServerPort();
        }

    }
}
