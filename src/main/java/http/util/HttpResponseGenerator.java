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
        }else if (responseCode.equals("404")) {
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

    public static String generateHeader(String responseCode, String redirectPath, boolean loginSuccess){
        if(responseCode == null || responseCode.isEmpty()){
            throw new RuntimeException();
        }else if (responseCode.equals("302")) {
            String header =  """
                    HTTP/1.1 302 Found\r
                    Location: %s\r
                    Content-Length: 0\r
                    Connection: close\r
                    """.formatted(redirectPath);
            if (loginSuccess) {
                return header + """
                        Set-Cookie: logined=true; Path=/; HttpOnly\\r
                        \r
                        """;
            }else {
                return header + """
                        \r
                        """;
            }
        }else  {
            throw new RuntimeException();
        }
    }
}
