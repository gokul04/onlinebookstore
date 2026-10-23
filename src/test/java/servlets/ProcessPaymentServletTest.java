package servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.bittercode.model.Book;
import com.bittercode.model.Cart;
import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class ProcessPaymentServletTest {

    private final ProcessPaymentServlet servlet = new ProcessPaymentServlet();

    @Test
    void placesOrderForEachCartItem() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        Book book = new Book("BC1", "Dune", "Herbert", 10.0, 5);
        List<Cart> cartItems = new ArrayList<>();
        cartItems.add(new Cart(book, 2));
        when(session.getAttribute("cartItems")).thenReturn(cartItems);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("Order Placed"));
    }

    @Test
    void asksToLoginWhenNotCustomer() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("Please Login First"));
    }
}
