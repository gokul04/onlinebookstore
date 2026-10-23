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

import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class ReceiptServletTest {

    private final ReceiptServlet servlet = new ReceiptServlet();

    private static ResultSet oneBookResultSet() throws Exception {
        ResultSet rs = mock(ResultSet.class);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn("BC1");
        when(rs.getString(2)).thenReturn("Dune");
        when(rs.getString(3)).thenReturn("Herbert");
        when(rs.getInt(4)).thenReturn(10);
        when(rs.getInt(5)).thenReturn(10);
        return rs;
    }

    @Test
    void paysForCheckedBookWithinAvailableQuantity() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("qty1")).thenReturn("2");
        when(req.getParameter("checked1")).thenReturn("pay");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = oneBookResultSet();
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("Total Paid Amount"));
    }

    @Test
    void stopsWhenRequestedQuantityExceedsAvailable() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("qty1")).thenReturn("99");
        when(req.getParameter("checked1")).thenReturn("pay");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = oneBookResultSet();
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("less than Available"));
    }

    @Test
    void ignoresBookWhenNotCheckedForPayment() throws Exception {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter("qty1")).thenReturn("1");
        when(req.getParameter("checked1")).thenReturn(null);
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = oneBookResultSet();
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.service(req, res);
        }

        assertTrue(sink.toString().contains("Total Paid Amount: 0.0"));
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
