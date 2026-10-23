package com.bittercode.constant;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import org.junit.jupiter.api.Test;

class ResponseCodeTest {

    @Test
    void successHasHttp200() {
        assertEquals(200, ResponseCode.SUCCESS.getCode());
        assertEquals("OK", ResponseCode.SUCCESS.getMessage());
    }

    @Test
    void getMessageByStatusCodeFindsExistingCode() {
        Optional<ResponseCode> found = ResponseCode.getMessageByStatusCode(404);

        assertTrue(found.isPresent());
        assertEquals(ResponseCode.PAGE_NOT_FOUND, found.get());
    }

    @Test
    void getMessageByStatusCodeReturnsEmptyForUnknownCode() {
        Optional<ResponseCode> found = ResponseCode.getMessageByStatusCode(999);

        assertFalse(found.isPresent());
    }
}
