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

import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.util.DBUtil;

class CustomerLoginServletTest {

    private final CustomerLoginServlet servlet = new CustomerLoginServlet();

    @Test
    void welcomesUserOnSuccessfulLogin() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("ada@example.com");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("secret");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("firstName")).thenReturn("Ada");
        when(rs.getString("lastName")).thenReturn("Lovelace");
        when(rs.getLong("phone")).thenReturn(123L);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.doPost(req, res);
        }

        assertTrue(sink.toString().contains("Welcome Ada"));
    }

    @Test
    void showsErrorOnFailedLogin() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter(UsersDBConstants.COLUMN_USERNAME)).thenReturn("bad@example.com");
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("wrong");
        StringWriter sink = new StringWriter();
        HttpServletResponse res = ServletTestSupport.mockResponse(sink);

        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            servlet.doPost(req, res);
        }

        assertTrue(sink.toString().contains("Incorrect UserName or PassWord"));
    }
}
