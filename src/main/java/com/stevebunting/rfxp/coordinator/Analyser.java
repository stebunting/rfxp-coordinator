package com.stevebunting.rfxp.coordinator;

import org.jetbrains.annotations.NotNull;
import java.util.*;

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

    // Number of invalid channels
    private int numInvalidChannels = 0;
    private int numChannelConflicts = 0;
    final private Map<Intermod.Type, Integer> numIMConflicts = new HashMap<>();

    // Settings
    final boolean randomSelection = true;

    // Metrics
    enum Metrics {
        ITERATION_COUNT,
        INIT_TIME,
        FIND_RANDOM_NUMBER_TIME,
        BUILD_CHANNEL_TIME,
        CALCULATE_INTERMODS_TIME,
        CALCULATE_CONFLICTS_TIME,
        MERGE_INTERMODS_TIME,
        COPY_MAP_TIME,
        RESTORE_ANALYSIS_TIME,
        TOTAL_TIME
    }

    final private Map<Metrics, Long> metrics = new HashMap<>();

    Analyser() {
        numIMConflicts.put(Intermod.Type.IM_2T3O, 0);
        numIMConflicts.put(Intermod.Type.IM_2T5O, 0);
        numIMConflicts.put(Intermod.Type.IM_2T7O, 0);
        numIMConflicts.put(Intermod.Type.IM_2T9O, 0);
        numIMConflicts.put(Intermod.Type.IM_3T3O, 0);

        metrics.put(Metrics.ITERATION_COUNT, 0L);
        metrics.put(Metrics.INIT_TIME, 0L);
        metrics.put(Metrics.FIND_RANDOM_NUMBER_TIME, 0L);
        metrics.put(Metrics.BUILD_CHANNEL_TIME, 0L);
        metrics.put(Metrics.CALCULATE_INTERMODS_TIME, 0L);
        metrics.put(Metrics.CALCULATE_CONFLICTS_TIME, 0L);
        metrics.put(Metrics.MERGE_INTERMODS_TIME, 0L);
        metrics.put(Metrics.COPY_MAP_TIME, 0L);
        metrics.put(Metrics.RESTORE_ANALYSIS_TIME, 0L);
        metrics.put(Metrics.TOTAL_TIME, 0L);
    }

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
     */
    final void updateChannel(@NotNull final Channel channel) throws IllegalArgumentException {
        if (channel == null) {
            throw new IllegalArgumentException();
        }

        final boolean channelRemoved = removeChannel(channel);
        if (channelRemoved) {
            addChannel(channel);
        }
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

        // Remove channel conflicts from counters
        for (Conflict conflict : channel.getConflicts()) {
            incrementConflictCounter(conflict, -1);
        }

        return newConflicts.size();
    }

    /**
     * Method to calculate all intermodulations between a single channel
     * and a list of channels. The new channel may be included in the list
     * of channels.
     *
     * @param newChannel channel to generate intermods against
     *                  conflicts found
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
        newIntermods.sort(null);
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

        final Integer maxSpacing = channel.getEquipment().getSpacing(intermod.getType());

        final int difference = Math.abs(channel.getFreq() - intermod.getFreq());
        if (maxSpacing != null && maxSpacing > difference) {
            Conflict newConflict = new Conflict(channel, intermod);
            conflicts.add(newConflict);
            if (addConflictToChannel) {
                addConflict(channel, newConflict);
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
                        addConflict(channel, newConflict);
                    }
                }
                if (difference < newChannel.getEquipment().getChannelSpacing()) {
                    final Conflict newConflict = new Conflict(newChannel, channel);
                    conflicts.add(newConflict);
                    if (addConflictToListChannel) {
                        addConflict(newChannel, newConflict);
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
                removeConflict(channel, conflict);
                return true;
            }
            switch (conflict.getType()) {
                case CHANNEL_SPACING:
                    if (conflict.getConflictChannel() == channel) {
                        removeConflict(conflict.getChannel(), conflict);
                        return true;
                    }
                    return false;

                case INTERMOD_SPACING:
                    if (conflict.getConflictIntermod().getF1() == channel
                     || conflict.getConflictIntermod().getF2() == channel
                     || conflict.getConflictIntermod().getF3() == channel) {
                        removeConflict(conflict.getChannel(), conflict);
                        return true;
                    }
                    return false;

                default:
                    return false;
            }
        });
    }

    /**
     * Method to add a single conflict reference to a channel and update
     * invalid channels and conflicts counters.
     *
     * @param channel channel to add conflict to
     * @param conflict conflict to add
     */
    private void addConflict(@NotNull final Channel channel, @NotNull final Conflict conflict) {
        incrementConflictCounter(conflict, 1);
        if (channel.getValidity() == Channel.Validity.VALID) {
            numInvalidChannels++;
        }
        channel.addConflict(conflict);
    }

    /**
     * Method to remove a single conflict reference from a channel and update
     * invalid channels counter.
     *
     * @param channel channel to remove conflict from
     * @param conflict conflict to remove
     */
    private void removeConflict(@NotNull final Channel channel, @NotNull final Conflict conflict) {
        incrementConflictCounter(conflict, -1);
        final boolean invalidBefore = channel.getValidity() != Channel.Validity.VALID;

        channel.removeConflict(conflict);

        if (invalidBefore && channel.getValidity() == Channel.Validity.VALID) {
            numInvalidChannels--;
        }
    }

    /**
     * Method to increment or decrement conflict counters depending on conflict
     * type.
     *
     * @param conflict conflict to check
     * @param incrementor integer to increment counter by (typically 1 or -1)
     */
    private void incrementConflictCounter(@NotNull final Conflict conflict, final int incrementor) {
        if (conflict.getType() == Conflict.Type.CHANNEL_SPACING) {
            numChannelConflicts += incrementor;
        } else if (conflict.getType() == Conflict.Type.INTERMOD_SPACING) {
            Intermod.Type imType = conflict.getConflictIntermod().getType();
            numIMConflicts.put(imType, numIMConflicts.get(imType) + incrementor);
        }
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

    final int getValidChannels() {
        return channels.size() - numInvalidChannels;
    }

    final int getNumChannelConflicts() {
        return numChannelConflicts;
    }

    final int getNumIMConflicts() {
        return numIMConflicts.get(Intermod.Type.IM_2T3O)
                + numIMConflicts.get(Intermod.Type.IM_2T5O)
                + numIMConflicts.get(Intermod.Type.IM_2T7O)
                + numIMConflicts.get(Intermod.Type.IM_2T9O)
                + numIMConflicts.get(Intermod.Type.IM_3T3O);
    }

    final int getNumIMConflicts(@NotNull final Intermod.Type type) {
        if (type == null) {
            return 0;
        }
        return numIMConflicts.get(type);
    }

    final List<Integer> addNewChannels(
            final int num,
            @NotNull final Equipment equipment,
            final boolean printReport
    ) throws InvalidFrequencyException {
        // Generate and populate list of possible frequencies
        resetMetrics();
        long startTime = System.nanoTime();

        HashMap<Integer, Integer> possibleFrequencies = new HashMap<>();

        // Initialise possible frequencies
        int counter = equipment.getRange().getLo();
        while (counter <= equipment.getRange().getHi()) {
            possibleFrequencies.put(counter, counter);
            counter += equipment.getTuningAccuracy();
        }

        for (Channel channel : channels) {
            removeConflictRange(equipment, channel, possibleFrequencies);
        }

        for (Intermod im : intermods) {
            removeConflictRange(equipment, im, possibleFrequencies);
        }

        List<Integer> newFrequencies = new ArrayList<>();
        metrics.put(Metrics.INIT_TIME, System.nanoTime() - startTime);

        newChannel(num, equipment, possibleFrequencies, newFrequencies);
        metrics.put(Metrics.TOTAL_TIME, System.nanoTime() - startTime);

        if (printReport) {
            printMetrics();
        }

        return newFrequencies;
    }

    /**
     * Create a copy of the possibleFrequencies array. Array is initialised
     * removing a frequency range from the array. It also removes
     * intermodulation products from a list. This method should be used in
     * a generate frequency process where the frequency to remove is of the
     * same type as the equipment to add.
     *
     * @param possibleFrequencies Map of possibleFrequencies to clone
     * @param frequency Frequency to remove from list
     * @param equipment Equipment to define width of frequency component
     * @param intermods Intermods to remove from list
     * @return New shallow copied map
     */
    private HashMap<Integer, Integer> clonePossibleFrequencies(
            @NotNull final HashMap<Integer, Integer> possibleFrequencies,
            final int frequency,
            @NotNull final Equipment equipment,
            @NotNull final List<Intermod> intermods
    ) {
        if (possibleFrequencies == null) {
            return null;
        }
        if (equipment == null) {
            return new HashMap<>(possibleFrequencies);
        }

        int rangeLo = frequency - equipment.getChannelSpacing();
        int rangeHi = frequency + equipment.getChannelSpacing();
        final HashMap<Integer, Integer> newMap = new HashMap<>(possibleFrequencies.size());

        // Create new map with new frequencies removed
        for (Integer freq : possibleFrequencies.keySet()) {
            if (freq <= rangeLo || freq >= rangeHi) {
                newMap.put(freq, freq);
            }
        }

        if (intermods == null) {
            return newMap;
        }

        // Remove intermods
        for (Intermod im : intermods) {
            removeConflictRange(equipment, im, newMap);
        }

        return newMap;
    }

    /**
     * Remove a range of frequencies from a possibleFrequencies map
     * depending on required equipment constraints. Takes either a
     * channel or an intermod.
     *
     * @param equipment Equipment to generate a frequency for
     * @param component Channel or Intermod to remove range for
     * @param possibleFrequencies Map of possibleFrequencies to amend
     */
    final void removeConflictRange(
            @NotNull final Equipment equipment,
            @NotNull final FrequencyComponent component,
            @NotNull final Map<Integer, Integer> possibleFrequencies
    ) {
        if (equipment == null || component == null || possibleFrequencies == null) {
            return;
        }

        final int spacing = component instanceof Channel
                ? Math.max(equipment.getChannelSpacing(), ((Channel) component).getEquipment().getChannelSpacing())
                : equipment.getSpacing(((Intermod) component).getType());

        final int rangeLo = component.getFreq() - spacing;
        final int rangeHi = component.getFreq() + spacing;
        final int startFreq = equipment.getTuningAccuracy() * (int) Math.ceil((1 + rangeLo) / (double) equipment.getTuningAccuracy());

        // This can be refined, very rough
        if (rangeHi < equipment.getRange().getLo() || rangeLo > equipment.getRange().getHi()) {
            return;
        }

        for (int i = startFreq; i < rangeHi; i += equipment.getTuningAccuracy()) {
            possibleFrequencies.remove(i);
        }
    }

    final boolean newChannel(
            final int num,
            final Equipment equipment,
            final HashMap<Integer, Integer> possibleFrequencies,
            final List<Integer> newFrequencies
    ) throws InvalidFrequencyException {
        if (num == 0) {
            return true;
        }
        Random rand = new Random();
        List<Conflict> newConflicts = new ArrayList<>();
        long startTime;
        Channel testChannel;

        while (possibleFrequencies.size() > 0) {
            // Increment iterations
            metrics.put(Metrics.ITERATION_COUNT, metrics.get(Metrics.ITERATION_COUNT) + 1);

            // Pick a frequency from the list of possible frequencies
            startTime = System.nanoTime();
            Set<Integer> keySet = possibleFrequencies.keySet();
            int newFrequency = randomSelection
                    ? (int) keySet.toArray()[rand.nextInt(possibleFrequencies.size())]
                    : Collections.min(keySet);
            metrics.put(Metrics.FIND_RANDOM_NUMBER_TIME, metrics.get(Metrics.FIND_RANDOM_NUMBER_TIME) + System.nanoTime() - startTime);

            // Build Test Channel
            startTime = System.nanoTime();
            testChannel = new Channel(null, Channel.kHzToMHz(newFrequency), equipment);
            metrics.put(Metrics.BUILD_CHANNEL_TIME, metrics.get(Metrics.BUILD_CHANNEL_TIME) + System.nanoTime() - startTime);

            // Get intermods
            startTime = System.nanoTime();
            List<Intermod> newIntermods = calculateIntermods(testChannel);
            newConflicts.clear();
            getIMConflicts(channels, newIntermods, newConflicts, false);
            metrics.put(Metrics.CALCULATE_INTERMODS_TIME, metrics.get(Metrics.CALCULATE_INTERMODS_TIME) + System.nanoTime() - startTime);

            // Remove frequency from possible frequencies
            possibleFrequencies.remove(newFrequency);

            // If frequency is good, add channel to analysis and move onto next
            if (newConflicts.size() == 0) {
                // Add channel to analysis
                startTime = System.nanoTime();
                channels.add(testChannel);
                final List<Intermod> backupIntermods = intermods;
                intermods = mergeLists(intermods, newIntermods);
                metrics.put(Metrics.MERGE_INTERMODS_TIME, metrics.get(Metrics.MERGE_INTERMODS_TIME) + System.nanoTime() - startTime);

                // Update Possible Frequencies and iterate
                startTime = System.nanoTime();
                final HashMap<Integer, Integer> newMap = clonePossibleFrequencies(possibleFrequencies, newFrequency, equipment, newIntermods);
                metrics.put(Metrics.COPY_MAP_TIME, metrics.get(Metrics.COPY_MAP_TIME) + System.nanoTime() - startTime);
                boolean valid = newChannel(num - 1, equipment, newMap, newFrequencies);

                // Restore analysis
                startTime = System.nanoTime();
                channels.remove(testChannel);
                intermods = backupIntermods;
                metrics.put(Metrics.RESTORE_ANALYSIS_TIME, metrics.get(Metrics.RESTORE_ANALYSIS_TIME) + System.nanoTime() - startTime);

                if (valid) {
                    newFrequencies.add(newFrequency);
                    return true;
                }
            }
        }
        return false;
    }

    private long nsToMs(@NotNull final Metrics metric) {
        if (metric == null) {
            return 0;
        }
        return metrics.get(metric) / 1000000;
    }

    private double percentage(@NotNull final Metrics metric, @NotNull final Metrics total) {
        return 100 * metrics.get(metric) / (double) metrics.get(total);
    }

    private void printMetrics() {
        String format = "│ \u001B[31m%24s\u001B[0m │ %5dms │ %6.1f%% │%n";
        System.out.println("┌──────────────────────────┬───────────────────┐");
        System.out.printf("│ \u001B[31m%24s\u001B[0m │ %-17d │%n",
                "ITERATION COUNT",
                metrics.get(Metrics.ITERATION_COUNT));
        System.out.printf("│ \u001B[31m%24s\u001B[0m │ %-17s │%n",
                "TIME PER ITERATION",
                String.format("%dµs", metrics.get(Metrics.TOTAL_TIME) / metrics.get(Metrics.ITERATION_COUNT) / 1000));
        System.out.println("├──────────────────────────┼─────────┬─────────┤");
        System.out.printf(
                "│ \u001B[31m%24s\u001B[0m │ \u001B[31m%7s\u001B[0m │ \u001B[31m%7s\u001B[0m │%n",
                "STAGE", "TIME", "PERCENT");
        System.out.println("├──────────────────────────┼─────────┼─────────┤");
        System.out.printf(format,
                "INITIALISATION",
                nsToMs(Metrics.INIT_TIME),
                percentage(Metrics.INIT_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "FINDING RANDOM NUMBERS",
                nsToMs(Metrics.FIND_RANDOM_NUMBER_TIME),
                percentage(Metrics.FIND_RANDOM_NUMBER_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "CONSTRUCTING CHANNEL",
                nsToMs(Metrics.BUILD_CHANNEL_TIME),
                percentage(Metrics.BUILD_CHANNEL_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "CALCULATING INTERMODS",
                nsToMs(Metrics.CALCULATE_INTERMODS_TIME),
                percentage(Metrics.CALCULATE_INTERMODS_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "CALCULATING CONFLICTS",
                nsToMs(Metrics.CALCULATE_CONFLICTS_TIME),
                percentage(Metrics.CALCULATE_CONFLICTS_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "MERGING INTERMODS",
                nsToMs(Metrics.MERGE_INTERMODS_TIME),
                percentage(Metrics.MERGE_INTERMODS_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "COPYING MAP",
                nsToMs(Metrics.COPY_MAP_TIME),
                percentage(Metrics.COPY_MAP_TIME, Metrics.TOTAL_TIME));
        System.out.printf(format,
                "RESTORING ANALYSIS",
                nsToMs(Metrics.RESTORE_ANALYSIS_TIME),
                percentage(Metrics.RESTORE_ANALYSIS_TIME, Metrics.TOTAL_TIME));
        System.out.println("├──────────────────────────┼─────────┼─────────┤");
        System.out.printf(format,
                "TOTAL",
                metrics.get(Metrics.TOTAL_TIME) / 1000000,
                100 * (metrics.get(Metrics.FIND_RANDOM_NUMBER_TIME)
                        + metrics.get(Metrics.BUILD_CHANNEL_TIME)
                        + metrics.get(Metrics.CALCULATE_INTERMODS_TIME)
                        + metrics.get(Metrics.CALCULATE_CONFLICTS_TIME)
                        + metrics.get(Metrics.MERGE_INTERMODS_TIME)
                        + metrics.get(Metrics.COPY_MAP_TIME)
                        + metrics.get(Metrics.RESTORE_ANALYSIS_TIME))
                        / (double) metrics.get(Metrics.TOTAL_TIME));
        System.out.println("└──────────────────────────┴─────────┴─────────┘");
    }

    private void resetMetrics() {
        metrics.put(Metrics.ITERATION_COUNT, 0L);
        metrics.put(Metrics.INIT_TIME, 0L);
        metrics.put(Metrics.FIND_RANDOM_NUMBER_TIME, 0L);
        metrics.put(Metrics.BUILD_CHANNEL_TIME, 0L);
        metrics.put(Metrics.CALCULATE_INTERMODS_TIME, 0L);
        metrics.put(Metrics.CALCULATE_CONFLICTS_TIME, 0L);
        metrics.put(Metrics.MERGE_INTERMODS_TIME, 0L);
        metrics.put(Metrics.COPY_MAP_TIME, 0L);
        metrics.put(Metrics.RESTORE_ANALYSIS_TIME, 0L);
        metrics.put(Metrics.TOTAL_TIME, 0L);
    }
}
