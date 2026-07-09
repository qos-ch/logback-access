package ch.qos.logback.access.common.servlet;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.io.IOException;
import java.lang.reflect.Proxy;
import java.nio.ByteBuffer;
import org.junit.jupiter.api.Test;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;

public class TeeServletInputStreamTest {

    @Test
    public void testReadsInputStreamFullyAndProvidesInputBuffer() throws IOException {
        // arrange
        byte[] bytes = "GET / HTTP/1.1".getBytes();
        HttpServletRequest request = mockServletRequest(bytes);

        // act
        try (TeeServletInputStream is = new TeeServletInputStream(request)) {
            // assert
            assertArrayEquals(bytes, is.getInputBuffer());
        }
    }

    @Test
    public void testProvidesReadableInputStream() throws IOException {
        // arrange
        byte[] bytes = "GET / HTTP/1.1".getBytes();
        HttpServletRequest request = mockServletRequest(bytes);

        // act
        try (TeeServletInputStream is = new TeeServletInputStream(request)) {
            // assert
            for (byte nextByte : bytes) {
                assertEquals(nextByte, is.read());
            }
            assertEquals(-1, is.read());
            assertEquals(-1, is.read());
            assertEquals(-1, is.read());
        }
    }

    @Test
    public void testPropagatesIOExceptionOnRead() throws IOException {
        // arrange
        byte[] bytes = "GET / ...".getBytes();
        HttpServletRequest request = mockServletRequest(bytes, "Read timed out");

        // act
        try (TeeServletInputStream is = new TeeServletInputStream(request)) {
            // assert
            IOException e = assertThrows(IOException.class, () -> { while(is.read() != -1); });
            assertEquals("Read timed out", e.getMessage());
            assertArrayEquals(new byte[0], is.getInputBuffer());
        }
    }

    private static HttpServletRequest mockServletRequest(byte[] bytes) {
        return mockServletRequest(bytes, null);
    }

    private static HttpServletRequest mockServletRequest(byte[] bytes, String ioError) {
        ServletInputStream inputStream = mockServletInputStream(bytes, ioError);

        ClassLoader classLoader = TeeServletInputStreamTest.class.getClassLoader();
        Class<?>[] interfaces = new Class<?>[] {HttpServletRequest.class};

        Object servletRequest = Proxy.newProxyInstance(classLoader, interfaces, (object, method, arg) -> {
            switch (method.getName()) {
                case "getInputStream": return inputStream;
                default: throw new UnsupportedOperationException();
            }
        });

        return (HttpServletRequest) servletRequest;
    }

    private static ServletInputStream mockServletInputStream(byte[] bytes, String ioError) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);

        return new ServletInputStream() {
            @Override
            public int read() throws IOException {
                if (buffer.hasRemaining()) {
                    return buffer.get();
                } else if (ioError == null) {
                    return -1;
                }
                throw new IOException(ioError);
            }

            @Override
            public void setReadListener(ReadListener readListener) {
                throw new UnsupportedOperationException();
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public boolean isFinished() {
                return buffer.hasRemaining();
            }
        };
    }

}
