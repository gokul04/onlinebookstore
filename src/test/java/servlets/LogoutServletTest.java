package servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import com.bittercode.model.UserRole;

class LogoutServletTest {

    private final LogoutServlet servlet = new LogoutServlet();

    @Test
    void logsOutAndClearsSession() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.doGet(req, res);

        verify(session).invalidate();
        assertTrue(sink.toString().contains("Successfully logged out"));
    }
}
