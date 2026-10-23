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

import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class AddBookServletTest {

    private final AddBookServlet servlet = new AddBookServlet();

    @Test
    void showsAddBookFormWhenNoNameSubmitted() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        servlet.service(req, res);

        assertTrue(sink.toString().contains("Add Book"));
    }

    @Test
    void addsBookWhenFormSubmitted() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Dune");
        when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Herbert");
        when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("20");
        when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("5");
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

        assertTrue(sink.toString().contains("Updated Successfully"));
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
