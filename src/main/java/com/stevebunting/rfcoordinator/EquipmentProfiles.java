package com.stevebunting.rfcoordinator;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;

public enum EquipmentProfiles {

    // Singleton Instance
    INSTANCE;

    // Array to hold all equipment profiles
    private final Equipment[] equipmentArray;

    EquipmentProfiles() {
        equipmentArray = parseJson();
    }

    private Equipment[] parseJson() {
        ObjectMapper mapper = new ObjectMapper();
        try {
            return mapper.readValue(new File("src/main/resources/EquipmentProfiles.json"), Equipment[].class);
        } catch (Exception e) {
            return new Equipment[0];
        }
    }

    final Equipment get(final int index) {
        if (index < 0 || index >= equipmentArray.length) {
            return null;
        }
        return equipmentArray[index];
    }

    final int getCount() {
        return equipmentArray.length;
    }
}
