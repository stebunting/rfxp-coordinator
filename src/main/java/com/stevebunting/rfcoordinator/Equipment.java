package com.stevebunting.rfcoordinator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.sun.istack.internal.NotNull;

public class Equipment {

    // INSTANCE VARIABLES
    // String to store manufacturer name
    @NotNull private final String manufacturer;

    // String to store model name
    @NotNull private final String model;

    // Integers to store range and tuning accuracy (kHz)
    private final int rangeLo;
    private final int rangeHi;
    private final int tuningAccuracy;

    // Integers to store spacing values (kHz)
    private final int spacingChannel;
    private final int spacing2t3o;
    private final int spacing2t5o;
    private final int spacing2t7o;
    private final int spacing2t9o;
    private final int spacing3t3o;
    private final int maxImSpacing;

    // CONSTRUCTORS
    // Constructor to create equipment profile
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Equipment(@JsonProperty("manufacturer") final String manufacturer,
              @JsonProperty("model") final String model,
              @JsonProperty("rangeLo") final int rangeLo,
              @JsonProperty("rangeHi") final int rangeHi,
              @JsonProperty("tuningAccuracy") final int tuningAccuracy,
              @JsonProperty("spacingChannel") final int spacingChannel,
              @JsonProperty("spacing2t3o") final int spacing2t3o,
              @JsonProperty("spacing2t5o") final int spacing2t5o,
              @JsonProperty("spacing2t7o") final int spacing2t7o,
              @JsonProperty("spacing2t9o") final int spacing2t9o,
              @JsonProperty("spacing3t3o") final int spacing3t3o) {
        this.manufacturer = manufacturer != null ? manufacturer : "";
        this.model = model != null ? model : "";
        this.rangeLo = rangeLo * 1000;
        this.rangeHi = rangeHi * 1000;
        this.tuningAccuracy = tuningAccuracy;
        this.spacingChannel = spacingChannel;
        this.spacing2t3o = spacing2t3o;
        this.spacing2t5o = spacing2t5o;
        this.spacing2t7o = spacing2t7o;
        this.spacing2t9o = spacing2t9o;
        this.spacing3t3o = spacing3t3o;
        this.maxImSpacing = Math.max(Math.max(Math.max(Math.max(spacing2t3o, spacing2t5o), spacing2t7o), spacing2t9o), spacing3t3o);
    }

    // OBJECT OVERRIDES
    // Method to check equality
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Equipment)) {
            return false;
        }
        Equipment that = (Equipment) obj;
        return (this.getManufacturer().equals(that.getManufacturer())
                && this.getModel().equals(that.getModel())
                && this.getRangeLo() == that.getRangeLo()
                && this.getRangeHi() == that.getRangeHi()
                && this.getTuningAccuracy() == that.getTuningAccuracy()
                && this.getChannelSpacing() == that.getChannelSpacing()
                && this.get2t3oSpacing() == that.get2t3oSpacing()
                && this.get2t5oSpacing() == that.get2t5oSpacing()
                && this.get2t7oSpacing() == that.get2t7oSpacing()
                && this.get2t9oSpacing() == that.get2t9oSpacing()
                && this.get3t3oSpacing() == that.get3t3oSpacing());
    }

    // Print object
    @Override
    public String toString() {
        final String stringFormat = manufacturer.equals("") || model.equals("")
                ? "%s%s"
                : "%s %s";
        return String.format(stringFormat, manufacturer, model);
    }

    // GETTERS AND SETTERS
    final String getManufacturer() {
        return manufacturer;
    }

    final String getModel() {
        return model;
    }

    final int getRangeHi() {
        return rangeHi;
    }

    final int getRangeLo() {
        return rangeLo;
    }

    final int getTuningAccuracy() {
        return tuningAccuracy;
    }

    final int getChannelSpacing() {
        return spacingChannel;
    }

    final int get2t3oSpacing() {
        return spacing2t3o;
    }

    final public int get2t5oSpacing() {
        return spacing2t5o;
    }

    final public int get2t7oSpacing() {
        return spacing2t7o;
    }

    final public int get2t9oSpacing() {
        return spacing2t9o;
    }

    final public int get3t3oSpacing() {
        return spacing3t3o;
    }

    final public int getMaxImSpacing() {
        return maxImSpacing;
    }
}
