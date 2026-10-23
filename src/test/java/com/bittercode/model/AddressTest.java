package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

class AddressTest {

    @Test
    void settersAndGettersRoundTrip() {
        Address address = new Address();

        address.setAddressLine1("221B Baker Street");
        address.setAddressLine2("Flat 2");
        address.setCity("London");
        address.setState("Greater London");
        address.setCountry("UK");
        address.setPinCode(123456L);
        address.setPhone("5551234");

        assertEquals("221B Baker Street", address.getAddressLine1());
        assertEquals("Flat 2", address.getAddressLine2());
        assertEquals("London", address.getCity());
        assertEquals("Greater London", address.getState());
        assertEquals("UK", address.getCountry());
        assertEquals(123456L, address.getPinCode());
        assertEquals("5551234", address.getPhone());
    }
}
