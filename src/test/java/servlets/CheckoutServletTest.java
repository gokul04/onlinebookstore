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

class CheckoutServletTest {

    private final CheckoutServlet servlet = new CheckoutServlet();

    @Test
    void showsAmountToPayForLoggedInCustomer() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        when(session.getAttribute("amountToPay")).thenReturn(42.0);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.doPost(req, res);

        assertTrue(sink.toString().contains("42.0"));
    }

    @Test
    void asksToLoginWhenNotCustomer() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.doPost(req, res);

        assertTrue(sink.toString().contains("Please Login First"));
    }
}
