package servlets;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.bittercode.constant.db.BooksDBConstants;
import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class UpdateBookServletTest {

    private final UpdateBookServlet servlet = new UpdateBookServlet();

    @Test
    void showsUpdateFormWhenBookIdRequested() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("bookId")).thenReturn("BC1");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn("BC1");
        when(rs.getString(2)).thenReturn("Dune");
        when(rs.getString(3)).thenReturn("Herbert");
        when(rs.getInt(4)).thenReturn(10);
        when(rs.getInt(5)).thenReturn(5);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("bookName"));
    }

    @Test
    void updatesBookWhenFormSubmitted() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.SELLER.toString())).thenReturn("seller@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("updateFormSubmitted")).thenReturn("true");
        when(req.getParameter(BooksDBConstants.COLUMN_NAME)).thenReturn("Dune");
        when(req.getParameter(BooksDBConstants.COLUMN_BARCODE)).thenReturn("BC1");
        when(req.getParameter(BooksDBConstants.COLUMN_AUTHOR)).thenReturn("Herbert");
        when(req.getParameter(BooksDBConstants.COLUMN_PRICE)).thenReturn("25.0");
        when(req.getParameter(BooksDBConstants.COLUMN_QUANTITY)).thenReturn("7");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);

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
