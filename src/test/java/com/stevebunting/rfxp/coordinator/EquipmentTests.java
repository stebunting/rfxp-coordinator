package com.stevebunting.rfxp.coordinator;

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
            equipment = new Equipment("Shure", "PSM900", 25, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000);
        }

        @DisplayName("ensure validity of frequency without range")
        @Test
        final void testFrequencyValidity() {
            assertTrue(equipment.isFrequencyValid(500750));
            assertTrue(equipment.isFrequencyValid(900000));
            assertTrue(equipment.isFrequencyValid(560275));
            assertTrue(equipment.isFrequencyValid(500750));
            assertFalse(equipment.isFrequencyValid(500751));
            assertFalse(equipment.isFrequencyValid(452874));
        }

        @DisplayName("measure equality for each component between 2 equipment objects")
        @Test
        final void testEquipmentEquality() {
            assertEquals(equipment, equipment);
            assertNotEquals(equipment, null);
            assertEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Not Shure", "PSM900", 25, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "Not PSM900", 25, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 0, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 0, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 0, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 0, 100, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 190, 0, 75, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 190, 100, 0, 50, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 190, 100, 75, 0, Equipment.FrontEndType.TRACKING, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.FIXED, 100000));
            assertNotEquals(equipment, new Equipment("Shure", "PSM900", 25, 300, 200, 190, 100, 75, 50, Equipment.FrontEndType.TRACKING, 200000));
        }

        @DisplayName("get human readable description of equipment")
        @Test
        final void testToString() {
            assertEquals("Shure PSM900", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (no model)")
        @Test
        final void testToStringNoModel() {
            equipment = new Equipment("Shure", "", 0, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals("Shure", equipment.toString());

            equipment = new Equipment("Shure", null, 0, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals("Shure", equipment.toString());
        }

        @DisplayName("get human readable description of equipment (no manufacturer)")
        @Test
        final void testToStringNoManufacturer() {
            equipment = new Equipment("", "PSM900", 0, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals("PSM900", equipment.toString());

            equipment = new Equipment(null, "PSM900", 0, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 100000);
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
            Equipment nullManufacturerEquipment = new Equipment(null, "PSM900", 25, 300, 100, 90, 0, 0, 50, Equipment.FrontEndType.TRACKING, 100000);
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
            Equipment nullModelEquipment = new Equipment("Shure", null, 25, 300, 100, 90, 0, 0, 50, Equipment.FrontEndType.TRACKING, 100000);
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
            assertEquals(200, equipment.getSpacing(Intermod.Type.IM_2T3O));
        }

        @DisplayName("get minimum channel-to-2T5O IM spacing (in kHz)")
        @Test
        final void testGet2t5oSpacing() {
            assertEquals(190, equipment.getSpacing(Intermod.Type.IM_2T5O));
        }

        @DisplayName("get minimum channel-to-2T7O IM spacing (in kHz)")
        @Test
        final void testGet2t7oSpacing() {
            assertEquals(100, equipment.getSpacing(Intermod.Type.IM_2T7O));
        }

        @DisplayName("get minimum channel-to-2T9O IM spacing (in kHz)")
        @Test
        final void testGet2t9oSpacing() {
            assertEquals(75, equipment.getSpacing(Intermod.Type.IM_2T9O));
        }

        @DisplayName("get minimum channel-to-3T3O IM spacing (in kHz)")
        @Test
        final void testGet3t3oSpacing() {
            assertEquals(50, equipment.getSpacing(Intermod.Type.IM_3T3O));
        }

        @DisplayName("get maximum channel-to-IM spacing (in kHz)")
        @Test
        final void testMaxImSpacing() {
            assertEquals(200, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 150, 0, 0, 50, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals(150, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 200, 0, 50, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals(200, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 250, 50, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals(250, equipment.getMaxImSpacing());

            equipment = new Equipment("Shure", "PSM900", 25, 300, 100, 90, 0, 0, 350, Equipment.FrontEndType.TRACKING, 100000);
            assertEquals(350, equipment.getMaxImSpacing());
        }

        @DisplayName("get front end filter type")
        @Test
        final void testGetFrontEndFilterType() {
            assertEquals(Equipment.FrontEndType.TRACKING, equipment.getFrontEndFilterType());
        }

        @DisplayName("get front end filter spacing (in kHz)")
        @Test
        final void testGetFrontEndFilter() {
            assertEquals(100000, equipment.getFrontEndFilter());
        }
    }
}
