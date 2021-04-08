package com.stevebunting.rfxp.coordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

class Channel implements Comparable<Channel>, FrequencyComponent {
    static final Locale locale = Locale.getDefault();

    private final int id;
    private int frequency;
    private String name;
    private Equipment equipment;
    private Range range;

    enum Validity {
        VALID,
        INVALID,
        WARNING
    }
    private Validity validity;

    private final ArrayList<Conflict> conflicts = new ArrayList<>();

    Channel(final Integer id,
            final double frequency,
            @NotNull final Equipment equipment
    ) throws InvalidFrequencyException {
        this(id, frequency, String.format("Channel %d", id != null ? id + 1 : 1), equipment);
    }

    Channel(final Integer id,
            final double frequency,
            @NotNull final String name,
            @NotNull final Equipment equipment
    ) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("A valid equipment profile must be supplied");
        }
        if (!equipment.isFrequencyValid(mhzToKhz(frequency))) {
            throw new InvalidFrequencyException();
        }
        this.frequency = mhzToKhz(frequency);
        this.equipment = equipment;
        this.id = id != null ? id : 0;
        this.name = name;
        setValidity();
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

    final Channel deepCopy() {
        Channel channelCopy;
        try {
            channelCopy = new Channel(id, khzToMhz(frequency), name, equipment);
        } catch (InvalidFrequencyException e) {
            return null;
        }
        for (Conflict conflict : conflicts) {
            channelCopy.addConflict(conflict);
        }
        return channelCopy;
    }

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
        return String.format(locale, "%.3f", khzToMhz(frequency));
    }

    final String toStringWithMHz() {
        return formattedFrequency(frequency);
    }

    final int getId() {
        return id;
    }

    final String getName() {
        return name;
    }

    final void setName(final String name) {
        this.name = name;
    }

    public final int getFreq() {
        return frequency;
    }

    final Equipment getEquipment() {
        return equipment;
    }

    final void setFreq(final double frequency) throws InvalidFrequencyException {
        setFreq(mhzToKhz(frequency));
    }

    final void setFreq(final int frequency) throws InvalidFrequencyException {
        if (range != null && !range.isValidFrequency(frequency)) {
            throw new InvalidFrequencyException();
        }
        setFreqAndEquipment(frequency, equipment, range);
    }

    final void setEquipment(@NotNull final Equipment equipment) throws InvalidFrequencyException {
        setFreqAndEquipment(this.frequency, equipment, null);
    }

    final void setFreqAndEquipment(
            int frequency,
            @NotNull final Equipment equipment,
            @NotNull final Range range
    ) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("Equipment must not be null");
        }
        if (!equipment.isFrequencyValid(frequency)) {
            throw new InvalidFrequencyException();
        }
        this.frequency = frequency;
        this.equipment = equipment;
        this.range = range;
    }

    @NotNull
    final Validity getValidity() {
        return validity;
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

    final List<Conflict> getConflicts() {
        return conflicts;
    }

    final List<Range> getAssignableRanges() {
        List<Range> ranges = new ArrayList<>();
        for (Range range: equipment.getRanges()) {
            if (range.isValidFrequency(frequency)) {
                ranges.add(range);
            }
        }
        return ranges;
    }

    final void setRange(@NotNull final Range range) {
        if (range == null || !equipment.isValidRange(range)) {
            throw new IllegalArgumentException();
        }
        this.range = range;
    }

    final Range getRange() {
        return range;
    }

    final boolean hasRange() {
        return range != null;
    }

    static int mhzToKhz(double frequency) {
        return (int) Math.round(frequency * 1000);
    }

    static double khzToMhz(int frequency) {
        return ((double) frequency) / 1000;
    }

    static String formattedFrequency(final int frequency) {
        return String.format(locale, "%.3f MHz", khzToMhz(frequency));
    }
}
