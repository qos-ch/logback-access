package ch.qos.logback.access.jetty;

import ch.qos.logback.access.common.spi.WrappedHttpRequest;
import jakarta.servlet.AsyncContext;
import jakarta.servlet.DispatcherType;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletConnection;
import jakarta.servlet.ServletContext;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.HttpUpgradeHandler;
import jakarta.servlet.http.Part;
import org.eclipse.jetty.http.HttpCookie;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.Fields;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.Collection;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.qos.logback.access.common.spi.IAccessEvent.NA;
import static ch.qos.logback.access.jetty.HeaderUtil.buildHeaderMap;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RequestWrapper implements HttpServletRequest, WrappedHttpRequest {

    static final Cookie[] EMPTY_COOKIE_ARRAY = new Cookie[0];
    static final String[] EMPTY_STRING_ARRAY = new String[0];

    Request request;
    StringBuffer requestURL;

    public RequestWrapper(Request request) {
        this.request = request;
    }

    @Override
    public String getAuthType() {
        return null;
    }

    @Override
    public Cookie[] getCookies() {
        List<HttpCookie> httpCookies = Request.getCookies(request);
        List<Cookie> cookieList = httpCookies.stream().map(httpCookie -> new Cookie(httpCookie.getName(), httpCookie.getValue())).collect(
                Collectors.toList());

        return  cookieList.toArray(EMPTY_COOKIE_ARRAY);
    }

    @Override
    public long getDateHeader(String name) {
        return 0;
    }

    @Override
    public String getHeader(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return null;
    }

    @Override
    public Enumeration<String> getHeaderNames() {

        return null;
    }

    @Override
    public Map<String, String> buildRequestHeaderMap() {
        return buildHeaderMap(request.getHeaders());
    }

    @Override
    public int getIntHeader(String name) {
        return 0;
    }

    @Override
    public String getMethod() {
        return request.getMethod();
    }

    @Override
    public String getPathInfo() {
        return null;
    }

    @Override
    public String getPathTranslated() {
        return null;
    }

    @Override
    public String getContextPath() {
        return null;
    }

    @Override
    public String getQueryString() {
        return request.getHttpURI().getQuery();
    }

    @Override
    public String getRemoteUser() {
        return null;
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    @Override
    public Principal getUserPrincipal() {
        return null;
    }

    @Override
    public String getRequestedSessionId() {
        return null;
    }

    @Override
    public String getRequestURI() {
        return request.getHttpURI().getPath();
    }

    @Override
    public StringBuffer getRequestURL() {
        if (requestURL == null) {
            String result = request.getHttpURI().asString();
            requestURL = new StringBuffer(result);
        }
        return requestURL;
    }

    @Override
    public String getServletPath() {
        return null;
    }

    @Override
    public String getSessionID() {
        Session session = request.getSession(false);
        if (session == null) {
            return NA;
        } else {
            return session.getId();
        }
    }

    @Override
    public HttpSession getSession(boolean create) {
        throw new UnsupportedOperationException();
    }

    @Override
    public HttpSession getSession() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String changeSessionId() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdValid() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isRequestedSessionIdFromCookie() {
        return false;
    }

    @Override
    public boolean isRequestedSessionIdFromURL() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean authenticate(HttpServletResponse response) throws IOException, ServletException {
        return false;
    }

    @Override
    public void login(String username, String password) throws ServletException {

    }

    @Override
    public void logout() throws ServletException {

    }

    @Override
    public Collection<Part> getParts() throws IOException, ServletException {
        return null;
    }

    @Override
    public Part getPart(String name) throws IOException, ServletException {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) throws IOException, ServletException {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        Set<String> attributeNamesSet = request.getAttributeNameSet();
        return Collections.enumeration(attributeNamesSet);
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) throws UnsupportedEncodingException {

    }

    @Override
    public int getContentLength() {
        return 0;
    }

    @Override
    public long getContentLengthLong() {
        return 0;
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        return null;
    }

    @Override
    public Map<String, String[]> buildRequestParameterMap() {
        Map<String, String[]> results = new HashMap<>();
        Fields allParameters = Request.extractQueryParameters(request, UTF_8);
        for (Fields.Field field : allParameters) {
           results.put(field.getName(), field.getValues().toArray(EMPTY_STRING_ARRAY));
        }
        return results;
    }

    @Override
    public String getParameter(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Enumeration<String> getParameterNames() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String[] getParameterValues(String name) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        throw new UnsupportedOperationException();
    }

    @Override
    public String getProtocol() {
        return request.getConnectionMetaData().getProtocol();
    }

    @Override
    public String getScheme() {
        return request.getHttpURI().getScheme();
    }

    @Override
    public String getServerName() {
        return Request.getServerName(request);
    }

    @Override
    public int getServerPort() {
        return Request.getServerPort(request);
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return null;
    }

    @Override
    public String getRemoteAddr() {
        return Request.getRemoteAddr(request);
    }

    @Override
    public String getRemoteHost() {
        return Request.getRemoteAddr(request);
    }

    @Override
    public void setAttribute(String name, Object o) {

    }

    @Override
    public void removeAttribute(String name) {

    }

    @Override
    public Locale getLocale() {
        return Request.getLocales(request).get(0);
    }

    @Override
    public Enumeration<Locale> getLocales() {
        return Collections.enumeration(Request.getLocales(request));
    }

    @Override
    public boolean isSecure() {
        return HttpScheme.HTTPS.is(request.getHttpURI().getScheme());
    }

    @Override
    public RequestDispatcher getRequestDispatcher(String path) {
        return null;
    }

    @Override
    public int getRemotePort() {
        return Request.getRemotePort(request);
    }

    @Override
    public String getLocalName() {
        return null;
    }

    @Override
    public String getLocalAddr() {
        return Request.getLocalAddr(request);
    }

    @Override
    public int getLocalPort() {
        return Request.getLocalPort(request);
    }

    @Override
    public ServletContext getServletContext() {
        return null;
    }

    @Override
    public AsyncContext startAsync() throws IllegalStateException {
        return null;
    }

    @Override
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse)
            throws IllegalStateException {
        return null;
    }

    @Override
    public boolean isAsyncStarted() {
        return false;
    }

    @Override
    public boolean isAsyncSupported() {
        return false;
    }

    @Override
    public AsyncContext getAsyncContext() {
        return null;
    }

    @Override
    public DispatcherType getDispatcherType() {
        return null;
    }

    @Override
    public String getRequestId() {
        return request.getConnectionMetaData().getId() + "#" + request.getId();
    }


    @Override
    public String getProtocolRequestId() {
       HttpVersion httpVersion = request.getConnectionMetaData().getHttpVersion();
       if(httpVersion == HttpVersion.HTTP_2 || httpVersion == (HttpVersion.HTTP_3)) {
           return request.getId();
       } else {
           return NA;
       }
    }

    @Override
    public ServletConnection getServletConnection() {
        return null;
    }

}
