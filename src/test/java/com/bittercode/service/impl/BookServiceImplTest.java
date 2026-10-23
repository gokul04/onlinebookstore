package com.bittercode.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;

import com.bittercode.constant.ResponseCode;
import com.bittercode.model.Book;
import com.bittercode.util.DBUtil;

class BookServiceImplTest {

    private final BookServiceImpl service = new BookServiceImpl();

    @Test
    void getBookByIdReturnsBookWhenFound() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, false);
        when(rs.getString(1)).thenReturn("BC1");
        when(rs.getString(2)).thenReturn("Dune");
        when(rs.getString(3)).thenReturn("Frank Herbert");
        when(rs.getInt(4)).thenReturn(20);
        when(rs.getInt(5)).thenReturn(5);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            Book book = service.getBookById("BC1");

            assertNotNull(book);
            assertEquals("BC1", book.getBarcode());
            assertEquals("Dune", book.getName());
            assertEquals(5, book.getQuantity());
        }
    }

    @Test
    void getBookByIdReturnsNullWhenNotFound() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(false);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertNull(service.getBookById("missing"));
        }
    }

    @Test
    void getBookByIdReturnsNullOnSqlException() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertNull(service.getBookById("BC1"));
        }
    }

    @Test
    void getAllBooksReturnsAllRows() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        ResultSet rs = mock(ResultSet.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeQuery()).thenReturn(rs);
        when(rs.next()).thenReturn(true, true, false);
        when(rs.getString(1)).thenReturn("BC1", "BC2");
        when(rs.getString(2)).thenReturn("Dune", "Foundation");
        when(rs.getString(3)).thenReturn("Herbert", "Asimov");
        when(rs.getInt(4)).thenReturn(10, 20);
        when(rs.getInt(5)).thenReturn(1, 2);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            List<Book> books = service.getAllBooks();

            assertEquals(2, books.size());
        }
    }

    @Test
    void getAllBooksReturnsEmptyListOnSqlException() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals(0, service.getAllBooks().size());
        }
    }

    @Test
    void deleteBookByIdSucceedsWhenRowRemoved() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals(ResponseCode.SUCCESS.name(), service.deleteBookById("BC1"));
        }
    }

    @Test
    void deleteBookByIdFailsWhenNoRowRemoved() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(0);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals(ResponseCode.FAILURE.name(), service.deleteBookById("missing"));
        }
    }

    @Test
    void deleteBookByIdHandlesException() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertTrue(service.deleteBookById("BC1").startsWith(ResponseCode.FAILURE.name()));
        }
    }

    @Test
    void addBookSucceeds() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);
        when(ps.executeUpdate()).thenReturn(1);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            Book book = new Book("BC1", "Dune", "Herbert", 10.0, 5);
            assertEquals(ResponseCode.SUCCESS.name(), service.addBook(book));
        }
    }

    @Test
    void addBookHandlesException() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("dup"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            Book book = new Book("BC1", "Dune", "Herbert", 10.0, 5);
            assertTrue(service.addBook(book).startsWith(ResponseCode.FAILURE.name()));
        }
    }

    @Test
    void updateBookQtyByIdSucceeds() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertEquals(ResponseCode.SUCCESS.name(), service.updateBookQtyById("BC1", 3));
        }
    }

    @Test
    void updateBookQtyByIdHandlesException() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            assertTrue(service.updateBookQtyById("BC1", 3).startsWith(ResponseCode.FAILURE.name()));
        }
    }

    @Test
    void getBooksByCommaSeperatedBookIdsReturnsMatches() throws Exception {
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

            List<Book> books = service.getBooksByCommaSeperatedBookIds("'BC1'");

            assertEquals(1, books.size());
        }
    }

    @Test
    void updateBookSucceeds() throws Exception {
        Connection con = mock(Connection.class);
        PreparedStatement ps = mock(PreparedStatement.class);
        when(con.prepareStatement(anyString())).thenReturn(ps);

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            Book book = new Book("BC1", "Dune", "Herbert", 10.0, 5);
            assertEquals(ResponseCode.SUCCESS.name(), service.updateBook(book));
        }
    }

    @Test
    void updateBookHandlesException() throws Exception {
        Connection con = mock(Connection.class);
        when(con.prepareStatement(anyString())).thenThrow(new SQLException("boom"));

        try (MockedStatic<DBUtil> dbUtil = mockStatic(DBUtil.class)) {
            dbUtil.when(DBUtil::getConnection).thenReturn(con);

            Book book = new Book("BC1", "Dune", "Herbert", 10.0, 5);
            assertTrue(service.updateBook(book).startsWith(ResponseCode.FAILURE.name()));
        }
    }
}
