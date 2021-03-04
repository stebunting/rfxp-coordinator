package com.stevebunting.rfcoordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Channel implements Comparable<Channel> {

    // Enum for declaring validity of channel
    enum Validity {
        VALID,
        INVALID,
        WARNING
    }

    // Locale for string formatting
    private final Locale locale = Locale.getDefault();

    // Integer to store channel ID
    private final int id;

    // Integer to store frequency (kHz)
    private int frequency;

    // String to store name
    private String name;

    // Equipment object to store equipment profile
    private Equipment equipment;

    // Channel validity
    private Validity validity;

    // Array to store channels conflicts
    private final ArrayList<Conflict> conflicts = new ArrayList<>();

    // Constructor to create channel with frequency
    Channel(
            final Integer id,
            final double frequency,
            @NotNull final Equipment equipment
    ) throws InvalidFrequencyException {
        this(id, frequency, String.format("Channel %d", id != null ? id + 1 : 1), equipment);
    }

    Channel(
            final Integer id,
            final double frequency,
            @NotNull final String name,
            @NotNull final Equipment equipment
    ) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("A valid equipment profile must be supplied");
        }
        if (isEquipmentUntunable(mHzToKHz(frequency), equipment)) {
            throw new InvalidFrequencyException();
        }
        this.frequency = mHzToKHz(frequency);
        this.equipment = equipment;
        this.id = id != null ? id : 0;
        this.name = name;
        setValidity();
    }

    // Check if equipment and frequency are compatible
    private boolean isEquipmentUntunable(int frequency, Equipment equipment) {
        return frequency % equipment.getTuningAccuracy() != 0;
    }

    // Method to return a new channel instance with the same data
    // Equipment is still a reference to the master equipment array
    final Channel deepCopy() {
        Channel channelCopy;
        try {
            channelCopy = new Channel(id, kHzToMHz(frequency), name, equipment);
        } catch (InvalidFrequencyException e) {
            return null;
        }
        for (Conflict conflict: conflicts) {
            channelCopy.addConflict(conflict);
        }
        return channelCopy;
    }

    // COMPARING METHODS
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Channel)) {
            return false;
        }
        Channel that = (Channel) obj;
        return this.getEquipment().equals(that.getEquipment())
                && this.getName().equals(that.getName())
                && this.getFreq() == that.getFreq();
    }

    @Override
    public int compareTo(Channel that) {
        return Integer.compare(this.getFreq(), that.getFreq());
    }

    @Override
    public final String toString() {
        return String.format(locale, "%.3f", kHzToMHz(frequency));
    }

    final String toStringWithMHz() {
        return String.format(locale, "%s MHz", toString());
    }

    // HELPER FUNCTIONS
    static int mHzToKHz(double frequency) {
        return (int) Math.round(frequency * 1000);
    }

    static double kHzToMHz(int frequency) {
        return ((double) frequency) / 1000;
    }

    // GETTERS AND SETTERS
    final int getId() {
        return id;
    }

    final String getName() {
        return name;
    }

    final void setName(final String name) {
        this.name = name;
    }

    // Return frequency in kHz
    final int getFreq() {
        return frequency;
    }

    final Equipment getEquipment() {
        return equipment;
    }

    final void setFreq(int frequency) throws InvalidFrequencyException {
        setFreqAndEquipment(frequency, equipment);
    }

    final void setFreq(double frequency) throws InvalidFrequencyException {
        setFreqAndEquipment(frequency, equipment);
    }

    final void setEquipment(@NotNull final Equipment equipment) throws InvalidFrequencyException {
        setFreqAndEquipment(this.frequency, equipment);
    }

    final void setFreqAndEquipment(double frequency, @NotNull final Equipment equipment) throws InvalidFrequencyException {
        setFreqAndEquipment(mHzToKHz(frequency), equipment);
    }

    final void setFreqAndEquipment(int frequency, @NotNull final Equipment equipment) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment must not be null");
        }
        if (isEquipmentUntunable(frequency, equipment)) {
            throw new InvalidFrequencyException();
        }
        this.frequency = frequency;
        this.equipment = equipment;
    }

    @NotNull
    final Validity getValidity() {
        return validity;
    }

    private void setValidity() {
        if (conflicts.isEmpty()) {
            validity = Validity.VALID;
        } else if (conflicts.size() == 1 && conflicts.get(0).getType() == Conflict.Type.WHITE_SPACE) {
            validity = Validity.WARNING;
        } else {
            validity = Validity.INVALID;
        }
    }

    /**
     * Add a reference to a relevant conflict to the channels conflict list.
     *
     * @param conflict conflict to add
     */
    final void addConflict(final Conflict conflict) {
        if (conflict != null) {
            conflicts.add(conflict);
            setValidity();
        }
    }

    final boolean removeConflict(final Conflict conflict) {
        if (conflicts.remove(conflict)) {
            setValidity();
            return true;
        }
        return false;
    }

    final void clearConflicts() {
        conflicts.clear();
        setValidity();
    }

    final int getNumConflicts() {
        return conflicts.size();
    }

    final List<Conflict> getConflicts() {
        return conflicts;
    }
}
