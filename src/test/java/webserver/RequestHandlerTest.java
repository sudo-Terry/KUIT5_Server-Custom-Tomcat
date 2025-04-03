package webserver;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Files;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

public class RequestHandlerTest {

    @ParameterizedTest
    @ValueSource(strings = {
            "GET / HTTP/1.1\r\nHost: localhost \r\n\r\n",
            "GET /index.html HTTP/1.1\r\nHost: localhost \r\n\r\n",
            "GET /user/list.html HTTP/1.\r\nHost: localhost \r\n\r\n1"
    })
    void testRunningGET(String getRequest) throws IOException {
        try (ServerSocket serverSocket = new ServerSocket(0)) {
            // 사용 가능한 포트 찾기
            int port = serverSocket.getLocalPort();

            try (Socket clientSocket = new Socket("localhost", port);
                 Socket serverSideSocket = serverSocket.accept()) {

                OutputStream clientOutputStream = clientSocket.getOutputStream();
                clientOutputStream.write(getRequest.getBytes());
                clientOutputStream.flush();

                RequestHandler requestHandler = new RequestHandler(serverSideSocket);
                requestHandler.run();

                InputStream clientInputStream = clientSocket.getInputStream();
                String response = new String(clientInputStream.readAllBytes());

                assertTrue(response.contains("HTTP/1.1 200 OK"));
            }
        }
    }

    @Test
    void testRunningPOST() throws IOException {
        String postRequest = """
            POST /user/signup HTTP/1.1\r
            Host: localhost\r
            Content-Type: application/x-www-form-urlencoded\r
            Content-Length: 29\r
            \r
            username=testUser&password=1234&name=greenjoa&email=hello@world
            """;

        try (ServerSocket serverSocket = new ServerSocket(0)) {
            // 사용 가능한 포트 찾기
            int port = serverSocket.getLocalPort();

            try (Socket clientSocket = new Socket("localhost", port);
                 Socket serverSideSocket = serverSocket.accept()) {

                OutputStream clientOutputStream = clientSocket.getOutputStream();
                clientOutputStream.write(postRequest.getBytes());
                clientOutputStream.flush();

                RequestHandler requestHandler = new RequestHandler(serverSideSocket);
                requestHandler.run();

                InputStream clientInputStream = clientSocket.getInputStream();
                String response = new String(clientInputStream.readAllBytes());

                assertTrue(response.contains("HTTP/1.1 302 Found"));
            }
        }
    }
}