package http.util;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class HttpResponseGeneratorTest {

    @Test
    void testGenerateHeader_200_OK() {
        String expected = """
                HTTP/1.1 200 OK\r
                Content-Type: text/html;charset=utf-8\r
                Content-Length: 100\r
                \r
                """;
        String actual = HttpResponseGenerator.generateHeader("200", 100);
        assertEquals(expected, actual);
    }

    @Test
    void testGenerateHeader_404_NotFound() {
        String expected = """
                HTTP/1.1 404 Not Found\r
                Content-Type: text/html;charset=utf-8\r
                Content-Length: 50\r
                \r
                """;
        String actual = HttpResponseGenerator.generateHeader("404", 50);
        assertEquals(expected, actual);
    }

    @Test
    void testGenerateHeader302_LoginSuccess() {
        String redirectPath = "/home";
        boolean loginSuccess = true;
        String expectedHeader = """
                HTTP/1.1 302 Found\r
                Location: /home\r
                Content-Length: 0\r
                Connection: close\r
                Set-Cookie: logined=true; Path=/; HttpOnly\\r
                \r
                """;

        String actualHeader = HttpResponseGenerator.generateHeader("302", redirectPath, loginSuccess);

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    void testGenerateHeader302_LoginFailure() {
        String redirectPath = "/login";
        boolean loginSuccess = false;
        String expectedHeader = """
                HTTP/1.1 302 Found\r
                Location: /login\r
                Content-Length: 0\r
                Connection: close\r
                \r
                """;

        String actualHeader = HttpResponseGenerator.generateHeader("302", redirectPath, loginSuccess);

        assertEquals(expectedHeader, actualHeader);
    }

    @Test
    void testGenerateCssHeader_200_OK() {
        String expected = """
            HTTP/1.1 200 OK\r
            Content-Type: text/css;charset=utf-8\r
            Content-Length: 150\r
            \r
            """;

        String actual = HttpResponseGenerator.generateCssHeader("200", 150);

        assertEquals(expected, actual);
    }

    @Test
    void testGenerateHeader_NullResponseCode_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> HttpResponseGenerator.generateHeader(null, 100));
    }

    @Test
    void testGenerateHeader_EmptyResponseCode_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> HttpResponseGenerator.generateHeader("", 100));
    }

    @Test
    void testGenerateHeader_InvalidResponseCode_ShouldThrowException() {
        assertThrows(RuntimeException.class, () -> HttpResponseGenerator.generateHeader("500", 100));
    }
}