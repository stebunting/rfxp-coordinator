package com.stevebunting.rfcoordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;

final class Coordination {

    // Counter to assign id's to channels
    private int idCounter = 0;

    // ArrayList to hold list of channels with id key
    final private ArrayList<Channel> channels = new ArrayList<>();

    // Analyser class
    final private Analyser analyser = new Analyser();

    // Add new channel to coordination
    final int addChannel(
            final double frequency,
            @NotNull final Equipment equipment
    ) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("A valid equipment profile must be supplied");
        }
        final int id = idCounter++;

        final Channel newChannel = new Channel(id, frequency, equipment);
        channels.add(newChannel);
        analyser.addChannel(newChannel);

        return id;
    }

    // Update a channels frequency
    final Channel updateChannel(final int id, final double frequency) throws InvalidFrequencyException {
        final Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate == null) {
            return null;
        }
        channelToUpdate.setFreq(frequency);
        analyser.updateChannel(channelToUpdate);

        return channelToUpdate;
    }

    // Update a channels name
    final Channel updateChannel(final int id, @NotNull final String name) {
        final Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate == null) {
            return null;
        }
        channelToUpdate.setName(name);
        return channelToUpdate;
    }

    // Update a channels equipment
    final Channel updateChannel(
            final int id,
            @NotNull final Equipment equipment
    ) throws InvalidFrequencyException {
        final Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate == null) {
            return null;
        }
        channelToUpdate.setEquipment(equipment);
        analyser.updateChannel(channelToUpdate);

        return channelToUpdate;
    }

    // Remove channel from coordination, returns null if id is not found
    final Channel removeChannel(final int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        Channel removedChannel = channels.remove(index);
        analyser.removeChannel(removedChannel);

        return removedChannel;
    }

    // Method to check a channel before addition
    final NewChannelReport checkChannel(
            final double frequency,
            @NotNull final Equipment profile
    ) throws InvalidFrequencyException {

        // Check for duplicates
        boolean isDuplicate = false;
        for (Channel channel : channels) {
            if (channel.getFreq() == Channel.mHzToKHz(frequency)) {
                isDuplicate = true;
                break;
            }
        }

        // Create report
        Channel channelToCheck = new Channel(null, frequency, profile);
        final int numConflicts = analyser.checkArtifacts(channelToCheck);
        NewChannelReport report = new NewChannelReport(
                numConflicts,
                channelToCheck.getValidity(),
                isDuplicate);

        return report;
    }


    // Get channel index from id, returns -1 if not found
    private int getChannelIndex(int id) {
        final int numChannels = channels.size();
        for (int index = 0; index < numChannels; index++) {
            if (channels.get(index).getId() == id) {
                return index;
            }
        }
        return -1;
    }

    // Get channel by id
    final Channel getChannelById(int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        return channels.get(index);
    }

    final Channel[] getChannels() {
        final Channel[] channelArray = new Channel[channels.size()];
        return channels.toArray(channelArray);
    }

    // Get number of channels
    public int getNumChannels() {
        return channels.size();
    }

    // Get number of intermods
    public int getNumIntermods() {
        return analyser.getIntermodList().size();
    }

    // Get number of conflicts
    public int getNumConflicts() {
        return analyser.getConflictList().size();
    }

    // Get number of conflicts by type
    public int getNumConflictsOfType(Conflict.Type type) {
        int count = 0;
        for(Conflict conflict : analyser.getConflictList()) {
            if (conflict.getType() == type) {
                count++;
            }
        }
        return count;
    }

    final boolean getCalculate2t3o() {
        return analyser.getCalculations().getIM2t3o();
    }

    final void setCalculate2t3o(final boolean calculate2t3o) {
        analyser.getCalculations().setIM2t3o(calculate2t3o);
    }

    final boolean getCalculate2t5o() {
        return analyser.getCalculations().getIM2t5o();
    }

    final void setCalculate2t5o(final boolean calculate2t5o) {
        analyser.getCalculations().setIM2t5o(calculate2t5o);
    }

    final boolean getCalculate2t7o() {
        return analyser.getCalculations().getIM2t7o();
    }

    final void setCalculate2t7o(final boolean calculate2t7o) {
        analyser.getCalculations().setIM2t7o(calculate2t7o);
    }

    final boolean getCalculate2t9o() {
        return analyser.getCalculations().getIM2t9o();
    }

    final void setCalculate2t9o(final boolean calculate2t9o) {
        analyser.getCalculations().setIM2t9o(calculate2t9o);
    }

    final boolean getCalculate3t3o() {
        return analyser.getCalculations().getIM3t3o();
    }

    final void setCalculate3t3o(final boolean calculate3t3o) {
        analyser.getCalculations().setIM3t3o(calculate3t3o);
    }

    final Analyser getAnalyser() {
        return analyser;
    }
}
