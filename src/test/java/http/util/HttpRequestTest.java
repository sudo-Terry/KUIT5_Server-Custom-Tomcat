package http.util;

import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Test;

class HttpRequestTest {
    @Test
    void testHttpRequestParsing() {
        String requestLine = "GET /index.html HTTP/1.1";
        Map<String, String> headers = new HashMap<>();
        headers.put("Host", "localhost");
        headers.put("User-Agent", "JUnitTest");
        String body = "";

        HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

        assertEquals("GET", httpRequest.getHttpMethod());
        assertEquals("/index.html", httpRequest.getHttpUrl());
        assertEquals("HTTP/1.1", httpRequest.getHttpVersion());
        assertEquals("localhost", httpRequest.getHttpHeader().get("Host"));
        assertEquals("JUnitTest", httpRequest.getHttpHeader().get("User-Agent"));
        assertEquals("", httpRequest.getHttpBody());
    }

    @Test
    void testHttpRequestWithBody() {
        String requestLine = "POST /submit HTTP/1.1";
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json");
        headers.put("Content-Length", "18");
        String body = "{\"name\":\"John\"}";

        HttpRequest httpRequest = new HttpRequest(requestLine, headers, body);

        assertEquals("POST", httpRequest.getHttpMethod());
        assertEquals("/submit", httpRequest.getHttpUrl());
        assertEquals("HTTP/1.1", httpRequest.getHttpVersion());
        assertEquals("application/json", httpRequest.getHttpHeader().get("Content-Type"));
        assertEquals("18", httpRequest.getHttpHeader().get("Content-Length"));
        assertEquals("{\"name\":\"John\"}", httpRequest.getHttpBody());
    }

    @Test
    void testInvalidRequestLine() {
        String invalidRequestLine = "INVALID_REQUEST";
        Map<String, String> headers = new HashMap<>();
        String body = "";

        assertThrows(IllegalArgumentException.class, () -> new HttpRequest(invalidRequestLine, headers, body));
    }
}