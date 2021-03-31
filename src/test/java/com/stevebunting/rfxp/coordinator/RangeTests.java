package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;

@DisplayName("Range Class...")
class RangeTests {

    @DisplayName("contains methods that...")
    @Nested
    class EquipmentMethodTests {

        // Range object under test
        private Range range;

        @BeforeEach
        void setUp() {
            range = new Range(606000, 666000, "K4E");
        }

        @DisplayName("check if frequency is in range")
        @Test
        final void testFrequencyInRange() {
            assertTrue(range.isValidFrequency(606000));
            assertTrue(range.isValidFrequency(666000));
            assertTrue(range.isValidFrequency(634756));
            assertFalse(range.isValidFrequency(605999));
            assertFalse(range.isValidFrequency(666001));
            assertFalse(range.isValidFrequency(56));
        }

        @DisplayName("sort ranges")
        @Test
        final void testSortRanges() {
            List<Range> ranges = new ArrayList<>();
            Range a = new Range(500, 560, "a");
            Range b = new Range(500, 580, "b");
            Range c = new Range(350, 900, "c");
            Range d = new Range(800, 890, "d");
            Range e = new Range(800, 890, "e");
            ranges.add(a);
            ranges.add(b);
            ranges.add(c);
            ranges.add(e);
            ranges.add(d);

            ranges.sort(null);
            assertEquals(c, ranges.get(0));
            assertEquals(a, ranges.get(1));
            assertEquals(b, ranges.get(2));
            assertEquals(d, ranges.get(3));
            assertEquals(e, ranges.get(4));
        }

        @DisplayName("get low frequency")
        @Test
        final void testGetLo() {
            assertEquals(606000, range.getLo());
        }

        @DisplayName("get high frequency")
        @Test
        final void testGetHi() {
            assertEquals(666000, range.getHi());
        }

        @DisplayName("get range name")
        @Test
        final void testGetRange() {
            assertEquals("K4E", range.getName());
        }

        @DisplayName("print as readable string")
        @Test
        final void testToString() {
            assertEquals("K4E (606.000 MHz - 666.000 MHz)", range.toString());
        }
    }
}
