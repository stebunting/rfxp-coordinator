package com.stevebunting.rfcoordinator;

import java.util.ArrayList;
import java.util.Collections;

public class Coordination {

    // Counter to assign id's to channels
    private int idCounter = 0;

    // ArrayList to hold list of channels with id key
    private final ArrayList<Channel> channels = new ArrayList<>();

    // ArrayList to hold list of all intermodulations
    private final ArrayList<Intermod> intermods = new ArrayList<>();

    // ArrayList to hold list of all conflicts
    private final ArrayList<Conflict> conflicts = new ArrayList<>();

    // Intermodulation Calculations to make
    private boolean calculate2t3o = true;
    private boolean calculate2t5o = true;
    private boolean calculate2t7o = true;
    private boolean calculate2t9o = true;
    private boolean calculate3t3o = true;

    // Add new channel to coordination
    public final int addChannel(double frequency, Equipment equipment) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("A valid equipment profile must be supplied");
        }
        final int id = idCounter;

        Channel newChannel = new Channel(id, frequency, equipment);
        addChannelInPlace(newChannel);
        calculateIntermods(newChannel);
        analyse();

        idCounter++;
        return id;
    }

    // Update a channels frequency
    public Channel updateChannel(int id, double frequency) throws InvalidFrequencyException {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        Channel channelToUpdate = channels.get(index);

        // Update channel, throws if invalid
        channelToUpdate.setFreq(frequency);

        // Remove channel and intermodulation products
        removeIntermods(channels.remove(index));

        // Insert Channel in new position
        addChannelInPlace(channelToUpdate);
        calculateIntermods(channelToUpdate);

        return channelToUpdate;
    }

    // Update a channels name
    public Channel updateChannel(int id, String name) {
        Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate != null) {
            channelToUpdate.setName(name);
        }
        return channelToUpdate;
    }

    // Update a channels equipment
    public Channel updateChannel(int id, Equipment equipment) throws InvalidFrequencyException {
        Channel channelToUpdate = getChannelById(id);
        if (channelToUpdate != null) {
            channelToUpdate.setEquipment(equipment);
        }
        return channelToUpdate;
    }

    // Method to add a channel in place
    private void addChannelInPlace(Channel channel) {
        int index;
        for (index = 0; index < channels.size(); index++) {
            if (channels.get(index).getFreq() > channel.getFreq()) {
                break;
            }
        }
        channels.add(index, channel);
        assert Helpers.isSorted(channels);
    }

    // Remove channel from coordination, returns null if id is not found
    public Channel removeChannel(int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        Channel removedChannel = channels.remove(index);

        removeIntermods(removedChannel);
        removeConflicts(removedChannel);

        return removedChannel;
    }

    private void analyse() {
        clearConflicts();

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
                    addConflict(new Conflict(currentChannel, pointerChannel));
                }

                // Exit loop if pointer higher than channels spacing range
                if (pointerFrequency >= channelFrequency + channelSpacing) {
                    break;
                }
            }

            // Iterate over intermods
            for (int pointer = nextImPointer; pointer < intermods.size(); pointer++) {
                Intermod currentIntermod = intermods.get(pointer);
                int spacing = Math.abs(currentChannel.getFreq() - currentIntermod.getFreq());

                // Set point to start iterating over intermods for nextFreq
                if (!nextImPointerSet && nextChannel != null && (nextChannel.getFreq() - currentIntermod.getFreq()) < nextChannel.getEquipment().getMaxImSpacing()) {
                    nextImPointer = pointer;
                    nextImPointerSet = true;
                }

                // If current intermodulation not created by current frequency, check spacing
                if (currentIntermod.getF1() != currentChannel && currentIntermod.getF2() != currentChannel && currentIntermod.getF3() != currentChannel) {
                    int requiredSpacing;
                    switch (currentIntermod.getType()) {
                        case IM_2T3O:
                            requiredSpacing = currentChannel.getEquipment().get2t3oSpacing();
                            break;

                        case IM_2T5O:
                            requiredSpacing = currentChannel.getEquipment().get2t5oSpacing();
                            break;

                        case IM_2T7O:
                            requiredSpacing = currentChannel.getEquipment().get2t7oSpacing();
                            break;

                        case IM_2T9O:
                            requiredSpacing = currentChannel.getEquipment().get2t9oSpacing();
                            break;

                        case IM_3T3O:
                        default:
                            requiredSpacing = currentChannel.getEquipment().get3t3oSpacing();
                            break;
                    }
                    if (spacing < requiredSpacing) {
                        addConflict(new Conflict(currentChannel, currentIntermod));
                    }
                }

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

    private void removeConflicts(Channel channel) {
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

    // Calculate intermodulations for a single channel
    private void calculateIntermods(Channel channel1) {
        final int numChannels = channels.size();
        for (int i = 0; i < numChannels; i++) {
            Channel channel2 = channels.get(i);

            if (channel1.getId() != channel2.getId()) {
                if (calculate2t3o) {
                    intermods.add(new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null));
                    intermods.add(new Intermod(Intermod.Type.IM_2T3O, channel2, channel1, null));
                }
                if (calculate2t5o) {
                    intermods.add(new Intermod(Intermod.Type.IM_2T5O, channel1, channel2, null));
                    intermods.add(new Intermod(Intermod.Type.IM_2T5O, channel2, channel1, null));
                }
                if (calculate2t7o) {
                    intermods.add(new Intermod(Intermod.Type.IM_2T7O, channel1, channel2, null));
                    intermods.add(new Intermod(Intermod.Type.IM_2T7O, channel2, channel1, null));
                }
                if (calculate2t9o) {
                    intermods.add(new Intermod(Intermod.Type.IM_2T9O, channel1, channel2, null));
                    intermods.add(new Intermod(Intermod.Type.IM_2T9O, channel2, channel1, null));
                }
                if (calculate3t3o) {
                    for (int j = i + 1; j < numChannels; j++) {
                        Channel channel3 = channels.get(j);

                        if (channel1.getId() != channel3.getId()) {
                            intermods.add(new Intermod(Intermod.Type.IM_3T3O, channel1, channel2, channel3));
                            intermods.add(new Intermod(Intermod.Type.IM_3T3O, channel2, channel3, channel1));
                            intermods.add(new Intermod(Intermod.Type.IM_3T3O, channel3, channel1, channel2));
                        }
                    }
                }
            }
        }
        Collections.sort(intermods);
        assert Helpers.isSorted(intermods);
    }

    // Remove all intermods that are contributed to by a specific channel
    private void removeIntermods(Channel channel) {
        intermods.removeIf(intermod -> intermod.getF1() == channel
                || intermod.getF2() == channel
                || intermod.getF3() == channel);
    }

    // Method to add new conflict to array and channel
    private void addConflict(Conflict conflict) {
        conflicts.add(conflict);
        conflict.getChannel().addConflict(conflict);
    }

    // Method to clear all conflicts from array and channels
    private void clearConflicts() {
        conflicts.clear();
        channels.forEach(Channel::clearConflicts);
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

    public Channel[] getChannels() {
        Channel[] channelArray = new Channel[channels.size()];
        return channels.toArray(channelArray);
    }

    // Get channel by id
    public Channel getChannelById(int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        return channels.get(index);
    }

    // Get number of channels
    public int getNumChannels() {
        return channels.size();
    }

    // Get number of intermods
    public int getNumIntermods() {
        return intermods.size();
    }

    // Get number of conflicts
    public int getNumConflicts() {
        return conflicts.size();
    }

    // Get number of conflicts by type
    public int getNumConflictsOfType(Conflict.Type type) {
        int count = 0;
        for(Conflict conflict : conflicts) {
            if (conflict.getType() == type) {
                count++;
            }
        }
        return count;
    }

    public boolean getCalculate2t3o() {
        return calculate2t3o;
    }

    public void setCalculate2t3o(boolean calculate2t3o) {
        this.calculate2t3o = calculate2t3o;
    }

    public boolean getCalculate2t5o() {
        return calculate2t5o;
    }

    public void setCalculate2t5o(boolean calculate2t5o) {
        this.calculate2t5o = calculate2t5o;
    }

    public boolean getCalculate2t7o() {
        return calculate2t7o;
    }

    public void setCalculate2t7o(boolean calculate2t7o) {
        this.calculate2t7o = calculate2t7o;
    }

    public boolean getCalculate2t9o() {
        return calculate2t9o;
    }

    public void setCalculate2t9o(boolean calculate2t9o) {
        this.calculate2t9o = calculate2t9o;
    }

    public boolean getCalculate3t3o() {
        return calculate3t3o;
    }

    public void setCalculate3t3o(boolean calculate3t3o) {
        this.calculate3t3o = calculate3t3o;
    }
}
