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
    void testGenerateHeader_302_Found() {
        String expected = """
                HTTP/1.1 302 Found\r
                Location: /index.html\r
                Content-Length: 0\r
                Connection: close\r
                \r
                """;
        String actual = HttpResponseGenerator.generateHeader("302", 0);
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