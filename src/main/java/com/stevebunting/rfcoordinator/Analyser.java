package com.stevebunting.rfcoordinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class Analyser {

    // Calculate intermodulations for a single channel
    List<Intermod> calculateIntermods(final List<Channel> channels, final Channel channel1, final boolean[] calculations) {
        final ArrayList<Intermod> newIntermods = new ArrayList<>();
        final int numChannels = channels.size();
        for (int i = 0; i < numChannels; i++) {
            Channel channel2 = channels.get(i);

            if (channel1.getId() != channel2.getId()) {
                if (calculations[0]) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T3O, channel2, channel1, null));
                }
                if (calculations[1]) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T5O, channel1, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T5O, channel2, channel1, null));
                }
                if (calculations[2]) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T7O, channel1, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T7O, channel2, channel1, null));
                }
                if (calculations[3]) {
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T9O, channel1, channel2, null));
                    newIntermods.add(new Intermod(Intermod.Type.IM_2T9O, channel2, channel1, null));
                }
                if (calculations[4]) {
                    for (int j = i + 1; j < numChannels; j++) {
                        Channel channel3 = channels.get(j);

                        if (channel1.getId() != channel3.getId()) {
                            newIntermods.add(new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3));
                            newIntermods.add(new Intermod(Intermod.Type.IM_3T3O, channel2, channel3, channel1));
                            newIntermods.add(new Intermod(Intermod.Type.IM_3T3O, channel3, channel1, channel2));
                        }
                    }
                }
            }
        }
        Collections.sort(newIntermods);
        assert Helpers.isSorted(newIntermods);

        return newIntermods;
    }

    // Remove all intermods that are contributed to by a specific channel
    void removeIntermods(Channel channel, List<Intermod> intermods) {
        intermods.removeIf(intermod -> intermod.getF1() == channel
                || intermod.getF2() == channel
                || intermod.getF3() == channel);
    }

    // Remove all conflicts contributed by a specific channel
    void removeConflicts(Channel channel, List<Conflict> conflicts) {
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

    // Function to analyse new intermods
    List<Conflict> analyse(int newChannelIndex,
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
    private void analyseIM(final Channel channel, final Intermod currentIntermod, List<Conflict> newConflicts) {
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
}
