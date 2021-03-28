package com.stevebunting.rfxp.coordinator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

public class Equipment {

    // INSTANCE VARIABLES
    // String to store manufacturer name
    @NotNull private final String manufacturer;

    // String to store model name
    @NotNull private final String model;

    // Integers to store tuning accuracy (kHz)
    private final int tuningAccuracy;

    // Integers to store spacing values (kHz)
    private final int spacingChannel;
    private final int spacing2t3o;
    private final int spacing2t5o;
    private final int spacing2t7o;
    private final int spacing2t9o;
    private final int spacing3t3o;
    private final int maxImSpacing;

    // Tuning range
    private Range range;

    // CONSTRUCTORS
    // Constructor to create equipment profile
    @JsonCreator(mode = JsonCreator.Mode.PROPERTIES)
    Equipment(@JsonProperty("manufacturer") final String manufacturer,
              @JsonProperty("model") final String model,
              @JsonProperty("tuningAccuracy") final int tuningAccuracy,
              @JsonProperty("spacingChannel") final int spacingChannel,
              @JsonProperty("spacing2t3o") final int spacing2t3o,
              @JsonProperty("spacing2t5o") final int spacing2t5o,
              @JsonProperty("spacing2t7o") final int spacing2t7o,
              @JsonProperty("spacing2t9o") final int spacing2t9o,
              @JsonProperty("spacing3t3o") final int spacing3t3o) {
        this(manufacturer, model, tuningAccuracy, spacingChannel, spacing2t3o, spacing2t5o, spacing2t7o, spacing2t9o, spacing3t3o, null);
    }

    Equipment(@NotNull final String manufacturer,
              @NotNull final String model,
              final int tuningAccuracy,
              final int spacingChannel,
              final int spacing2t3o,
              final int spacing2t5o,
              final int spacing2t7o,
              final int spacing2t9o,
              final int spacing3t3o,
              final Range range) {
        this.manufacturer = manufacturer != null ? manufacturer : "";
        this.model = model != null ? model : "";
        this.tuningAccuracy = tuningAccuracy;
        this.spacingChannel = spacingChannel;
        this.spacing2t3o = spacing2t3o;
        this.spacing2t5o = spacing2t5o;
        this.spacing2t7o = spacing2t7o;
        this.spacing2t9o = spacing2t9o;
        this.spacing3t3o = spacing3t3o;
        this.maxImSpacing = Math.max(Math.max(Math.max(Math.max(spacing2t3o, spacing2t5o), spacing2t7o), spacing2t9o), spacing3t3o);
        this.range = range;
    }

    final boolean isFrequencyValid(final int frequency) {
        return (range == null || (frequency >= range.getLo() && frequency <= range.getHi()))
                && (frequency % tuningAccuracy == 0);
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
                && this.getTuningAccuracy() == that.getTuningAccuracy()
                && this.getChannelSpacing() == that.getChannelSpacing()
                && this.getSpacing(Intermod.Type.IM_2T3O) == that.getSpacing(Intermod.Type.IM_2T3O)
                && this.getSpacing(Intermod.Type.IM_2T5O) == that.getSpacing(Intermod.Type.IM_2T5O)
                && this.getSpacing(Intermod.Type.IM_2T7O) == that.getSpacing(Intermod.Type.IM_2T7O)
                && this.getSpacing(Intermod.Type.IM_2T9O) == that.getSpacing(Intermod.Type.IM_2T9O)
                && this.getSpacing(Intermod.Type.IM_3T3O) == that.getSpacing(Intermod.Type.IM_3T3O));
    }

    // Print object
    @Override
    public String toString() {
        final String rangePostfix = range != null && !range.getName().equals("")
                ? String.format(" %s", range.getName())
                : "";
        final String stringFormat = manufacturer.equals("") || model.equals("")
                ? "%s%s%s"
                : "%s %s%s";
        return String.format(stringFormat, manufacturer, model, rangePostfix);
    }

    // GETTERS AND SETTERS
    final void setRange(final Range range) {
        this.range = range;
    }

    final Range getRange() {
        return range;
    }

    @NotNull
    final String getManufacturer() {
        return manufacturer;
    }

    @NotNull
    final String getModel() {
        return model;
    }

    final int getTuningAccuracy() {
        return tuningAccuracy;
    }

    final int getChannelSpacing() {
        return spacingChannel;
    }

    final int getMaxImSpacing() {
        return maxImSpacing;
    }

    final int getSpacing(@NotNull final Intermod.Type type) {
        switch (type) {
            case IM_2T3O:
                return spacing2t3o;

            case IM_2T5O:
                return spacing2t5o;

            case IM_2T7O:
                return spacing2t7o;

            case IM_2T9O:
                return spacing2t9o;

            case IM_3T3O:
                return spacing3t3o;

            default:
                return 0;
        }
    }
}
