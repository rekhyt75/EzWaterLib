package org.rekhyt.ezwaterlib;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

import org.junit.jupiter.api.Test;

/**
 * Smoke test for the example App.
 */
class AppTest {

    @Test
    void mainRunsWithoutThrowing() {
        assertDoesNotThrow(() -> App.main(new String[0]));
    }
}
