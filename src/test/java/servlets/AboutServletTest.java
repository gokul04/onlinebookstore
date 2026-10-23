package servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import com.bittercode.model.UserRole;

class AboutServletTest {

    private final AboutServlet servlet = new AboutServlet();

    @Test
    void showsAboutInfoForLoggedInCustomer() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("flowcv.me"));
    }

    @Test
    void showsAboutInfoForLoggedInSeller() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("flowcv.me"));
    }

    @Test
    void asksToLoginWhenNotAuthenticated() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("Please Login First"));
    }
}
