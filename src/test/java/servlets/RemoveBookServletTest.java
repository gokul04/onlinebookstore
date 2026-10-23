package servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class RemoveBookServletTest {

    private final RemoveBookServlet servlet = new RemoveBookServlet();

    @Test
    void showsRemoveFormWhenNoBookIdSubmitted() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("Enter BookId to Remove"));
    }

    @Test
    void removesBookSuccessfully() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("bookId")).thenReturn("BC1");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("Removed Successfully"));
    }

    @Test
    void showsNotAvailableWhenBookMissing() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("bookId")).thenReturn("missing");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("Not Available In The Store"));
    }

    @Test
    void asksToLoginWhenNotSeller() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("Please Login First"));
    }
}
