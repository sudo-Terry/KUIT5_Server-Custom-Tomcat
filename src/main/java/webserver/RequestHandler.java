package webserver;

import db.MemoryUserRepository;
import http.util.HttpRequest;
import http.util.HttpRequestUtils;
import http.util.HttpResponseGenerator;
import model.User;

import java.io.*;
import java.net.Socket;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

public class RequestHandler implements Runnable{
    Socket connection;
    private static final Logger log = Logger.getLogger(RequestHandler.class.getName());
    private static final String WEB_DOC_ROOT = "webapp";

    public RequestHandler(Socket connection) {
        this.connection = connection;
    }

    @Override
    public void run() {
        log.log(Level.INFO, "New Client Connect! Connected IP : " + connection.getInetAddress() + ", Port : " + connection.getPort());
        try (InputStream in = connection.getInputStream(); OutputStream out = connection.getOutputStream()){
            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            DataOutputStream dos = new DataOutputStream(out);

            parseRequest(br, dos);
        } catch (IOException e) {
            log.log(Level.SEVERE,e.getMessage());
        }
    }

    // HTTP 요청에서 경로 추출
    private void parseRequest(BufferedReader br, DataOutputStream dos) throws IOException {
        String requestLine = br.readLine();
        if (requestLine == null || requestLine.isEmpty()) {
            return;
        }
        log.log(Level.INFO, "Request Line: " + requestLine);

        // 요청 헤더 읽기
        Map<String, String> headers = new HashMap<>();
        String line;
        while ((line = br.readLine()) != null && !line.isEmpty()) {
            String[] headerParts = line.split(": ", 2);
            if (headerParts.length == 2) {
                headers.put(headerParts[0], headerParts[1]);
            }
        }

        // 요청 바디 읽기 (필요한 경우)
        StringBuilder body = new StringBuilder();
        if (headers.containsKey("Content-Length")) {
            int contentLength = Integer.parseInt(headers.get("Content-Length"));
            char[] bodyChars = new char[contentLength];
            br.read(bodyChars, 0, contentLength);
            body.append(bodyChars);
        }

        HttpRequest request = new HttpRequest(requestLine, headers, body.toString());
        handleByRequestMethod(request, dos);
        dos.flush();
    }

    // 1: HTTP Method에 따라 처리 로직 분기
    private void handleByRequestMethod(HttpRequest request, DataOutputStream dos) {
        if (request.getHttpMethod().equals("GET")) {
            handleByGETRequestPath(request.getHttpUrl(), dos);
        }else if (request.getHttpMethod().equals("POST")) {
            handleByPOSTRequestPath(request.getHttpUrl(), request.getHttpBody(), dos);
        }
    }

    // 2: URL에 따라 처리 로직 분기 (GET)
    private void handleByGETRequestPath(String requestPath, DataOutputStream dos) {
        if (requestPath == null) {
            //Bad Request
            throw new RuntimeException("Invalid request path");
        } else {
            handleFileReturn(requestPath, dos);
        }
    }

    // 2: URL에 따라 처리 로직 분기 (POST)
    private void handleByPOSTRequestPath(String requestPath, String requestBody, DataOutputStream dos) {
        if (requestPath == null) {
            //Bad Request
            throw new RuntimeException("Invalid request path");
        }else if (requestPath.equals("/user/signup")) {
            handleSignUp(requestBody, dos);
        }else {
            //Bad Request
        }
    }

    // 전달받은 path에 알맞은 파일을 서버에서 찾아 반환
    private void handleFileReturn(String requestPath, DataOutputStream dos) {
        if (requestPath.equals("/")) requestPath = "/index.html";

        File file = new File(WEB_DOC_ROOT + requestPath);
        String header; byte[] body;

        if (!file.exists()) {
            String errorMessage = "<h1>404 Not Found</h1>";
            body = errorMessage.getBytes();
            header = HttpResponseGenerator.generateHeader("404", body.length);
        } else {
            try {body = Files.readAllBytes(file.toPath());} catch (IOException e) {throw new RuntimeException(e);}
            header = HttpResponseGenerator.generateHeader("200", body.length);
        }
        responseHeader(dos, header);
        responseBody(dos, body);
    }

    private void handleSignUp(String requestBody, DataOutputStream dos){
        Map<String, String> params = new HashMap<>();
        String header = "";

        params = HttpRequestUtils.parseQueryParameter(requestBody);
        log.log(Level.INFO, "Sign Up Request: " + requestBody);
        MemoryUserRepository.getInstance().addUser(
                new User(
                        params.get("userId"),
                        params.get("password"),
                        params.get("name"),
                        params.get("email")
                )
        );
        header = HttpResponseGenerator.generateHeader("302", 0);
        responseHeader(dos, header);
        responseBody(dos, new byte[0]);
    }

    private void responseHeader(DataOutputStream dos, String response){
        try {
            dos.writeBytes(response);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

    private void responseBody(DataOutputStream dos, byte[] body) {
        try {
            dos.write(body, 0, body.length);
        } catch (IOException e) {
            log.log(Level.SEVERE, e.getMessage());
        }
    }

}
