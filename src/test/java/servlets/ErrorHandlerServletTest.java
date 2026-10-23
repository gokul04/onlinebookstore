package servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import com.bittercode.model.StoreException;
import com.bittercode.model.UserRole;

class ErrorHandlerServletTest {

    private final ErrorHandlerServlet servlet = new ErrorHandlerServlet();

    @Test
    void showsErrorForLoggedInCustomer() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getAttribute("javax.servlet.error.status_code")).thenReturn(404);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("PAGE_NOT_FOUND"));
    }

    @Test
    void showsErrorForLoggedInSeller() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("INTERNAL_SERVER_ERROR"));
    }

    @Test
    void usesStoreExceptionDetailsWhenPresent() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StoreException storeException = new StoreException("CUSTOM_CODE", "Custom failure");
        when(req.getAttribute("javax.servlet.error.exception")).thenReturn(storeException);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("Custom failure"));
    }

    @Test
    void showsErrorForAnonymousUser() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("INTERNAL_SERVER_ERROR"));
    }
}
