package com.stevebunting.rfcoordinator;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.fasterxml.jackson.databind.ObjectMapper;

enum EquipmentProfiles {

    // Singleton Instance
    INSTANCE;

    // Array to hold all equipment profiles
    final private Equipment[] equipmentArray;

    EquipmentProfiles() {
        equipmentArray = parseJson();
    }

    private Equipment[] parseJson() {
        final ObjectMapper mapper = new ObjectMapper();
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

    final Equipment getByName(String manufacturer, String model) {
        manufacturer = manufacturer.toLowerCase();
        model = model.toLowerCase();

        for (Equipment equipment : equipmentArray) {
            if (equipment.getManufacturer().toLowerCase().equals(manufacturer)
                    && equipment.getModel().toLowerCase().equals(model)) {
                return equipment;
            }
        }
        return null;
    }
}
