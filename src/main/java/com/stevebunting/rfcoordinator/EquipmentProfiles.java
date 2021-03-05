package com.stevebunting.rfcoordinator;

import java.io.File;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jetbrains.annotations.NotNull;

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
                new Equipment(null, "Default", 1,300, 100, 90, 0, 0, 50)
            };
        }
    }

    final Equipment get(final int index) {
        if (index < 0 || index >= equipmentArray.length) {
            return null;
        }
        return equipmentArray[index];
    }

    final Equipment get(@NotNull String manufacturer, @NotNull String model) {
        if (manufacturer == null || model == null) {
            return null;
        }

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

    final int getCount() {
        return equipmentArray.length;
    }
}
