package com.stevebunting.rfxp.coordinator;

import org.jetbrains.annotations.NotNull;
import java.util.*;

class ChannelGeneratorWrapper implements Comparable<ChannelGeneratorWrapper> {
    final SplittableRandom rand = new SplittableRandom();

    private final Channel channel;
    private Integer proposedFrequency;
    private Map<Integer, Integer> possibleFrequencies;
    private final int maxPossibleFrequencies;

    ChannelGeneratorWrapper(@NotNull final Channel channel) {
        this.channel = channel;
        maxPossibleFrequencies = calculateMaxPossibleFrequencies();
    }

    final int calculateMaxPossibleFrequencies() {
        final Range range = this.channel.getRange();
        final int tuningAccuracy = this.channel.getEquipment().getTuningAccuracy();
        final int low = getFirstValidFrequencyInRange(range.getLo());

        if (low > range.getHi()) {
            return 0;
        }
        return 1 + ((range.getHi() - low) / tuningAccuracy);
    }

    final void getPossibleFrequencies(
            @NotNull final List<Channel> channels,
            @NotNull final List<Intermod> intermods
    ) {
        getBaseFrequencies();

        for (Channel ch : channels) {
            removeConflictRange(ch);
        }
        for (Intermod im : intermods) {
            removeConflictRange(im);
        }
    }

    private void getBaseFrequencies() {
        possibleFrequencies = new HashMap<>();

        final Range range = channel.getRange();
        final Equipment equipment = channel.getEquipment();

        int counter = range.getLo();
        while (counter <= range.getHi()) {
            possibleFrequencies.put(counter, counter);
            counter += equipment.getTuningAccuracy();
        }
    }

    private int getFirstValidFrequencyInRange(final int low) {
        final Equipment equipment = channel.getEquipment();
        return equipment.getTuningAccuracy() * (int) Math.ceil((low) / (double) equipment.getTuningAccuracy());
    }

    private void removeConflictRange(
            @NotNull final FrequencyComponent component
    ) {
        final Equipment equipment = channel.getEquipment();
        final Range range = channel.getRange();

        final int spacing = component instanceof Channel
                ? Math.max(equipment.getChannelSpacing(), ((Channel) component).getEquipment().getChannelSpacing())
                : equipment.getSpacing(((Intermod) component).getType());

        final int rangeLo = component.getFreq() - spacing;
        final int rangeHi = component.getFreq() + spacing;
        final int startFreq = getFirstValidFrequencyInRange(rangeLo + 1);

        // TODO: This can be refined, very rough
        if (rangeHi < range.getLo() || rangeLo > range.getHi()) {
            return;
        }

        for (int i = startFreq; i < rangeHi; i += equipment.getTuningAccuracy()) {
            possibleFrequencies.remove(i);
        }
    }

    final void setTestFrequency(final boolean randomSelection) throws InvalidFrequencyException {
        final Set<Integer> keySet = possibleFrequencies.keySet();
        proposedFrequency = randomSelection
                ? (int) keySet.toArray()[rand.nextInt(possibleFrequencies.size())]
                : Collections.min(keySet);
        channel.setFreq(proposedFrequency);
        possibleFrequencies.remove(proposedFrequency);
    }

    final boolean hasPossibleFrequencies() {
        return possibleFrequencies.size() > 0;
    }

    final Channel getChannel() {
        return channel;
    }

    @NotNull
    final Integer getProposedFrequency() {
        return proposedFrequency;
    }

    final int getMaxPossibleFrequencies() {
        return maxPossibleFrequencies;
    }

    @Override
    public int compareTo(@NotNull ChannelGeneratorWrapper that) {
        return 0;
    }
}
