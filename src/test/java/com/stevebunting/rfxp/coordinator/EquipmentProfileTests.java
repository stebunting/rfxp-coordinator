package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

@DisplayName("EquipmentProfiles Class...")
class EquipmentProfileTests {

    // Equipment Profiles Instance
    EquipmentProfiles equipmentProfiles;

    @BeforeEach
    void setUp() {
        equipmentProfiles = EquipmentProfiles.INSTANCE;
    }

    @DisplayName("is singleton")
    @Test
    final void testSingletonStatus() {
        EquipmentProfiles equipmentProfiles2 = EquipmentProfiles.INSTANCE;
        assertSame(equipmentProfiles, equipmentProfiles2);
    }

    @DisplayName("initialises constant equipment profiles")
    @Test
    final void testEquipmentProfilesLoaded() {
        assertEquals(equipmentProfiles.getCount(), 12);
        assertNotNull(equipmentProfiles.get(0));
        assertEquals(equipmentProfiles.get(0).toString(), "Generic IEM");
    }

    @DisplayName("can fail and supply single default Equipment type")
    @Test
    @Disabled
    final void testEquipmentProfilesFailed() {
        fail("Test needs to be written");
    }

    @DisplayName("can get equipment type by name")
    @Test
    final void testGetEquipmentByName() {
        Equipment equipment = equipmentProfiles.get("Shure", "UHF-R");
        assertNotNull(equipment);
        assertEquals("Shure UHF-R", equipment.toString());
    }

    @DisplayName("can get equipment type by case-insensitive name")
    @Test
    final void testGetEquipmentByCaseInsensitiveName() {
        Equipment equipment = equipmentProfiles.get("SENNHEISER", "g3/g4 mic");
        assertNotNull(equipment);
        assertEquals("Sennheiser G3/G4 Mic", equipment.toString());
    }

    @DisplayName("gets null for invalid equipment type")
    @Test
    final void testInvalidEquipmentName() {
        assertNull(equipmentProfiles.get("Invalid", "Equipment"));
        assertNull(equipmentProfiles.get("", "Model"));
        assertNull(equipmentProfiles.get("", "Equipment"));
        assertNull(equipmentProfiles.get("", ""));
    }

    @DisplayName("gets null for invalid equipment indexes")
    @Test
    final void testInvalidEquipmentIndexes() {
        assertNull(equipmentProfiles.get(-1));
        assertNull(equipmentProfiles.get(25));
        assertNull(equipmentProfiles.get(12));
        assertNotNull(equipmentProfiles.get(11));
    }
}
