package com.stevebunting.rfcoordinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
     * @param channels a list of channels to compare new channel against
     * @param newChannel channel to generate intermods against
     * @param calculate object to specify which intermodulations will be
     *                  calculated
     * @return sorted list of intermodulations generated between newChannel and
     * all other channels in channels list
     */
    static List<Intermod> calculateIntermods(final List<Channel> channels, final Channel newChannel, final Analyser.Calculate calculate) {
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
     * Method to merge a new list of intermods into an existing list.
     *
     * @param mainList list of intermods to merge into
     * @param newList list of intermods to insert
     */
    static void mergeIntermods(final List<Intermod> mainList, final List<Intermod> newList) {
        mainList.addAll(newList);
        Collections.sort(mainList);
    }

    /**
     * Method to remove all intermodulations from a list that are contributed
     * by a specific channel.
     *
     * @param channel channel object to test intermodulation list against
     * @param intermods a list of intermodulations to be operated on
     */
    static void removeIntermods(final Channel channel, List<Intermod> intermods) {
        intermods.removeIf(intermod -> intermod.getF1() == channel
                || intermod.getF2() == channel
                || intermod.getF3() == channel);
    }

    // Function to test one channel against an array (channel must be in array)
    static List<Conflict> analyseChannelSpacing(final int newChannelIndex, final List<Channel> channels) {
        ArrayList<Conflict> newConflicts = new ArrayList<>();

        Channel channelUnderTest = channels.get(newChannelIndex);

        for (Channel channel : channels) {
            int difference = Math.abs(channel.getFreq() - channelUnderTest.getFreq());
            int channelSpacing = channel.getEquipment().getChannelSpacing();
            int newChannelSpacing = channelUnderTest.getEquipment().getChannelSpacing();
            if (channel != channelUnderTest) {
                if (difference < channelSpacing) {
                    newConflicts.add(new Conflict(channel, channelUnderTest));
                }
                if (difference < newChannelSpacing) {
                    newConflicts.add(new Conflict(channelUnderTest, channel));
                }
            }
        }
        return newConflicts;
    }

//    static List<Conflict> analyseIMSpacing(Channel newChannel, List<Intermod> intermods) {
//        ArrayList<Conflict> newConflicts = new ArrayList<>();
//        int loRange = newChannel.getFreq() - newChannel.getEquipment().getMaxImSpacing();
//        int hiRange = newChannel.getFreq() + newChannel.getEquipment().getMaxImSpacing();
//        int startPoint = findClosestIM(loRange, hiRange, intermods);
//        System.out.println(startPoint);
//        return newConflicts;
//    }

//    static int findClosestIM(int lo, int hi, List<Intermod> intermods) {
//        return findClosestIM(lo, hi, intermods, 0, intermods.size());
//    }

//    static int findClosestIM(int lo, int hi, List<Intermod> intermods, int start, int end) {
//        int midIndex = start + ((end - start) / 2);
//        Intermod midIntermod = intermods.get(midIndex);
//        if (midIntermod.getFreq() > lo && midIntermod.getFreq() < hi) {
//            return midIndex;
//        }
//
//        if (midIntermod.getFreq() >= hi) {
//            return findClosestIM(lo, hi, intermods, start, midIndex - 1);
//        } else if (midIntermod.getFreq() <= lo) {
//            return findClosestIM(lo, hi, intermods, midIndex + 1, end);
//        }
//    }

    // Function to analyse new intermods
    static List<Conflict> analyseBackup(int newChannelIndex,
                           List<Channel> channels,
                           List<Intermod> intermodList,
                           List<Intermod> allIntermods) {
        ArrayList<Conflict> newConflicts = new ArrayList<>();

        final int firstIndexToFind = channels.size();
        int nextChannelPointer = 0;
        int nextImPointer = 0;

        for (int i = 0; i < firstIndexToFind; i++) {
            final Channel currentChannel = channels.get(i);
            boolean nextChannelPointerSet = false;
            boolean nextImPointerSet = false;

            // Set next frequency in list, if currentFreq is the last frequency, set to null
            // Use to set nextImPointer for iterating over intermods
            final Channel nextChannel = i < firstIndexToFind - 1
                    ? channels.get(i + 1)
                    : null;

            // Iterate over frequencies
            if (i == newChannelIndex) {
                for (int pointer = nextChannelPointer; pointer < firstIndexToFind; pointer++) {
                    final Channel pointerChannel = channels.get(pointer);
                    final int pointerFrequency = pointerChannel.getFreq();

                    // Set point to start iterating over channels for nextFreq
                    // Must be first and not last channel
                    // Next frequency must be less than allowable channel spacing
                    final int channelFrequency = currentChannel.getFreq();
                    final int channelSpacing = currentChannel.getEquipment().getChannelSpacing();
                    if (!nextChannelPointerSet && nextChannel != null && (nextChannel.getFreq() - pointerChannel.getFreq()) < nextChannel.getEquipment().getChannelSpacing()) {
                        nextChannelPointer = pointer;
                        nextChannelPointerSet = true;
                    }

                    // Add conflict if not pointing at current channel and pointer channel within channel spacing
                    if (currentChannel != pointerChannel && Math.abs(channelFrequency - pointerFrequency) < channelSpacing) {
                        newConflicts.add(new Conflict(currentChannel, pointerChannel));
                        newConflicts.add(new Conflict(pointerChannel, currentChannel));
                    }

                    // Exit loop if pointer higher than channels spacing range
                    if (pointerFrequency >= channelFrequency + channelSpacing) {
                        break;
                    }
                }

                for (Intermod intermod : allIntermods) {
                    if (intermod.getFreq() >= currentChannel.getFreq() - currentChannel.getEquipment().getMaxImSpacing()) {
                        analyseIM(currentChannel, intermod, newConflicts);
                    }
                    if (intermod.getFreq() >= currentChannel.getFreq() + currentChannel.getEquipment().getMaxImSpacing()) {
                        break;
                    }
                }
            } else {

                // Iterate over intermods
                for (int pointer = nextImPointer; pointer < intermodList.size(); pointer++) {
                    Intermod currentIntermod = intermodList.get(pointer);

                    // Set point to start iterating over intermods for nextFreq
                    if (!nextImPointerSet && nextChannel != null && (nextChannel.getFreq() - currentIntermod.getFreq()) < nextChannel.getEquipment().getMaxImSpacing()) {
                        nextImPointer = pointer;
                        nextImPointerSet = true;
                    }

                    analyseIM(currentChannel, currentIntermod, newConflicts);

                    // If current intermodulation is out of range of the highest IM spacing, move on to next frequency
                    if (currentIntermod.getFreq() >= currentChannel.getFreq() + currentChannel.getEquipment().getMaxImSpacing()) {
                        if (!nextImPointerSet) {
                            nextImPointer = pointer;
                        }
                        break;
                    }
                }
            }
        }
        return newConflicts;
    }

    // Compare channel to intermod and create conflict if necessary
    private static void analyseIM(final Channel channel, final Intermod currentIntermod, List<Conflict> newConflicts) {
        int spacing = Math.abs(channel.getFreq() - currentIntermod.getFreq());

        // If current intermodulation not created by current frequency, check spacing
        if (currentIntermod.getF1() != channel && currentIntermod.getF2() != channel && currentIntermod.getF3() != channel) {
            int requiredSpacing;
            switch (currentIntermod.getType()) {
                case IM_2T3O:
                    requiredSpacing = channel.getEquipment().get2t3oSpacing();
                    break;

                case IM_2T5O:
                    requiredSpacing = channel.getEquipment().get2t5oSpacing();
                    break;

                case IM_2T7O:
                    requiredSpacing = channel.getEquipment().get2t7oSpacing();
                    break;

                case IM_2T9O:
                    requiredSpacing = channel.getEquipment().get2t9oSpacing();
                    break;

                case IM_3T3O:
                default:
                    requiredSpacing = channel.getEquipment().get3t3oSpacing();
                    break;
            }
            if (spacing < requiredSpacing) {
                newConflicts.add(new Conflict(channel, currentIntermod));
            }
        }
    }

    // Remove all conflicts contributed by a specific channel
    static void removeConflicts(Channel channel, List<Conflict> conflicts) {
        conflicts.removeIf(conflict -> {
            if (conflict.getChannel() == channel
                    || conflict.getConflictChannel() == channel
                    || (conflict.getConflictIntermod() != null && conflict.getConflictIntermod().getF1() == channel)
                    || (conflict.getConflictIntermod() != null && conflict.getConflictIntermod().getF2() == channel)
                    || (conflict.getConflictIntermod() != null && conflict.getConflictIntermod().getF3() == channel)) {
                conflict.getChannel().removeConflict(conflict);
                return true;
            }
            return false;
        });
    }

    static List<Conflict> analyse(int newChannelIndex,
                                  List<Channel> channels,
                                  List<Intermod> intermodList,
                                  List<Intermod> allIntermods) {
        ArrayList<Conflict> newConflicts = new ArrayList<>();
        return newConflicts;
    }
}
