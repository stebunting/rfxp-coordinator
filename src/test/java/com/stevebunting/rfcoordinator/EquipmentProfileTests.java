package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
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
        //noinspection ConstantConditions
        assertEquals(equipmentProfiles.get(0).toString(), "Generic IEM");
    }

    @DisplayName("gets null for invalid equipment indexes")
    @Test
    final void testInvalidIndexes() {
        assertNull(equipmentProfiles.get(-1));
        assertNull(equipmentProfiles.get(25));
        assertNull(equipmentProfiles.get(12));
        assertNotNull(equipmentProfiles.get(11));
    }
}
