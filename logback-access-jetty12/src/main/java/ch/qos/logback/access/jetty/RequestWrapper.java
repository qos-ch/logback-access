package ch.qos.logback.access.jetty;

import ch.qos.logback.access.common.spi.WrappedHttpRequest;
import jakarta.servlet.*;
import jakarta.servlet.http.*;
import org.eclipse.jetty.http.HttpHeader;
import org.eclipse.jetty.http.HttpScheme;
import org.eclipse.jetty.http.HttpVersion;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Session;
import org.eclipse.jetty.util.Fields;

import java.io.BufferedReader;
import java.security.Principal;
import java.time.DateTimeException;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

import static ch.qos.logback.access.common.spi.IAccessEvent.NA;
import static ch.qos.logback.access.jetty.HeaderUtil.buildHeaderMap;
import static java.nio.charset.StandardCharsets.UTF_8;

public class RequestWrapper implements HttpServletRequest, WrappedHttpRequest {
    private static final Cookie[] EMPTY_COOKIE_ARRAY = new Cookie[0];
    private static final String[] EMPTY_STRING_ARRAY = new String[0];

    private final Request request;
    private StringBuffer requestURL;

    public RequestWrapper(Request request) {
        this.request = request;
    }

    @Override
    public String getAuthType() {
        return Optional.ofNullable(request.getHeaders().get(HttpHeader.AUTHORIZATION))
                .map(s -> s.split(" ")[0])
                .orElse(null);
    }

    @Override
    public Cookie[] getCookies() {
        return Request.getCookies(request)
                .stream()
                .map(httpCookie -> new Cookie(httpCookie.getName(), httpCookie.getValue()))
                .collect(Collectors.toList())
                .toArray(EMPTY_COOKIE_ARRAY);
    }

    private Optional<String> getOptionalHeader(String name) {
        return Optional.ofNullable(request.getHeaders().get(name));
    }

    @Override
    public long getDateHeader(String name) {
        return getOptionalHeader(name).map(header -> {
                    try {
                        var headerLong = Long.parseLong(header);
                        Instant.ofEpochSecond(headerLong);

                        return headerLong;
                    } catch (NumberFormatException | DateTimeException e) {
                        throw new IllegalArgumentException(e);
                    }
                })
                .orElse(-1L);
    }

    @Override
    public String getHeader(String name) {
        return request.getHeaders().get(name);
    }

    @Override
    public Enumeration<String> getHeaders(String name) {
        return request.getHeaders().getValues(name);
    }

    @Override
    public Enumeration<String> getHeaderNames() {
        return Collections.enumeration(request.getHeaders().getFieldNamesCollection());
    }

    @Override
    public Map<String, String> buildRequestHeaderMap() {
        return buildHeaderMap(request.getHeaders());
    }

    @Override
    public int getIntHeader(String name) {
        return getOptionalHeader(name).map(Integer::parseInt)
                .orElse(-1);
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
        return getPrincipal().map(Principal::getName)
                .orElse(null);
    }

    @Override
    public boolean isUserInRole(String role) {
        return false;
    }

    private Optional<Principal> getPrincipal() {
        return Optional.ofNullable(Request.getAuthenticationState(request))
                .map(Request.AuthenticationState::getUserPrincipal);
    }

    @Override
    public Principal getUserPrincipal() {
        return getPrincipal().orElse(null);
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
        return Optional.ofNullable(request.getSession(false))
                .map(Session::getId)
                .orElse(NA);
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
    public boolean authenticate(HttpServletResponse response) {
        return false;
    }

    @Override
    public void login(String username, String password) {
    }

    @Override
    public void logout() {
    }

    @Override
    public Collection<Part> getParts() {
        return null;
    }

    @Override
    public Part getPart(String name) {
        return null;
    }

    @Override
    public <T extends HttpUpgradeHandler> T upgrade(Class<T> handlerClass) {
        return null;
    }

    @Override
    public Object getAttribute(String name) {
        return request.getAttribute(name);
    }

    @Override
    public Enumeration<String> getAttributeNames() {
        return Collections.enumeration(request.getAttributeNameSet());
    }

    @Override
    public String getCharacterEncoding() {
        return null;
    }

    @Override
    public void setCharacterEncoding(String env) {
    }

    @Override
    public int getContentLength() {
        return Math.toIntExact(request.getLength());
    }

    @Override
    public long getContentLengthLong() {
        return request.getLength();
    }

    @Override
    public String getContentType() {
        return getHeader("Content-Type");
    }

    @Override
    public ServletInputStream getInputStream() {
        return null;
    }

    @Override
    public Map<String, String[]> buildRequestParameterMap() {
        return Request.extractQueryParameters(request, UTF_8)
                .stream()
                .collect(Collectors.toUnmodifiableMap(Fields.Field::getName, field -> field.getValues().toArray(EMPTY_STRING_ARRAY)));
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
    public BufferedReader getReader() {
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
    public AsyncContext startAsync(ServletRequest servletRequest, ServletResponse servletResponse) throws IllegalStateException {
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
        var httpVersion = request.getConnectionMetaData().getHttpVersion();
        if (httpVersion == HttpVersion.HTTP_2 || httpVersion == (HttpVersion.HTTP_3)) {
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
