package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class BookTest {

    @Test
    void constructorSetsAllFields() {
        Book book = new Book("BC123", "Effective Java", "Joshua Bloch", 45.99, 10);

        assertEquals("BC123", book.getBarcode());
        assertEquals("Effective Java", book.getName());
        assertEquals("Joshua Bloch", book.getAuthor());
        assertEquals(45.99, book.getPrice());
        assertEquals(10, book.getQuantity());
    }

    @Test
    void noArgConstructorStartsEmpty() {
        Book book = new Book();

        assertEquals(null, book.getBarcode());
        assertEquals(0, book.getQuantity());
        assertEquals(0.0, book.getPrice());
    }

    @Test
    void settersUpdateFields() {
        Book book = new Book();

        book.setBarcode("BC999");
        book.setName("Clean Code");
        book.setAuthor("Robert C. Martin");
        book.setPrice(29.5);
        book.setQuantity(3);

        assertEquals("BC999", book.getBarcode());
        assertEquals("Clean Code", book.getName());
        assertEquals("Robert C. Martin", book.getAuthor());
        assertEquals(29.5, book.getPrice());
        assertEquals(3, book.getQuantity());
    }
}
