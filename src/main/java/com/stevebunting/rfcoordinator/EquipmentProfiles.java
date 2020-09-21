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
            return mapper.readValue(new File("src/main/resources/data/EquipmentProfiles.json"), Equipment[].class);
        } catch (Exception e) {
            return new Equipment[] {
                    new Equipment(null, "Default", 0, 10000, 1,300, 100, 90, 0, 0, 50)
            };
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
