package http.util;

import java.util.HashMap;
import java.util.Map;

public class HttpRequest {
    private String httpMethod;
    private String httpUrl;
    private String httpVersion;
    private Map<String, String> httpHeaders = new HashMap<>();
    private String httpBody;

    public HttpRequest(String requestLine, Map<String, String> headers, String body) {
        parseRequest(requestLine, headers, body);
    }

    private void parseRequest(String requestLine, Map<String, String> headers, String body) {
        String[] requestParts = requestLine.split(" ");
        if (requestParts.length < 3) {
            throw new IllegalArgumentException("Invalid HTTP request line: " + requestLine);
        }

        this.httpMethod = requestParts[0];
        this.httpUrl = requestParts[1];
        this.httpVersion = requestParts[2];

        this.httpHeaders = headers;
        this.httpBody = body;
    }

    public String getHttpMethod() {
        return httpMethod;
    }

    public String getHttpUrl() {
        return httpUrl;
    }

    public String getHttpVersion() {
        return httpVersion;
    }

    public Map<String, String> getHttpHeader() {
        return httpHeaders;
    }

    public String getHttpBody() {
        return httpBody;
    }
}
