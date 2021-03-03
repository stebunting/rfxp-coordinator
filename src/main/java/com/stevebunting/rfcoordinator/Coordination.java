package com.stevebunting.rfcoordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Comparator;

/**
 * Coordination is the main interaction point. It offers methods to add,
 * remove, update and check channels in the coordination.
 */
final class Coordination {

    // Counter to assign id's to channels
    private int idCounter = 0;

    // ArrayList to hold list of channels with id key
    final private ArrayList<Channel> channels = new ArrayList<>();

    // Analyser class
    final private Analyser analyser = new Analyser();

    // Current editing channel
    private Channel editChannelBackup = null;

    enum SortBy { ID, FREQUENCY, NAME }
    private Comparator<Channel> sortBy = new ChannelIDComparator();

    /**
     * Declare channel that is to be edited.
     *
     * @param id ID of channel to edit
     */
    final void startEditingChannel(final int id) {
        final Channel channel = getChannelById(id);
        editChannelBackup = channel.deepCopy();
    }

    /**
     * Stop editing a channel. Resets instance variable to null.
     */
    final void stopEditingChannel() {
        editChannelBackup = null;
    }

    /**
     * Update the current editing channels name.
     *
     * @param name name to update with
     */
    final void editChannel(@NotNull final String name) {
        if (editChannelBackup == null) {
            return;
        }
        updateChannel(editChannelBackup.getId(), name);
    }

    /**
     * Update the current editing channels frequency.
     *
     * @param frequency frequency to update channel with
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
    final void editChannel(final double frequency) throws InvalidFrequencyException {
        if (editChannelBackup == null) {
            return;
        }
        updateChannel(editChannelBackup.getId(), frequency);
    }

    /**
     * Update the current editing channels equipment.
     *
     * @param equipment equipment to update channel with
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
    final void editChannel(@NotNull final Equipment equipment) throws InvalidFrequencyException {
        if (editChannelBackup == null) {
            return;
        }
        updateChannel(editChannelBackup.getId(), equipment);
    }

    /**
     * Restores the channel that is being edited to the state it was in before
     * editing started.
     *
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
    final void restoreEditingChannel() throws InvalidFrequencyException {
        if (editChannelBackup == null) {
            return;
        }
        Channel channelToRestore = getChannelById(editChannelBackup.getId());
        if (channelToRestore == null) {
            return;
        }
        channelToRestore.setName(editChannelBackup.getName());
        channelToRestore.setFreqAndEquipment(editChannelBackup.getFreq(), editChannelBackup.getEquipment());
        analyser.updateChannel(channelToRestore);
    }

    /**
     * Add a new channel to the coordination.
     *
     * @param frequency new channel frequency in MHz
     * @param equipment equipment type
     * @return ID of the new channel
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
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

    /**
     * Update a channels frequency.
     *
     * @param id ID of channel to update
     * @param frequency new channel frequency in MHz
     * @return the channel object
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
    final Channel updateChannel(final int id, final double frequency) throws InvalidFrequencyException {
        final Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate == null) {
            return null;
        }
        channelToUpdate.setFreq(frequency);
        analyser.updateChannel(channelToUpdate);

        return channelToUpdate;
    }

    /**
     * Update a channels name.
     *
     * @param id ID of channel to update
     * @param name new channel name
     * @return the channel object
     */
    final Channel updateChannel(final int id, @NotNull final String name) {
        final Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate == null) {
            return null;
        }
        channelToUpdate.setName(name);
        return channelToUpdate;
    }

    /**
     * Update a channels equipment.
     *
     * @param id ID of channel to update
     * @param equipment new channel equipment type
     * @return the channel object
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
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

    /**
     * Remove a channel from the coordination.
     *
     * @param id ID of channel to remove
     * @return the removed channel or null if channel is not found
     */
    final Channel removeChannel(final int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        Channel removedChannel = channels.remove(index);
        analyser.removeChannel(removedChannel);

        return removedChannel;
    }

    /**
     * Check the impact a new channel will have on the coordination.
     *
     * @param frequency frequency of channel to check in MHz
     * @param profile equipment type of channel to check
     * @return NewChannelReport
     * @throws InvalidFrequencyException on invalid frequency / equipment combination
     */
    final NewChannelReport testChannel(
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
        return new NewChannelReport(
                numConflicts,
                channelToCheck.getValidity(),
                isDuplicate);
    }

    /**
     * Get channels index in channels ArrayList from id.
     *
     * @param id ID of channel to find
     * @return index of channel in channels ArrayList or -1 if not found
     */
    private int getChannelIndex(int id) {
        for (int index = 0; index < channels.size(); index++) {
            if (channels.get(index).getId() == id) {
                return index;
            }
        }
        return -1;
    }

    /**
     * Get channel from channels ArrayList from id.
     *
     * @param id ID of channel to find
     * @return channel requested or null if not found
     */
    final Channel getChannelById(int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        return channels.get(index);
    }

    /**
     * Sort channel list using stored comparator.
     */
    final void sort() {
        channels.sort(sortBy);
    }

    /**
     * Set channel sort order.
     *
     * @param sortBy required sort order
     */
    final void setSortBy(SortBy sortBy) {
        if (sortBy == null) {
            sortBy = SortBy.ID;
        }
        switch (sortBy) {
            case FREQUENCY:
                this.sortBy = new ChannelFrequencyComparator();
                break;

            case NAME:
                this.sortBy = new ChannelNameComparator();
                break;

            default:
            case ID:
                this.sortBy = new ChannelIDComparator();
                break;
        }
    }

    /**
     * Get channel array
     *
     * @return array of channels
     */
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
