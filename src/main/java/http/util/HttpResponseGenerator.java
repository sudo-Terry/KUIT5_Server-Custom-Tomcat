package http.util;

public class HttpResponseGenerator {
    private HttpResponseGenerator(){}

    public static String generateHeader(String responseCode, int bodyLength){
        if(responseCode == null || responseCode.isEmpty()){
            throw new RuntimeException();
        }

        if (responseCode.equals("200")){
            return """
                HTTP/1.1 200 OK\r
                Content-Type: text/html;charset=utf-8\r
                Content-Length: %d\r
                \r
                """.formatted(bodyLength);
        }else if (responseCode.equals("302")) {
            return """
                    HTTP/1.1 302 Found\r
                    Location: %s\r
                    Content-Length: 0\r
                    Connection: close\r
                    \r
                    """.formatted("/index.html");
        } else if (responseCode.equals("404")) {
            return """
                    HTTP/1.1 404 Not Found\r
                    Content-Type: text/html;charset=utf-8\r
                    Content-Length: %d\r
                    \r
                    """.formatted(bodyLength);
        }else  {
            throw new RuntimeException();
        }
    }
}
