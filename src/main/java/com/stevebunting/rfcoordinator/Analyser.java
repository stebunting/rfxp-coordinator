package com.stevebunting.rfcoordinator;

import com.sun.istack.internal.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * The Analyser class provides a series of methods that allow an RF analysis
 * to be performed.
 *
 * A channel list and intermod list must be maintained.
 *
 * PROCEDURE:
 * When a new channel is added, calculateIntermods() should be called with
 * the new channel passed. The return value from this should be merged with
 * the intermod list using the mergeLists() method.
 */
public class Analyser {
    /**
     * Class to define which intermodulations are required for the
     * coordination. If an intermodulation is not required, it should not be
     * calculated to optimise performance.
     */
    static class Calculate {
        boolean im2t3o = true;
        boolean im2t5o = true;
        boolean im2t7o = true;
        boolean im2t9o = true;
        boolean im3t3o = true;
    }

    /**
     * Function to calculate all intermodulations between a single channel
     * and a list of channels. The new channel may be included in the list
     * of channels.
     *
     * @param channels   a list of channels to compare new channel against
     * @param newChannel channel to generate intermods against
     * @param calculate  object to specify which intermodulations will be
     *                   calculated
     * @return sorted list of intermodulations generated between newChannel and
     * all other channels in channels list
     */
    static List<Intermod> calculateIntermods(
            @NotNull final List<Channel> channels,
            @NotNull final Channel newChannel,
            @NotNull final Analyser.Calculate calculate
    ) throws IllegalArgumentException {
        if (channels == null || newChannel == null || calculate == null) {
            throw new IllegalArgumentException();
        }

        final ArrayList<Intermod> newIntermods = new ArrayList<>();
        final int numChannels = channels.size();

        for (int i = 0; i < numChannels; i++) {
            Channel channel2 = channels.get(i);

            if (newChannel != channel2) {
                if (calculate.im2t3o) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T3O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T3O, channel2, newChannel, null));
                }
                if (calculate.im2t5o) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T5O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T5O, channel2, newChannel, null));
                }
                if (calculate.im2t7o) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T7O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T7O, channel2, newChannel, null));
                }
                if (calculate.im2t9o) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T9O, newChannel, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T9O, channel2, newChannel, null));
                }
                if (calculate.im3t3o) {
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
     * Method to merge a 2 lists into a new sorted list.
     *
     * @param a first list
     * @param b second list
     */
    static <T extends Comparable<T>> List<T> mergeLists(
            @NotNull final List<T> a,
            @NotNull final List<T> b
    ) throws IllegalArgumentException {
        if (a == null || b == null) {
            throw new IllegalArgumentException();
        }

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
     * Method to remove all intermodulations from a list that are contributed
     * by a specific channel.
     *
     * @param channel   channel object to test intermodulation list against
     * @param intermods a list of intermodulations to be operated on
     */
    static void removeIntermods(
            final Channel channel,
            @NotNull List<Intermod> intermods
    ) throws IllegalArgumentException {
        if (intermods == null) {
            throw new IllegalArgumentException();
        }
        intermods.removeIf(intermod -> intermod.getF1() == channel
                || intermod.getF2() == channel
                || intermod.getF3() == channel);
    }

    /**
     * Method to calculate channel conflicts between 2 channels and add them
     * to a list of conflicts.
     *
     * @param conflicts list of conflicts to add to
     * @param channel1  first channel to test
     * @param channel2  second channel to test
     */
    static void getConflictsTwoChannels(
            @NotNull final List<Conflict> conflicts,
            @NotNull final Channel channel1,
            @NotNull final Channel channel2
    ) throws IllegalArgumentException {
        if (conflicts == null || channel1 == null || channel2 == null) {
            throw new IllegalArgumentException();
        }

        int difference = Math.abs(channel1.getFreq() - channel2.getFreq());
        if (channel1 != channel2) {
            if (difference < channel1.getEquipment().getChannelSpacing()) {
                conflicts.add(new Conflict(channel1, channel2));
            }
            if (difference < channel2.getEquipment().getChannelSpacing()) {
                conflicts.add(new Conflict(channel2, channel1));
            }
        }
    }

    /**
     * Method to test one channel against a list of channels. The channel may
     * be in the list.
     *
     * @param channels   list of channels to compare against
     * @param newChannel channel to compare
     * @return list of conflicts
     */
    static List<Conflict> analyseChannelSpacing(
            @NotNull final List<Channel> channels,
            @NotNull final Channel newChannel
    ) throws IllegalArgumentException {
        if (channels == null || newChannel == null) {
            throw new IllegalArgumentException();
        }

        ArrayList<Conflict> conflicts = new ArrayList<>();
        for (Channel channel : channels) {
            Analyser.getConflictsTwoChannels(conflicts, channel, newChannel);
        }
        return conflicts;
    }

    /**
     * Method to calculate conflicts generated between a single channel and
     * a single intermod.
     *
     * @param channel channel to test
     * @param intermod intermod to test against
     */
    static Conflict getConflictsChannelAndIM(
        @NotNull final Channel channel,
        @NotNull final Intermod intermod
    ) throws IllegalArgumentException {
        if (channel == null || intermod == null) {
            throw new IllegalArgumentException();
        }

        if (intermod.getF1() == channel || intermod.getF2() == channel || intermod.getF3() == channel) {
            return null;
        }

        int difference = Math.abs(channel.getFreq() - intermod.getFreq());
        Integer maxSpacing;
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

        if (maxSpacing != null && maxSpacing > difference) {
            return new Conflict(channel, intermod);
        }
        return null;
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
    private static int getNextImIndex(int limitLo, List<Intermod> intermods, int start, int end) {
        if (start > end) {
            return start;
        }

        int mid = start + ((end - start) / 2);
        return intermods.get(mid).getFreq() <= limitLo
            ? getNextImIndex(limitLo, intermods, mid + 1, end)
            : getNextImIndex(limitLo, intermods, start, mid - 1);
    }

    /**
     * Method to initiate getNextImIndex binary search
     *
     * @param limitLo limit of frequencies, frequency to find must be first
     *                intermod with a frequency higher than this
     * @param intermods list of intermods to search in
     * @return found index
     */
    static int getNextImIndex(int limitLo, List<Intermod> intermods) {
        return getNextImIndex(limitLo, intermods, 0, intermods.size() - 1);
    }

    /**
     * Method to find all channel/intermod conflicts given a list of channels
     * and a list of intermods.
     *
     * @param channels list of channels to test
     * @param intermods list of intermodulations to test
     * @return list of conflicts generated
     */
    static List<Conflict> analyseIMSpacing(
        @NotNull final List<Channel> channels,
        @NotNull final List<Intermod> intermods
    ) {
        // TODO: Test return if channel list empty or null
        List<Conflict> conflicts = new ArrayList<>();
        if (channels.size() == 0 || intermods.size() == 0) {
            return conflicts;
        }

        // Loop over channels
        for (int i = 0; i < channels.size(); i++) {
            // Channel to test
            Channel channel = channels.get(i);
            int rangeLo = channel.getFreq() - channel.getEquipment().getMaxImSpacing();
            int rangeHi = channel.getFreq() + channel.getEquipment().getMaxImSpacing();

            // Starting index in intermod list
            int imIndex = Analyser.getNextImIndex(rangeLo, intermods);

            // Loop over intermods until out of upper danger range of channel
            while (imIndex < intermods.size() && intermods.get(imIndex).getFreq() < rangeHi) {
                Conflict newConflict = Analyser.getConflictsChannelAndIM(channel, intermods.get(imIndex));
                if (newConflict != null) {
                    conflicts.add(newConflict);
                }
                imIndex++;
            }
        }
        return conflicts;
    }

    /**
     * Method to find all channel/intermod conflicts given a single channel
     * and a list of intermods.
     *
     * @param channel channel to test
     * @param intermods list of intermodulations to test
     * @return list of conflicts generated
     */
    static List<Conflict> analyseIMSpacing(
            @NotNull final Channel channel,
            @NotNull final List<Intermod> intermods
    ) {
        return Analyser.analyseIMSpacing(
            new ArrayList<>(Collections.singletonList(channel)),
            intermods
        );
    }
}
