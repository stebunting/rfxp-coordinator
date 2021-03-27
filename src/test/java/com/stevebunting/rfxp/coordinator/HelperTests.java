package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import java.util.Arrays;
import java.util.List;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Helper Function...")
class HelperTests {

    @DisplayName("isSorted()...")
    @Nested
    class IsSortedTests {

        @DisplayName("returns true if array is sorted")
        @Test
        final void testIsSorted() {
            List<Integer> sortedList = Arrays.asList(1, 2, 3, 4, 5);
            assertTrue(Helpers.isSorted(sortedList));
        }

        @DisplayName("returns true if array is equal")
        @Test
        final void testIsEqual() {
            List<Integer> sortedList = Arrays.asList(1, 1, 1, 1, 1);
            assertTrue(Helpers.isSorted(sortedList));
        }

        @DisplayName("returns false if array is not sorted")
        @Test
        final void testIsNotSorted() {
            List<Integer> unsortedList = Arrays.asList(1, 3, 2, 4, 5);
            assertFalse(Helpers.isSorted(unsortedList));
        }
    }
}
