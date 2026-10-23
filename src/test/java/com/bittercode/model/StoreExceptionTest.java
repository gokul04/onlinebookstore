package com.bittercode.model;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.junit.jupiter.api.Test;

import com.bittercode.constant.ResponseCode;

class StoreExceptionTest {

    @Test
    void messageOnlyConstructorDefaultsToBadRequest() {
        StoreException ex = new StoreException("Something went wrong");

        assertEquals("BAD_REQUEST", ex.getErrorCode());
        assertEquals("Something went wrong", ex.getErrorMessage());
        assertEquals(400, ex.getStatusCode());
        assertEquals("Something went wrong", ex.getMessage());
    }

    @Test
    void responseCodeConstructorCopiesCodeAndMessage() {
        StoreException ex = new StoreException(ResponseCode.ACCESS_DENIED);

        assertEquals("ACCESS_DENIED", ex.getErrorCode());
        assertEquals(ResponseCode.ACCESS_DENIED.getMessage(), ex.getErrorMessage());
        assertEquals(ResponseCode.ACCESS_DENIED.getCode(), ex.getStatusCode());
    }

    @Test
    void errorCodeAndMessageConstructorDefaultsTo422() {
        StoreException ex = new StoreException("CUSTOM_CODE", "Custom message");

        assertEquals("CUSTOM_CODE", ex.getErrorCode());
        assertEquals("Custom message", ex.getErrorMessage());
        assertEquals(422, ex.getStatusCode());
    }

    @Test
    void fullConstructorSetsEverythingExplicitly() {
        StoreException ex = new StoreException(503, "SERVICE_UNAVAILABLE", "Try later");

        assertEquals(503, ex.getStatusCode());
        assertEquals("SERVICE_UNAVAILABLE", ex.getErrorCode());
        assertEquals("Try later", ex.getErrorMessage());
    }

    @Test
    void settersOverrideConstructedValues() {
        StoreException ex = new StoreException("Initial message");

        ex.setErrorCode("OVERRIDDEN");
        ex.setErrorMessage("Overridden message");
        ex.setStatusCode(418);

        assertEquals("OVERRIDDEN", ex.getErrorCode());
        assertEquals("Overridden message", ex.getErrorMessage());
        assertEquals(418, ex.getStatusCode());
    }
}
