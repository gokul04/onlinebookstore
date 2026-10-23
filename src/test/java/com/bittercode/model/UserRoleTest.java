package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class UserRoleTest {

    @Test
    void hasExactlyTwoRoles() {
        assertArrayEquals(new UserRole[] { UserRole.CUSTOMER, UserRole.SELLER }, UserRole.values());
    }

    @Test
    void valueOfResolvesByName() {
        assertEquals(UserRole.CUSTOMER, UserRole.valueOf("CUSTOMER"));
        assertEquals(UserRole.SELLER, UserRole.valueOf("SELLER"));
    }
}
