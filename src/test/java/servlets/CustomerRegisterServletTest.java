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

import com.bittercode.constant.db.UsersDBConstants;
import com.bittercode.util.DBUtil;

class CustomerRegisterServletTest {

    private final CustomerRegisterServlet servlet = new CustomerRegisterServlet();

    private static HttpServletRequest requestWithRegistrationParams(HttpSession session) {
        HttpServletRequest req = ServletTestSupport.mockRequest(session);
        when(req.getParameter(UsersDBConstants.COLUMN_PASSWORD)).thenReturn("secret");
        when(req.getParameter(UsersDBConstants.COLUMN_FIRSTNAME)).thenReturn("Ada");
        when(req.getParameter(UsersDBConstants.COLUMN_LASTNAME)).thenReturn("Lovelace");
        when(req.getParameter(UsersDBConstants.COLUMN_ADDRESS)).thenReturn("1 Analytical Engine Way");
        when(req.getParameter(UsersDBConstants.COLUMN_PHONE)).thenReturn("5551112222");
        when(req.getParameter(UsersDBConstants.COLUMN_MAILID)).thenReturn("ada@example.com");
        return req;
    }

    @Test
    void registersNewUserSuccessfully() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = requestWithRegistrationParams(session);
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

        assertTrue(sink.toString().contains("Registered Successfully"));
    }

    @Test
    void showsFailureMessageWhenRegistrationFails() throws Exception {
        HttpSession session = mock(HttpSession.class);
        HttpServletRequest req = requestWithRegistrationParams(session);
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

        assertTrue(sink.toString().contains("Try again"));
    }
}
