package com.stevebunting.rfcoordinator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
//    private final Analyser.Calculate calculations = new Analyser.Calculate();

    // Add new channel to coordination
    public final int addChannel(double frequency, Equipment equipment) throws InvalidFrequencyException {
        if (equipment == null) {
            throw new IllegalArgumentException("A valid equipment profile must be supplied");
        }
        final int id = idCounter;

        Channel newChannel = new Channel(id, frequency, equipment);
        int index = addChannelInPlace(newChannel);

//        List<Intermod> intermodList = Analyser.calculateIntermods(channels, newChannel, calculations);
//        mergeConflicts(Analyser.analyse(index, channels, intermodList, intermods));
//        mergeIntermods(intermodList);

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
//        Analyser.removeIntermods(channels.remove(index), intermods);

        // Insert Channel in new position
        addChannelInPlace(channelToUpdate);
//        Analyser.calculateIntermods(channels, channelToUpdate, calculations);

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
    private int addChannelInPlace(Channel channel) {
        int index;
        for (index = 0; index < channels.size(); index++) {
            if (channels.get(index).getFreq() > channel.getFreq()) {
                break;
            }
        }
        channels.add(index, channel);
        assert Helpers.isSorted(channels);

        return index;
    }

    // Remove channel from coordination, returns null if id is not found
    public Channel removeChannel(int id) {
        final int index = getChannelIndex(id);
        if (index == -1) {
            return null;
        }
        Channel removedChannel = channels.remove(index);

//        Analyser.removeIntermods(removedChannel, intermods);
//        Analyser.removeConflicts(removedChannel, conflicts);

        return removedChannel;
    }

    // Merge list of intermods into main list
    private void mergeIntermods(final List<Intermod> newIntermods) {
        intermods.addAll(newIntermods);
        Collections.sort(intermods);
    }

    // Merge list of conflicts into main list
    private void mergeConflicts(final List<Conflict> newConflicts) {
        for (Conflict conflict : newConflicts) {
            conflicts.add(conflict);
            conflict.getChannel().addConflict(conflict);
        }
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
//
//    public boolean getCalculate2t3o() {
//        return calculations.im2t3o;
//    }
//
//    public void setCalculate2t3o(final boolean calculate2t3o) {
//        this.calculations.im2t3o = calculate2t3o;
//    }
//
//    public boolean getCalculate2t5o() {
//        return calculations.im2t5o;
//    }
//
//    public void setCalculate2t5o(final boolean calculate2t5o) {
//        this.calculations.im2t5o = calculate2t5o;
//    }
//
//    public boolean getCalculate2t7o() {
//        return calculations.im2t7o;
//    }
//
//    public void setCalculate2t7o(final boolean calculate2t7o) {
//        this.calculations.im2t7o = calculate2t7o;
//    }
//
//    public boolean getCalculate2t9o() {
//        return calculations.im2t9o;
//    }
//
//    public void setCalculate2t9o(final boolean calculate2t9o) {
//        this.calculations.im2t9o = calculate2t9o;
//    }
//
//    public boolean getCalculate3t3o() {
//        return calculations.im3t3o;
//    }
//
//    public void setCalculate3t3o(final boolean calculate3t3o) {
//        this.calculations.im3t3o = calculate3t3o;
//    }
}
