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
            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 50);
        }

        @DisplayName("ensure validity of frequency without range")
        @Test
        final void testFrequencyValidity() {
            assert(equipment.isFrequencyValid(500750));
            assertFalse(equipment.isFrequencyValid(500751));
        }

        @DisplayName("ensure validity of frequency with range")
        @Test
        final void testFrequencyValidityWithRange() {
            Range range = new Range(400000, 500000, "Generic");
            Equipment equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 50, range);
            assert(equipment.isFrequencyValid(450025));
            assertFalse(equipment.isFrequencyValid(450026));
            assert(equipment.isFrequencyValid(400000));
            assertFalse(equipment.isFrequencyValid(399999));
            assertFalse(equipment.isFrequencyValid(399975));
            assert(equipment.isFrequencyValid(500000));
            assertFalse(equipment.isFrequencyValid(500001));
            assertFalse(equipment.isFrequencyValid(500025));
            assertFalse(equipment.isFrequencyValid(200300));
            assertFalse(equipment.isFrequencyValid(800075));
        }

        @DisplayName("measure equality for each component between 2 equipment objects")
        @Test
        final void testEquipmentEquality() {
            assertEquals(equipment, equipment);
            assertNotEquals(equipment, null);
            assertEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Not Shure", "PSM900", 25, 300, 100, 90, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "Not PSM900", 25, 300, 100, 90, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 0, 300, 100, 90, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 0, 100, 90, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 0, 90, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 100, 0, 0, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 100, 90, 1000, 0, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 1000, 50));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 0));
        }

        @DisplayName("get human readable description of equipment without range")
        @Test
        final void testToStringNoRange() {
            assertEquals("Shure PSM900", equipment.toString());
        }

        @DisplayName("get human readable description of equipment with range")
        @Test
        final void testToStringWithRange() {
            equipment.setRange(new Range(400, 500, "GB"));
            assertEquals("Shure PSM900 GB", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (no model)")
        @Test
        final void testToStringNoModel() {
            equipment = new Equipment("Shure", "", 0, 0, 0, 0, 0, 0, 0);
            assertEquals("Shure", equipment.toString());

            equipment = new Equipment("Shure", null, 0, 0, 0, 0, 0, 0, 0);
            assertEquals("Shure", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (no manufacturer)")
        @Test
        final void testToStringNoManufacturer() {
            equipment = new Equipment("", "PSM900", 0, 0, 0, 0, 0, 0, 0);
            assertEquals("PSM900", equipment.toString());

            equipment = new Equipment(null, "PSM900", 0, 0, 0, 0, 0, 0, 0);
            assertEquals("PSM900", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (empty range)")
        @Test
        final void testToStringEmptyRange() {

            equipment.setRange(new Range(400, 500, ""));
            assertEquals("Shure PSM900", equipment.toString());
        }

        @DisplayName("get manufacturer name")
        @Test
        final void testGetManufacturer() {
            assertEquals("Shure", equipment.getManufacturer());
        }

        @DisplayName("get manufacturer name with blank/null manufacturer")
        @Test
        final void testGetNullManufacturer() {
            Equipment nullManufacturerEquipment = new Equipment(null, "PSM900", 25, 300, 100, 90, 0, 0, 50);
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
            Equipment nullModelEquipment = new Equipment("Shure", null, 25, 300, 100, 90, 0, 0, 50);
            assertEquals("", nullModelEquipment.getModel());
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

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 150, 0, 0, 50);
            assertEquals(150, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 200, 0, 50);
            assertEquals(200, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 250, 50);
            assertEquals(250, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 350);
            assertEquals(350, equipment.getMaxImSpacing());
        }
    }
}
