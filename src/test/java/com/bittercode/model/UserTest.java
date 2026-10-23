package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Arrays;

import org.junit.jupiter.api.Test;

class UserTest {

    @Test
    void settersAndGettersRoundTrip() {
        User user = new User();

        user.setEmailId("reader@example.com");
        user.setPassword("s3cr3t-hash");
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setPhone(5551112222L);
        user.setAddress("1 Analytical Engine Way");
        user.setRoles(Arrays.asList(UserRole.CUSTOMER, UserRole.SELLER));

        assertEquals("reader@example.com", user.getEmailId());
        assertEquals("s3cr3t-hash", user.getPassword());
        assertEquals("Ada", user.getFirstName());
        assertEquals("Lovelace", user.getLastName());
        assertEquals(5551112222L, user.getPhone());
        assertEquals("1 Analytical Engine Way", user.getAddress());
        assertTrue(user.getRoles().contains(UserRole.CUSTOMER));
        assertTrue(user.getRoles().contains(UserRole.SELLER));
    }
}
