package com.bittercode.util;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.junit.jupiter.api.Test;

import com.bittercode.model.UserRole;

class StoreUtilTest {

    @Test
    void isLoggedInReturnsTrueWhenRoleAttributePresent() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(UserRole.CUSTOMER.toString())).thenReturn("someUser");

        assertTrue(StoreUtil.isLoggedIn(UserRole.CUSTOMER, session));
    }

    @Test
    void isLoggedInReturnsFalseWhenRoleAttributeMissing() {
        HttpSession session = mock(HttpSession.class);
        when(session.getAttribute(anyString())).thenReturn(null);

        assertFalse(StoreUtil.isLoggedIn(UserRole.SELLER, session));
    }

    @Test
    void updateCartItemsAddsNewBookToEmptyCart() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);
        when(req.getParameter("selectedBookId")).thenReturn("book-1");
        when(req.getParameter("addToCart")).thenReturn("true");
        when(session.getAttribute("items")).thenReturn(null);
        when(session.getAttribute("qty_book-1")).thenReturn(null);

        StoreUtil.updateCartItems(req);

        verify(session).setAttribute("items", "book-1");
        verify(session).setAttribute("qty_book-1", 1);
    }

    @Test
    void updateCartItemsRemovesLastUnitFromCart() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);
        when(req.getParameter("selectedBookId")).thenReturn("book-1");
        when(req.getParameter("addToCart")).thenReturn(null);
        when(session.getAttribute("items")).thenReturn("book-1");
        when(session.getAttribute("qty_book-1")).thenReturn(1);

        StoreUtil.updateCartItems(req);

        verify(session).removeAttribute("qty_book-1");
        verify(session).setAttribute("items", "");
    }

    @Test
    void updateCartItemsDoesNothingWhenNoBookSelected() {
        HttpServletRequest req = mock(HttpServletRequest.class);
        HttpSession session = mock(HttpSession.class);
        when(req.getSession()).thenReturn(session);
        when(req.getParameter("selectedBookId")).thenReturn(null);

        StoreUtil.updateCartItems(req);

        verify(session, org.mockito.Mockito.never()).setAttribute(anyString(), org.mockito.ArgumentMatchers.any());
    }
}
