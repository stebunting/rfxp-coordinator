package com.stevebunting.rfcoordinator;

import org.jetbrains.annotations.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Analyser class stores lists of channels, intermods and conflicts
 * from a coordination and performs analysis on them.
 */
final class Analyser {
    // ArrayList to hold sorted list of channels
    final private List<Channel> channels = new ArrayList<>();

    // ArrayList to hold sorted list of all intermodulations
    private List<Intermod> intermods = new ArrayList<>();

    // ArrayList to hold list of all conflicts
    final private List<Conflict> conflicts = new ArrayList<>();

    // Intermodulation Calculations to make
    final private AnalyserCalculations calculations = new AnalyserCalculations();

    /**
     * Method to add a new channel to the analysis
     *
     * @param channel channel to add
     * @throws IllegalArgumentException on null channel passed
     */
    final void addChannel(@NotNull final Channel channel) throws IllegalArgumentException {
        if (channel == null) {
            throw new IllegalArgumentException();
        }
        channels.add(channel);

        // Calculate new intermods
        List<Intermod> newIntermods = calculateIntermods(channel);

        // Generate intermod conflicts and merge intermods into list
        getIMConflicts(channel, intermods, conflicts, true);
        getIMConflicts(channels, newIntermods, conflicts, true);
        intermods = mergeLists(intermods, newIntermods);

        // Generate channel conflicts
        getChannelConflicts(channel, conflicts, true, true);
    }

    /**
     * Method to remove a channel from the analysis
     *
     * @param channel channel to remove
     * @throws IllegalArgumentException on null channel passed
     * @return boolean representing success or failure of operation
     */
    final boolean removeChannel(@NotNull final Channel channel) throws IllegalArgumentException {
        if (channel == null) {
            throw new IllegalArgumentException();
        }

        final boolean channelRemoved = channels.remove(channel);
        if (channelRemoved) {
            removeConflicts(channel);
            removeIntermods(channel);
        }
        return channelRemoved;
    }

    /**
     * Method to update a channel in the analysis by removing and re-adding,
     * triggering a recalculation of intermods and conflicts.
     *
     * @param channel channel to update
     * @throws IllegalArgumentException on null channel passed
     * @return boolean representing success or failure of operation
     */
    final boolean updateChannel(@NotNull final Channel channel) throws IllegalArgumentException {
        if (channel == null) {
            throw new IllegalArgumentException();
        }

        final boolean channelRemoved = removeChannel(channel);
        if (channelRemoved) {
            addChannel(channel);
        }
        return channelRemoved;
    }

    /**
     * Method to generate artifacts from a new channel but does not merge them
     * into the state.
     *
     * @param channel channel to test
     * @return number of conflicts generated
     * @throws IllegalArgumentException on null channel
     */
    final int checkArtifacts(@NotNull final Channel channel) throws IllegalArgumentException {
        if (channel == null) {
            throw new IllegalArgumentException();
        }

        // Calculate new intermods
        List<Intermod> newIntermods = calculateIntermods(channel);

        // Generate conflicts and add to a local list
        List<Conflict> newConflicts = new ArrayList<>();
        getIMConflicts(channel, intermods, newConflicts, true);
        getIMConflicts(channels, newIntermods, newConflicts, false);
        getChannelConflicts(channel, newConflicts, true, false);

        return newConflicts.size();
    }

    /**
     * Method to calculate all intermodulations between a single channel
     * and a list of channels. The new channel may be included in the list
     * of channels.
     *
     * @param newChannel channel to generate intermods against
     * @return sorted list of intermodulations generated between newChannel and
     * all other channels in channels list
     */
    private List<Intermod> calculateIntermods(@NotNull final Channel newChannel) {
        final ArrayList<Intermod> newIntermods = new ArrayList<>();
        final int numChannels = channels.size();

        for (int i = 0; i < numChannels; i++) {
            Channel channel2 = channels.get(i);

            if (newChannel != channel2) {
                if (calculations.getIM2t3o()) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T3O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T3O, channel2, newChannel, null));
                }
                if (calculations.getIM2t5o()) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T5O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T5O, channel2, newChannel, null));
                }
                if (calculations.getIM2t7o()) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T7O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T7O, channel2, newChannel, null));
                }
                if (calculations.getIM2t9o()) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T9O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T9O, channel2, newChannel, null));
                }
                if (calculations.getIM3t3o()) {
                    for (int j = i + 1; j < numChannels; j++) {
                        Channel channel3 = channels.get(j);

                        if (newChannel != channel3) {
                            newIntermods.add(new Intermod(Intermod.Type.IM_3T3O, newChannel, channel2, channel3));
                            newIntermods.add(new Intermod(Intermod.Type.IM_3T3O, channel2, channel3, newChannel));
                            newIntermods.add(new Intermod(Intermod.Type.IM_3T3O, channel3, newChannel, channel2));
                        }
                    }
                }
            }
        }
        Collections.sort(newIntermods);
        return newIntermods;
    }

    /**
     * Method to merge 2 lists into a new sorted list.
     *
     * @param a first list
     * @param b second list
     */
    private <T extends Comparable<T>> List<T> mergeLists(
            @NotNull final List<T> a,
            @NotNull final List<T> b
    ) {
        List<T> mergedList = new ArrayList<>();
        int indexA = 0;
        int indexB = 0;
        while (indexA < a.size() || indexB < b.size()) {
            if (indexA == a.size()) {
                mergedList.add(b.get(indexB));
                indexB++;
            } else if (indexB == b.size()) {
                mergedList.add(a.get(indexA));
                indexA++;
            } else if (a.get(indexA).compareTo(b.get(indexB)) < 0) {
                mergedList.add(a.get(indexA));
                indexA++;
            } else {
                mergedList.add(b.get(indexB));
                indexB++;
            }
        }
        return mergedList;
    }

    /**
     * Method to find all channel/intermod conflicts from a list of channels
     * and a list of intermods and add them to a list of conflicts. Adds
     * conflicts to conflicts list and to relevant channel if flag is set.
     *
     * @param channels list of channels to test
     * @param intermods list of intermodulations to test
     * @param conflicts list to add generated conflicts to
     * @param addConflictToChannel add conflict reference to channel if true
     */
    private void getIMConflicts(
            @NotNull final List<Channel> channels,
            @NotNull final List<Intermod> intermods,
            @NotNull final List<Conflict> conflicts,
            final boolean addConflictToChannel
    ) {
        if (channels.size() == 0 || intermods.size() == 0) {
            return;
        }
        for (Channel channel : channels) {
            getIMConflicts(channel, intermods, conflicts, addConflictToChannel);
        }
    }

    /**
     * Method to find all intermod conflicts with a new channel and an existing
     * list of intermods and add them to a list of conflicts. Adds conflicts to
     * conflicts list and to relevant channel if flag is set.
     *
     * @param channel channel to test
     * @param intermods list of intermodulations to test
     * @param conflicts list to add generated conflicts to
     * @param addConflictToChannel add conflict reference to channel if true
     */
    private void getIMConflicts(
            @NotNull final Channel channel,
            @NotNull final List<Intermod> intermods,
            @NotNull final List<Conflict> conflicts,
            final boolean addConflictToChannel
    ) {
        final int rangeLo = channel.getFreq() - channel.getEquipment().getMaxImSpacing();
        final int rangeHi = channel.getFreq() + channel.getEquipment().getMaxImSpacing();

        // Starting index in intermod list
        int imIndex = getNextImIndex(rangeLo, intermods);

        // Loop over intermods until out of upper danger range of channel
        while (imIndex < intermods.size() && intermods.get(imIndex).getFreq() < rangeHi) {
            getChannelIMConflicts(channel, intermods.get(imIndex), conflicts, addConflictToChannel);
            imIndex++;
        }
    }

    /**
     * Method to calculate conflicts generated between a single channel and
     * a single intermod and add them to a list of conflicts. Adds conflicts
     * to conflicts list and to relevant channel if flag is set.
     *
     * @param channel channel to test
     * @param intermod intermod to test against
     * @param conflicts list to add generated conflicts to
     * @param addConflictToChannel add conflict reference to channel if true
     */
    private void getChannelIMConflicts(
            @NotNull final Channel channel,
            @NotNull final Intermod intermod,
            @NotNull final List<Conflict> conflicts,
            final boolean addConflictToChannel
    ) {
        if (intermod.getF1() == channel || intermod.getF2() == channel || intermod.getF3() == channel) {
            return;
        }

        final Integer maxSpacing;
        switch (intermod.getType()) {
            case IM_2T3O:
                maxSpacing = channel.getEquipment().get2t3oSpacing();
                break;

            case IM_2T5O:
                maxSpacing = channel.getEquipment().get2t5oSpacing();
                break;

            case IM_2T7O:
                maxSpacing = channel.getEquipment().get2t7oSpacing();
                break;

            case IM_2T9O:
                maxSpacing = channel.getEquipment().get2t9oSpacing();
                break;

            case IM_3T3O:
                maxSpacing = channel.getEquipment().get3t3oSpacing();
                break;

            default:
                maxSpacing = null;
                break;
        }

        final int difference = Math.abs(channel.getFreq() - intermod.getFreq());
        if (maxSpacing != null && maxSpacing > difference) {
            Conflict newConflict = new Conflict(channel, intermod);
            conflicts.add(newConflict);
            if (addConflictToChannel) {
                channel.addConflict(newConflict);
            }
        }
    }

    /**
     * Method to test one channel against a list of channels. The channel may
     * or may not be in the list. Adds conflicts to conflicts list and to
     * relevant channel if flag is set.
     *
     * @param newChannel channel to compare
     * @param conflicts list to add generated conflicts to
     * @param addConflictToNewChannel add conflict reference to channel if true
     * @param addConflictToListChannel add conflict reference to channel in
     *                                 channel list if true
     */
    private void getChannelConflicts(
            @NotNull final Channel newChannel,
            @NotNull final List<Conflict> conflicts,
            final boolean addConflictToNewChannel,
            final boolean addConflictToListChannel
    ) {
        for (Channel channel : channels) {
            final int difference = Math.abs(channel.getFreq() - newChannel.getFreq());
            if (channel != newChannel) {
                if (difference < channel.getEquipment().getChannelSpacing()) {
                    final Conflict newConflict = new Conflict(channel, newChannel);
                    conflicts.add(newConflict);
                    if (addConflictToNewChannel) {
                        channel.addConflict(newConflict);
                    }
                }
                if (difference < newChannel.getEquipment().getChannelSpacing()) {
                    final Conflict newConflict = new Conflict(newChannel, channel);
                    conflicts.add(newConflict);
                    if (addConflictToListChannel) {
                        newChannel.addConflict(newConflict);
                    }
                }
            }
        }
    }

    /**
     * Method to remove all intermodulations from a list that are contributed
     * by a specific channel.
     *
     * @param channel   channel object to test intermodulation list against
     */
    private void removeIntermods(@NotNull final Channel channel) {
        intermods.removeIf((Intermod intermod) ->
                intermod.getF1() == channel
             || intermod.getF2() == channel
             || intermod.getF3() == channel);
    }

    /**
     * Method to remove all conflicts from a list that are contributed
     * by a specific channel.
     *
     * @param channel   channel object to test conflict list against
     */
    private void removeConflicts(@NotNull final Channel channel) {
        conflicts.removeIf((Conflict conflict) -> {
            if (conflict.getChannel() == channel) {
                channel.removeConflict(conflict);
                return true;
            }
            switch (conflict.getType()) {
                case CHANNEL_SPACING:
                    if (conflict.getConflictChannel() == channel) {
                        conflict.getChannel().removeConflict(conflict);
                        return true;
                    }
                    return false;

                case INTERMOD_SPACING:
                    if (conflict.getConflictIntermod().getF1() == channel
                     || conflict.getConflictIntermod().getF2() == channel
                     || conflict.getConflictIntermod().getF3() == channel) {
                        conflict.getChannel().removeConflict(conflict);
                        return true;
                    }
                    return false;

                default:
                    return false;
            }
        });
    }

    /**
     * Method to search recursively for first index to check in a list of
     * intermodulations. Intermod must be first frequency in list higher than
     * limitLo. Implements binary search methodology.
     *
     * @param limitLo limit of frequencies, frequency to find must be first
     *                intermod with a frequency higher than this
     * @param intermods list of intermods to search in
     * @param start starting index
     * @param end end index
     * @return found index
     */
    private int getNextImIndex(
            final int limitLo,
            @NotNull final List<Intermod> intermods,
            final int start,
            final int end
    ) {
        if (start > end) {
            return start;
        }

        final int mid = start + ((end - start) / 2);
        if (intermods.get(mid).getFreq() <= limitLo) {
            return mid + 1 < intermods.size() && intermods.get(mid + 1).getFreq() > limitLo
                ? mid + 1
                : getNextImIndex(limitLo, intermods, mid + 1, end);
        } else {
            return mid > 0 && intermods.get(mid - 1).getFreq() <= limitLo
                ? mid
                : getNextImIndex(limitLo, intermods, start, mid - 1);
        }
    }

    /**
     * Method to initiate getNextImIndex binary search
     *
     * @param limitLo limit of frequencies, frequency to find must be first
     *                intermod with a frequency higher than this
     * @param intermods list of intermods to search in
     * @return found index
     */
    private int getNextImIndex(final int limitLo, final List<Intermod> intermods) {
        return getNextImIndex(limitLo, intermods, 0, intermods.size() - 1);
    }

    final List<Channel> getChannelList() {
        return channels;
    }

    final List<Intermod> getIntermodList() {
        return intermods;
    }

    final List<Conflict> getConflictList() {
        return conflicts;
    }

    final AnalyserCalculations getCalculations() {
        return calculations;
    }
}
