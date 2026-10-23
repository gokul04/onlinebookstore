package com.bittercode.util;

import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;

import com.bittercode.model.StoreException;

class DBUtilTest {

    @Test
    void getConnectionThrowsWhenNoDatabaseIsReachable() {
        // No real Postgres is listening on the test port, so the static initializer's
        // connection attempt fails and DBUtil.getConnection() should surface that as
        // a StoreException rather than returning a usable connection.
        assertThrows(StoreException.class, DBUtil::getConnection);
    }
}
