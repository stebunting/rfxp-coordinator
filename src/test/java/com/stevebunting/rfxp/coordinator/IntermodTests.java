package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.Locale;

@DisplayName("Intermod Class...")
class IntermodTests {

    Channel channel1;
    Channel channel2;
    Channel channel3;
    final Equipment equipment = new Equipment("Sennheiser", "SR2050", 25, 300, 100, 90, 0, 0, 50, Equipment.FrontEndType.TRACKING, 100000);

    @BeforeEach
    void setUp() throws Exception {
        Locale.setDefault(new Locale("en", "GB"));

        channel1 = new Channel(0, 606.000, equipment);
        channel2 = new Channel(1, 606.300, equipment);
        channel3 = new Channel(2, 606.700, equipment);
    }

    @DisplayName("constructor....")
    @Nested
    class IntermodConstructorTests {

        @DisplayName("creates and calculates new 2T3O Intermodulation")
        @Test
        final void testNew2T3OIntermod() {
            Intermod intermod1 = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            assertEquals(605700, intermod1.getFreq());
            assertEquals(Intermod.Type.IM_2T3O, intermod1.getType());
            assertEquals(channel1, intermod1.getF1());
            assertEquals(channel2, intermod1.getF2());
            assertNull(intermod1.getF3());

            Intermod intermod2 = new Intermod(Intermod.Type.IM_2T3O, channel2, channel1, null);
            assertEquals(606600, intermod2.getFreq());
        }

        @DisplayName("creates and calculates new 2T5O Intermodulation")
        @Test
        final void testNew2T5OIntermod() {
            Intermod intermod1 = new Intermod(Intermod.Type.IM_2T5O, channel1, channel2, null);
            assertEquals(605400, intermod1.getFreq());
            assertEquals(Intermod.Type.IM_2T5O, intermod1.getType());

            Intermod intermod2 = new Intermod(Intermod.Type.IM_2T5O, channel2, channel1, null);
            assertEquals(606900, intermod2.getFreq());
            assertEquals(channel2, intermod2.getF1());
            assertEquals(channel1, intermod2.getF2());
            assertNull(intermod2.getF3());
        }

        @DisplayName("creates and calculates new 2T7O Intermodulation")
        @Test
        final void testNew2T7OIntermod() {
            Intermod intermod1 = new Intermod(Intermod.Type.IM_2T7O, channel1, channel2, null);
            assertEquals(605100, intermod1.getFreq());
            assertEquals(Intermod.Type.IM_2T7O, intermod1.getType());

            Intermod intermod2 = new Intermod(Intermod.Type.IM_2T7O, channel2, channel1, null);
            assertEquals(607200, intermod2.getFreq());
        }

        @DisplayName("creates and calculates new 2T9O Intermodulation")
        @Test
        final void testNew2T9OIntermod() {
            Intermod intermod1 = new Intermod(Intermod.Type.IM_2T9O, channel1, channel2, null);
            assertEquals(604800, intermod1.getFreq());
            assertEquals(Intermod.Type.IM_2T9O, intermod1.getType());

            Intermod intermod2 = new Intermod(Intermod.Type.IM_2T9O, channel2, channel1, null);
            assertEquals(607500, intermod2.getFreq());
        }

        @DisplayName("creates and calculates 2nd Order Intermodulation with f3 argument supplied")
        @Test
        final void testNew2ndOrderIntermodWithF3() {
            Intermod intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, channel3);
            assertEquals(Intermod.Type.IM_2T3O, intermod.getType());
            assertNull(intermod.getF3());
        }

        @DisplayName("creates and calculates new 3T3O Intermodulation")
        @Test
        final void testNew3T3OIntermod() {
            Intermod intermod1 = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3);
            assertEquals(605600, intermod1.getFreq());
            assertEquals(Intermod.Type.IM_3T3O, intermod1.getType());

            Intermod intermod2 = new Intermod(Intermod.Type.IM_3T3O, channel2, channel3, channel1);
            assertEquals(607000, intermod2.getFreq());

            Intermod intermod3 = new Intermod(Intermod.Type.IM_3T3O, channel3, channel1, channel2);
            assertEquals(606400, intermod3.getFreq());
        }

        @DisplayName("throws error when type set to null")
        @Test
        final void testNullType() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Intermod(null, channel1, channel2, channel3));
        }

        @DisplayName("throws error when channel 1 set to null")
        @Test
        final void testNullF1Type() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Intermod(Intermod.Type.IM_2T3O, null, channel2, channel3));
        }

        @DisplayName("throws error when channel 2 set to null")
        @Test
        final void testNullF2Type() {
            assertThrows(IllegalArgumentException.class, () ->
                    new Intermod(Intermod.Type.IM_2T3O, channel1, null, channel3));
        }
    }

    @DisplayName("contains methods that...")
    @Nested
    class IntermodMethodTests {

        @DisplayName("generate pretty printed intermodulation types")
        @Test
        final void testPrettyEnums() {
            assertEquals("2T3O", Intermod.Type.IM_2T3O.pretty());
            assertEquals("2T5O", Intermod.Type.IM_2T5O.pretty());
            assertEquals("2T7O", Intermod.Type.IM_2T7O.pretty());
            assertEquals("2T9O", Intermod.Type.IM_2T9O.pretty());
            assertEquals("3T3O", Intermod.Type.IM_3T3O.pretty());
        }

        @DisplayName("check for basic equality")
        @Test
        final void testBasicEquality() {
            Intermod intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            assertEquals(intermod, intermod);
            assertNotEquals(intermod, null);
        }

        @DisplayName("compare intermodulations")
        @Test
        final void testCompareToFrequency() {
            Intermod intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            Intermod intermodLo = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            Intermod intermodHi = new Intermod(Intermod.Type.IM_2T3O, channel2, channel1, null);
            assertEquals(0, intermod.compareTo(intermodLo));
            assertTrue(intermod.compareTo(intermodHi) < 0);
            assertTrue(intermodHi.compareTo(intermodLo) > 0);

            assertEquals(intermod, intermodLo);
            assertNotEquals(intermod, intermodHi);
        }

        @DisplayName("compare intermodulation frequencies")
        @Test
        final void testCompareToType() throws Exception {
            Intermod intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            Intermod intermodLo = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            Intermod intermodHi = new Intermod(Intermod.Type.IM_2T5O, channel2, new Channel(0, 606.600, equipment), null);
            assertEquals(intermodLo.getFreq(), intermodHi.getFreq());
            assertEquals(0, intermod.compareTo(intermodLo));
            assertTrue(intermod.compareTo(intermodHi) < 0);
            assertTrue(intermodHi.compareTo(intermodLo) > 0);

            assertEquals(intermod, intermodLo);
            assertNotEquals(intermod, intermodHi);
        }

        @DisplayName("compare intermodulation channel 1 frequencies")
        @Test
        final void testCompareToCh1() throws Exception {
            Intermod intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            Intermod intermodLo = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            Intermod intermodHi = new Intermod(Intermod.Type.IM_2T3O, channel2, new Channel(0, 606.900, equipment), null);
            assertEquals(intermodLo.getFreq(), intermodHi.getFreq());
            assertEquals(intermodLo.getType(), intermodHi.getType());
            assertEquals(0, intermod.compareTo(intermodLo));
            assertTrue(intermod.compareTo(intermodHi) < 0);
            assertTrue(intermodHi.compareTo(intermodLo) > 0);

            assertEquals(intermod, intermodLo);
            assertNotEquals(intermod, intermodHi);
        }

        @DisplayName("compare intermodulation channel 2 frequencies")
        @Test
        final void testCompareToCh2() throws Exception {
            Intermod intermod = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3);
            Intermod intermodLo = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3);
            Intermod intermodHi = new Intermod(Intermod.Type.IM_3T3O, channel1, channel3, new Channel(0, 607.1, equipment));
            assertEquals(intermodLo.getFreq(), intermodHi.getFreq());
            assertEquals(intermodLo.getType(), intermodHi.getType());
            assertEquals(intermodLo.getF1().getFreq(), intermodHi.getF1().getFreq());
            assertEquals(0, intermod.compareTo(intermodLo));
            assertTrue(intermod.compareTo(intermodHi) < 0);
            assertTrue(intermodHi.compareTo(intermodLo) > 0);

            assertEquals(intermod, intermodLo);
            assertNotEquals(intermod, intermodHi);
        }

        @DisplayName("compare intermodulation channel 3 frequencies")
        @Test
        final void testCompareToCh3() throws Exception {
            Intermod intermod = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3);
            Intermod intermodLo = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3);
            Intermod intermodHi = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, new Channel(0, 607.1, equipment));

            assertEquals(intermod, intermodLo);
            assertNotEquals(intermod, intermodHi);
        }

        @DisplayName("get human readable description of Conflict")
        @Test
        final void testIntermodString() {
            Intermod intermod = new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null);
            assertEquals("606.000 MHz & 606.300 MHz", intermod.toString());

            intermod = new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3);
            assertEquals("606.000 MHz & 606.300 MHz & 606.700 MHz", intermod.toString());
        }
    }
}
