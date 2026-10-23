package com.bittercode.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.User;
import com.bittercode.model.UserRole;
import com.bittercode.util.DBUtil;

class UserServiceImplTest {

    private final UserServiceImpl service = new UserServiceImpl();

    private static User sampleUser() {
        User user = new User();
        user.setEmailId("ada@example.com");
        user.setPassword("secret");
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setAddress("1 Analytical Engine Way");
        user.setPhone(5551112222L);
        return user;
    }

    @Test
    void loginReturnsUserWhenCredentialsMatch() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        HttpSession session = mock(HttpSession.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true);
        when(rs.getString("firstName")).thenReturn("Ada");
        when(rs.getString("lastName")).thenReturn("Lovelace");
        when(rs.getLong("phone")).thenReturn(5551112222L);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            User user = service.login(UserRole.CUSTOMER, "ada@example.com", "secret", session);

            assertNotNull(user);
            assertEquals("Ada", user.getFirstName());
            verify(session).setAttribute(UserRole.CUSTOMER.toString(), "ada@example.com");
        }
    }

    @Test
    void loginReturnsNullWhenNoMatch() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        HttpSession session = mock(HttpSession.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertNull(service.login(UserRole.SELLER, "x@example.com", "bad", session));
        }
    }

    @Test
    void loginHandlesSqlException() throws Exception {
        Connection con = mock(Connection.class);
        HttpSession session = mock(HttpSession.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertNull(service.login(UserRole.CUSTOMER, "x@example.com", "bad", session));
        }
    }

    @Test
    void isLoggedInDefaultsToCustomerRoleWhenNull() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("x@example.com");

        assertTrue(service.isLoggedIn(null, session));
    }

    @Test
    void isLoggedInReturnsFalseWhenNoAttribute() {
        HttpSession session = mock(HttpSession.class);

        assertFalse(service.isLoggedIn(UserRole.SELLER, session));
    }

    @Test
    void logoutClearsSessionAndInvalidates() {
        HttpSession session = mock(HttpSession.class);

        assertTrue(service.logout(session));

        verify(session).removeAttribute(UserRole.CUSTOMER.toString());
        verify(session).removeAttribute(UserRole.SELLER.toString());
        verify(session).invalidate();
    }

    @Test
    void registerSucceeds() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals(ResponseCode.SUCCESS.name(), service.register(UserRole.CUSTOMER, sampleUser()));
        }
    }

    @Test
    void registerFailsWhenNoRowInserted() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals(ResponseCode.FAILURE.name(), service.register(UserRole.SELLER, sampleUser()));
        }
    }

    @Test
    void registerReportsDuplicateEmail() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("Duplicate entry 'ada@example.com'"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals("User already registered with this email !!",
                    service.register(UserRole.SELLER, sampleUser()));
        }
    }
}
