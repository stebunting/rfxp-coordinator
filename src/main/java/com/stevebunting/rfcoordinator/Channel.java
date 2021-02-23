package com.stevebunting.rfcoordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Comparator;
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
    private final ArrayList<Conflict> conflicts;

    // Constructor to create channel with frequency
    Channel(final Integer id, final double frequency, @NotNull final Equipment equipment)
            throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("A valid equipment profile must be supplied");
        }
        if (isEquipmentUntunable(mHzToKHz(frequency), equipment)) {
            throw new InvalidFrequencyException();
        }
        this.frequency = mHzToKHz(frequency);
        this.equipment = equipment;
        this.id = id != null ? id : 0;
        this.name = String.format(locale, "Channel %d", this.id + 1);
        this.conflicts = new ArrayList<>();
        setValidity();
    }

    // Check if equipment and frequency are compatible
    private boolean isEquipmentUntunable(int frequency, Equipment equipment) {
        return frequency % equipment.getTuningAccuracy() != 0;
    }

    // COMPARING METHODS
    public final boolean equals(Channel that) {
        return this.getEquipment().equals(that.getEquipment())
                && this.getName().equals(that.getName())
                && this.getFreq() == that.getFreq();
    }

    @Override
    public int compareTo(Channel that) {
        return Integer.compare(this.getFreq(), that.getFreq());
    }

    // Comparator to sort channels by ID
    final private static class IDComparator implements Comparator<Channel> {
        @Override
        public int compare(Channel a, Channel b) {
            int result = Integer.compare(a.getId(), b.getId());
            return result == 0 ? a.compareTo(b) : result;
        }
    }
    static Comparator<Channel> sortByID() {
        return new IDComparator();
    }

    // Comparator to sort channels by Name
    final private static class NameComparator implements Comparator<Channel> {
        @Override
        public int compare(Channel a, Channel b) {
            int result = a.getName().compareToIgnoreCase(b.getName());
            return result == 0 ? a.compareTo(b) : result;
        }
    }
    static Comparator<Channel> sortByName() {
        return new NameComparator();
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

    // Return frequency in kHz
    final int getFreq() {
        return frequency;
    }

    final void setFreq(int frequency) throws InvalidFrequencyException {
        if (isEquipmentUntunable(frequency, this.equipment)) {
            throw new InvalidFrequencyException();
        }
        this.frequency = frequency;
    }

    final void setFreq(double frequency) throws InvalidFrequencyException {
        setFreq(mHzToKHz(frequency));
    }

    final String getName() {
        return name;
    }

    final void setName(final String name) {
        this.name = name;
    }

    final Equipment getEquipment() {
        return equipment;
    }

    final void setEquipment(@NotNull final Equipment equipment) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment must not be null");
        }
        if (isEquipmentUntunable(this.frequency, equipment)) {
            throw new InvalidFrequencyException();
        }
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
}
