package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

@DisplayName("Equipment Class...")
class EquipmentTests {

    @DisplayName("contains methods that...")
    @Nested
    class EquipmentMethodTests {

        // Equipment object under test
        private Equipment equipment;

        @BeforeEach
        void setUp() {
            equipment = new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 50);
        }

        @DisplayName("measure equality for each component between 2 equipment objects")
        @Test
        final void testEquipmentEquality() {
            assertTrue(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Not Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "Not PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 0, 900, 25, 300, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 0, 25, 300, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 0, 300, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 0, 100, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 300, 0, 90, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 0, 0, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 1000, 0, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 1000, 50)));
            assertFalse(equipment.equals(new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 0)));
        }

        @DisplayName("get human readable description of equipment")
        @Test
        final void testToString() {
            assertEquals("Shure PSM900", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (no model)")
        @Test
        final void testToStringNoModel() {
            equipment = new Equipment("Shure", "", 0, 0, 0, 0, 0, 0, 0, 0, 0);
            assertEquals("Shure", equipment.toString());

            equipment = new Equipment("Shure", null, 0, 0, 0, 0, 0, 0, 0, 0, 0);
            assertEquals("Shure", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (no manufacturer)")
        @Test
        final void testToStringNoManufacturer() {
            equipment = new Equipment("", "PSM900", 0, 0, 0, 0, 0, 0, 0, 0, 0);
            assertEquals("PSM900", equipment.toString());

            equipment = new Equipment(null, "PSM900", 0, 0, 0, 0, 0, 0, 0, 0, 0);
            assertEquals("PSM900", equipment.toString());
        }

        @DisplayName("get manufacturer name")
        @Test
        final void testGetManufacturer() {
            assertEquals("Shure", equipment.getManufacturer());
        }

        @DisplayName("get manufacturer name with blank/null manufacturer")
        @Test
        final void testGetNullManufacturer() {
            Equipment nullManufacturerEquipment = new Equipment(null, "PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 50);
            assertEquals("", nullManufacturerEquipment.getManufacturer());
        }

        @DisplayName("get model name")
        @Test
        final void testGetModel() {
            assertEquals("PSM900", equipment.getModel());
        }

        @DisplayName("get model name with blank/null model")
        @Test
        final void testGetNullModel() {
            Equipment nullModelEquipment = new Equipment("Shure", null, 470, 900, 25, 300, 100, 90, 0, 0, 50);
            assertEquals("", nullModelEquipment.getModel());
        }

        @DisplayName("get highest tunable frequency (in kHz)")
        @Test
        final void testGetRangeHi() {
            assertEquals(900000, equipment.getRangeHi());
        }

        @DisplayName("get lowest tunable frequency (in kHz)")
        @Test
        final void testGetRangeLo() {
            assertEquals(470000, equipment.getRangeLo());
        }

        @DisplayName("get tuning accuracy (in kHz)")
        @Test
        final void testGetTuningAccuracy() {
            assertEquals(25, equipment.getTuningAccuracy());
        }

        @DisplayName("get minimum channel-to-channel spacing (in kHz)")
        @Test
        final void testGetChannelSpacing() {
            assertEquals(300, equipment.getChannelSpacing());
        }

        @DisplayName("get minimum channel-to-2T3O IM spacing (in kHz)")
        @Test
        final void testGet2t3oSpacing() {
            assertEquals(100, equipment.get2t3oSpacing());
        }

        @DisplayName("get minimum channel-to-2T5O IM spacing (in kHz)")
        @Test
        final void testGet2t5oSpacing() {
            assertEquals(90, equipment.get2t5oSpacing());
        }

        @DisplayName("get minimum channel-to-2T7O IM spacing (in kHz)")
        @Test
        final void testGet2t7oSpacing() {
            assertEquals(0, equipment.get2t7oSpacing());
        }

        @DisplayName("get minimum channel-to-2T9O IM spacing (in kHz)")
        @Test
        final void testGet2t9oSpacing() {
            assertEquals(0, equipment.get2t9oSpacing());
        }

        @DisplayName("get minimum channel-to-3T3O IM spacing (in kHz)")
        @Test
        final void testGet3t3oSpacing() {
            assertEquals(50, equipment.get3t3oSpacing());
        }

        @DisplayName("get maximum channel-to-IM spacing (in kHz)")
        @Test
        final void testMaxImSpacing() {
            assertEquals(100, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 150, 0, 0, 50);
            assertEquals(150, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 200, 0, 50);
            assertEquals(200, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 250, 50);
            assertEquals(250, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 470, 900, 25, 300, 100, 90, 0, 0, 350);
            assertEquals(350, equipment.getMaxImSpacing());
        }
    }
}
