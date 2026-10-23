package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class CartTest {

    @Test
    void constructorSetsBookAndQuantity() {
        Book book = new Book("BC1", "Dune", "Frank Herbert", 19.99, 5);
        Cart cart = new Cart(book, 2);

        assertSame(book, cart.getBook());
        assertEquals(2, cart.getQuantity());
    }

    @Test
    void settersUpdateFields() {
        Cart cart = new Cart(new Book(), 1);
        Book replacement = new Book("BC2", "Foundation", "Isaac Asimov", 15.0, 4);

        cart.setBook(replacement);
        cart.setQuantity(7);

        assertSame(replacement, cart.getBook());
        assertEquals(7, cart.getQuantity());
    }
}
