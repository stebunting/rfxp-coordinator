package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.Locale;

@DisplayName("Conflict Class...")
class ConflictTests {

    final Equipment equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 50, Equipment.FrontEndType.TRACKING, 100000);

    @DisplayName("constructs channel spacing conflicts that...")
    @Nested
    class ChannelSpacingConflictTests {

        Channel channel1;
        Channel channel2;
        Conflict conflict;

        @BeforeEach
        void setUp() throws InvalidFrequencyException {
            Locale.setDefault(new Locale("en", "GB"));

            channel1 = new Channel(0, 560.500, equipment);
            channel2 = new Channel(0, 560.700, equipment);
            conflict = new Conflict(channel1, channel2);
        }

        @DisplayName("get affected channel")
        @Test
        final void testGetAffectedChannel() {
            assertSame(conflict.getChannel(), channel1);
        }

        @DisplayName("get conflict channel")
        @Test
        final void testGetConflictChannel() {
            assertSame(conflict.getConflictChannel(), channel2);
        }

        @DisplayName("get null conflict intermodulation channel")
        @Test
        final void testGetIntermodChannel() {
            assertNull(conflict.getConflictIntermod());
        }

        @DisplayName("get conflict type")
        @Test
        final void testGetConflictType() {
            assertEquals(Conflict.Type.CHANNEL_SPACING, conflict.getType());
        }

        @DisplayName("get human readable description")
        @Test
        final void testToString() {
            assertEquals("Channel spacing violation (560.700 MHz)", conflict.toString());
        }

        @DisplayName("throw error on null channel construction")
        @Test
        final void testNullChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Conflict(null, channel2));
        }

        @DisplayName("throw error on null conflict channel argument")
        @Test
        final void testNullConflictChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Conflict(channel1, (Channel) null));
        }
    }

    @DisplayName("constructs intermodulation spacing conflicts that...")
    @Nested
    class IntermodulationSpacingConflictTests {

        Channel channel1;
        Channel channel2;
        Intermod intermod;
        Conflict conflict;

        @BeforeEach
        void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(0, 560.500, equipment);
            channel2 = new Channel(0, 560.700, equipment);
            intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            conflict = new Conflict(channel1, intermod);
        }

        @DisplayName("gets affected channel")
        @Test
        final void testGetAffectedChannel() {
            assertSame(conflict.getChannel(), channel1);
        }

        @DisplayName("gets conflict intermod")
        @Test
        final void testGetIntermod() {
            assertSame(conflict.getConflictIntermod(), intermod);
        }

        @DisplayName("gets null conflict channel")
        @Test
        final void testGetNullConflictChannel() {
            assertNull(conflict.getConflictChannel());
        }

        @DisplayName("gets conflict type")
        @Test
        final void testGetConflictType() {
            assertEquals(Conflict.Type.INTERMOD_SPACING, conflict.getType());
        }

        @DisplayName("gets human readable description")
        @Test
        final void testToString() {
            assertEquals("2T3O violation (560.500 MHz & 560.700 MHz)", conflict.toString());
        }

        @DisplayName("throws error on null channel argument")
        @Test
        final void testNullChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Conflict(null, intermod));
        }

        @DisplayName("throws error on null intermodulation argument")
        @Test
        final void testNullIntermodChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Conflict(channel1, (Intermod) null));
        }
    }

    @DisplayName("constructs whitespace conflicts that...")
    @Nested
    class WhiteSpaceConflictTests {

        Channel channel1;
        Conflict conflict;

        @BeforeEach
        void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(0, 560.500, equipment);
            conflict = new Conflict(channel1);
        }

        @DisplayName("gets affected channel")
        @Test
        final void testGetAffectedChannel() {
            assertSame(conflict.getChannel(), channel1);
        }

        @DisplayName("gets null conflict channel")
        @Test
        final void testGetNullConflictChannel() {
            assertNull(conflict.getConflictChannel());
        }

        @DisplayName("gets null conflict intermod")
        @Test
        final void testGetIntermod() {
            assertNull(conflict.getConflictIntermod());
        }

        @DisplayName("gets conflict type")
        @Test
        final void testGetConflictType() {
            assertEquals(Conflict.Type.WHITE_SPACE, conflict.getType());
        }

        @DisplayName("gets human readable description")
        @Test
        final void testToString() {
            assertEquals("Possible white space violation", conflict.toString());
        }

        @DisplayName("throw error on null channel construction")
        @Test
        final void testNullChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Conflict(null));
        }
    }
}
