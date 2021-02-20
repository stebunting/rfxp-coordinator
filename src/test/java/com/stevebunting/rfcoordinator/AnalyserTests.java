package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

@DisplayName("Analyser...")
class AnalyserTests {

    @DisplayName("intermod calculation methods...")
    @Nested
    class AnalyserIMTests {
        class ExpectedIntermod {
            final private int frequency;
            final private Intermod.Type type;

            public ExpectedIntermod(int frequency, Intermod.Type type) {
                this.frequency = frequency;
                this.type = type;
            }
        }

        Channel channel1;
        Channel channel2;
        Channel channel3;
        Channel channel4;
        ArrayList<Channel> channelList;
        final Equipment equipment = new Equipment("Test", "Equipment", 0, 1000, 25, 300, 100, 90, 0, 0, 50);
        private final Analyser.Calculate calculations = new Analyser.Calculate();

        @BeforeEach
        final void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(null, 606, equipment);
            channel2 = new Channel(null, 607, equipment);
            channel3 = new Channel(null, 608, equipment);
            channel4 = new Channel(null, 613.2, equipment);
            channelList = new ArrayList<>();
        }

        @DisplayName("throw when calculating with null channel list passed")
        @Test
        final void testCalculateIntermodsThrowsOnNullList() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.calculateIntermods(null, channel1, calculations));
        }

        @DisplayName("throw when calculating with null channel passed")
        @Test
        final void testCalculateIntermodsThrowsOnNullChannel() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.calculateIntermods(channelList, null, calculations));
        }

        @DisplayName("throw when calculating with null calculate object passed")
        @Test
        final void testCalculateIntermodsThrowsOnNullCalculate() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.calculateIntermods(new ArrayList<>(), channel1, null));
        }

        @DisplayName("throw when merging lists with null list A passed")
        @Test
        final void testMergeListsThrowsOnNullFirstList() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.mergeLists(null, new ArrayList<Intermod>()));
        }

        @DisplayName("throw when merging lists with null list B passed")
        @Test
        final void testMergeListsThrowsOnNullSecondList() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.mergeLists(new ArrayList<Intermod>(), null));
        }

        @DisplayName("throw when removing intermods with null list passed")
        @Test
        final void testRemoveIntermodsThrowsOnNullList() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.removeIntermods(channel1, null));
        }

        @DisplayName("calculate intermods correctly for two channels")
        @Test
        final void testGenerateIntermodsTwoChannels() {
            channelList.add(channel1);
            channelList.add(channel2);

            List<Intermod> intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            assertTrue(TestHelpers.isSorted(intermods));
            assertEquals(8, intermods.size());

            ExpectedIntermod[] expectedArray = new ExpectedIntermod[]{
                new ExpectedIntermod(602000, Intermod.Type.IM_2T9O),
                new ExpectedIntermod(603000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(604000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(605000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(608000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(609000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(610000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(611000, Intermod.Type.IM_2T9O)
            };

            for (int i = 0; i < expectedArray.length; i++) {
                ExpectedIntermod e = expectedArray[i];
                Intermod a = intermods.get(i);

                assertEquals(e.frequency, a.getFreq());
                assertEquals(e.type, a.getType());
            }
        }

        @DisplayName("calculate intermods correctly for three channels")
        @Test
        final void testGenerateIntermodsThreeChannels() throws IllegalArgumentException {
            channelList.add(channel1);
            channelList.add(channel2);

            List<Intermod> intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            assertEquals(8, intermods.size());

            channelList.add(channel3);

            List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, channel3, calculations);
            assertEquals(19, newIntermods.size());

            intermods = Analyser.mergeLists(intermods, newIntermods);
            assertTrue(TestHelpers.isSorted(intermods));

            ExpectedIntermod[] expectedArray = new ExpectedIntermod[]{
                new ExpectedIntermod(598000, Intermod.Type.IM_2T9O),
                new ExpectedIntermod(600000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(602000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(602000, Intermod.Type.IM_2T9O),
                new ExpectedIntermod(603000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(603000, Intermod.Type.IM_2T9O),
                new ExpectedIntermod(604000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(604000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(604000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(605000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(605000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(605000, Intermod.Type.IM_3T3O),
                new ExpectedIntermod(606000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(607000, Intermod.Type.IM_3T3O),
                new ExpectedIntermod(608000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(609000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(609000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(609000, Intermod.Type.IM_3T3O),
                new ExpectedIntermod(610000, Intermod.Type.IM_2T3O),
                new ExpectedIntermod(610000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(610000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(611000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(611000, Intermod.Type.IM_2T9O),
                new ExpectedIntermod(612000, Intermod.Type.IM_2T5O),
                new ExpectedIntermod(612000, Intermod.Type.IM_2T9O),
                new ExpectedIntermod(614000, Intermod.Type.IM_2T7O),
                new ExpectedIntermod(616000, Intermod.Type.IM_2T9O)
            };

            for (int i = 0; i < expectedArray.length; i++) {
                ExpectedIntermod e = expectedArray[i];
                Intermod a = intermods.get(i);

                assertEquals(e.frequency, a.getFreq());
                assertEquals(e.type, a.getType());
            }
        }

        @DisplayName("calculate intermods correctly for three channels")
        @Test
        final void testCorrectNumberOfIntermods() throws IllegalArgumentException, InvalidFrequencyException {
            channelList.add(channel1);
            List<Intermod> intermods = new ArrayList<>();

            int NUM_TESTS = 30;
            for (int i = 0; i < NUM_TESTS; i++) {
                Channel newChannel = new Channel(null, (i * 4) + 400, equipment);
                channelList.add(newChannel);
                assertEquals(channelList.size(), i + 2);

                List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, newChannel, calculations);
                intermods = Analyser.mergeLists(intermods, newIntermods);
                assertEquals(
                    TestHelpers.expectedIntermods(channelList.size(), calculations),
                    intermods.size());
            }
        }

        @DisplayName("return sorted list of intermods")
        @Test
        final void testSorting() throws IllegalArgumentException {
            channelList.add(channel1);
            channelList.add(channel2);
            List<Intermod> intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            assertTrue(TestHelpers.isSorted(intermods));

            channelList.add(channel3);
            List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, channel3, calculations);
            assertTrue(TestHelpers.isSorted(newIntermods));

            intermods = Analyser.mergeLists(intermods, newIntermods);
            assertTrue(TestHelpers.isSorted(intermods));
        }

        @DisplayName("remove all intermods associated with a channel")
        @Test
        final void testRemoveIntermods() {
            channelList.add(channel1);
            channelList.add(channel2);
            List<Intermod> intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            assertEquals(8, intermods.size());
            Analyser.removeIntermods(channel2, intermods);
            assertEquals(0, intermods.size());

            intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            channelList.add(channel3);
            List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, channel3, calculations);
            intermods = Analyser.mergeLists(intermods, newIntermods);
            assertEquals(27, intermods.size());
        }

        @DisplayName("remove required intermods from large list")
        @Test
        final void testRemoveIntermodsFromLargeList() throws InvalidFrequencyException {
            channelList.add(channel1);
            List<Intermod> intermods = new ArrayList<>();

            int NUM_TESTS = 30;
            for (int i = 0; i < NUM_TESTS; i++) {
                Channel newChannel = new Channel(null, (i * 4) + 400, equipment);
                channelList.add(newChannel);
                assertEquals(channelList.size(), i + 2);

                List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, newChannel, calculations);
                intermods = Analyser.mergeLists(intermods, newIntermods);
                assertEquals(
                    TestHelpers.expectedIntermods(channelList.size(), calculations),
                    intermods.size());
            }

            for (int i = 0; i < NUM_TESTS - 1; i++) {
                Collections.shuffle(channelList);
                Channel channelToRemove = channelList.remove(0);
                Analyser.removeIntermods(channelToRemove, intermods);
                assertEquals(
                    TestHelpers.expectedIntermods(channelList.size(), calculations),
                    intermods.size());
            }
        }

        @DisplayName("add and remove intermods with no artifacts")
        @Test
        final void testAddAndRemoveIntermodsWithNoArtifacts() throws InvalidFrequencyException {
            int NUM_TESTS = 50;
            Random rand = new Random();
            List<Intermod> intermods = new ArrayList<>();

            for (int i = 0; i < NUM_TESTS; i++) {
                if (i % 3 == 2 && channelList.size() > 0) {
                    Collections.shuffle(channelList);
                    Channel channel = channelList.remove(0);
                    Analyser.removeIntermods(channel, intermods);
                } else {
                    Channel channel = new Channel(null, (rand.nextInt() % 500) + 400, equipment);
                    channelList.add(channel);
                    List<Intermod> newIM = Analyser.calculateIntermods(channelList, channel, calculations);
                    intermods = Analyser.mergeLists(intermods, newIM);
                }
                assertEquals(
                        TestHelpers.expectedIntermods(channelList.size(), calculations),
                        intermods.size());
            }
            assert(TestHelpers.isSorted(intermods));

            List<Intermod> newIntermods = new ArrayList<>();
            int numChannels = channelList.size();
            for (int i = 0; i < numChannels; i++) {
                Channel channel = channelList.remove(0);
                newIntermods = Analyser.mergeLists(
                        newIntermods,
                        Analyser.calculateIntermods(channelList, channel, calculations));
            }
            assert(TestHelpers.isSorted(newIntermods));

            assertEquals(0, channelList.size());
            assertEquals(newIntermods.size(), intermods.size());
            for (int i = 0; i < newIntermods.size(); i++) {
                assertEquals(newIntermods.get(i).getFreq(), intermods.get(i).getFreq());
                assertEquals(newIntermods.get(i).getType(), intermods.get(i).getType());
            }
        }
    }

    @DisplayName("channel conflict calculation methods...")
    @Nested
    class AnalyserChannelConflictTests {
        Channel channel1;
        Channel channel2;
        ArrayList<Channel> channelList;
        ArrayList<Intermod> intermodList;
        final Equipment equipment = new Equipment("Test", "Equipment", 0, 1000, 25, 300, 100, 90, 0, 0, 50);
        private final Analyser.Calculate calculations = new Analyser.Calculate();

        @BeforeEach
        final void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(null, 606, equipment);
            channel2 = new Channel(null, 606.2, equipment);
            channelList = new ArrayList<>();
            intermodList = new ArrayList<>();
            calculations.im2t3o = false;
            calculations.im2t5o = false;
            calculations.im2t7o = false;
            calculations.im2t9o = false;
            calculations.im3t3o = false;
        }

        @DisplayName("throw when calculating channel conflicts with null conflicts list")
        @Test
        final void testAnalyseTwoChannelsThrowsOnNullConflictList() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.getConflictsTwoChannels(null, channel1, channel2));
        }

        @DisplayName("throw when calculating channel conflicts with null channel 1")
        @Test
        final void testAnalyseTwoChannelsThrowsOnNullChannel1() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.getConflictsTwoChannels(new ArrayList<>(), null, channel2));
        }

        @DisplayName("throw when calculating channel conflicts with null channel 2")
        @Test
        final void testAnalyseTwoChannelsThrowsOnNullChannel2() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.getConflictsTwoChannels(new ArrayList<>(), channel1, null));
        }

        @DisplayName("calculates channel conflicts for 2 conflicting channels")
        @Test
        final void testGetConflictsTwoChannels() {
            List<Conflict> conflicts = new ArrayList<>();
            Analyser.getConflictsTwoChannels(conflicts, channel1, channel2);
            assertEquals(2, conflicts.size());
        }

        @DisplayName("calculates channel conflicts for 2 conflicting channels with different equipment")
        @Test
        final void testGetConflictsTwoChannelsDifferentEquipment() throws InvalidFrequencyException {
            List<Conflict> conflicts = new ArrayList<>();
            channel2 = new Channel(24, 606.2, new Equipment("Efficient", "Equipment", 0, 1000, 25, 100, 100, 90, 0, 0, 50));
            Analyser.getConflictsTwoChannels(conflicts, channel1, channel2);
            assertEquals(1, conflicts.size());
            assertSame(conflicts.get(0).getChannel(), channel1);
            assertSame(conflicts.get(0).getConflictChannel(), channel2);
        }

        @DisplayName("calculates channel conflicts for 2 non-conflicting channels")
        @Test
        final void testGetConflictsTwoChannelsWithNoConflicts() throws InvalidFrequencyException {
            List<Conflict> conflicts = new ArrayList<>();
            channel2 = new Channel(24, 606.3, equipment);
            Analyser.getConflictsTwoChannels(conflicts, channel1, channel2);
            assertEquals(0, conflicts.size());
        }

        @DisplayName("throw when calculating conflicts list with null channel list")
        @Test
        final void testAnalyseChannelSpacingWithNullList() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.analyseChannelSpacing(null, channel1));
        }

        @DisplayName("throw when calculating conflicts list with null channel")
        @Test
        final void testAnalyseChannelSpacingWithNullChannel() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.analyseChannelSpacing(new ArrayList<>(), null));
        }

        @DisplayName("generate conflict list from list of channels")
        @Test
        final void testAnalyseChannelSpacing() throws InvalidFrequencyException {
            double[] frequencies = new double[]{ 470, 470.5, 480, 480.1, 480.4, 470.2, 480.7, 534, 534.3, 534.5 };
            int[] expected = new int[]{ 0, 0, 0, 2, 2, 4, 4, 4, 4, 6 };

            List<Channel> channelList = new ArrayList<>();
            List<Conflict> conflictList = new ArrayList<>();
            for (int i = 0; i < frequencies.length; i++) {
                Channel channel = new Channel(null, frequencies[i], equipment);
                conflictList.addAll(Analyser.analyseChannelSpacing(channelList, channel));
                channelList.add(channel);
                assertEquals(conflictList.size(), expected[i]);
            }
        }
    }

    @DisplayName("Intermodulation methods...")
    @Nested
    class AnalyserIMConflictTests {
        Channel channel1;
        Channel channel2;
        List<Channel> channelList;
        List<Intermod> intermodList;
        List<Conflict> conflictList;
        final Equipment equipment = new Equipment("Test", "Equipment", 0, 1000, 25, 300, 100, 90, 0, 0, 50);
        private final Analyser.Calculate calculations = new Analyser.Calculate();

        @BeforeEach
        final void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(null, 606, equipment);
            channel2 = new Channel(null, 606.2, equipment);
            channelList = new ArrayList<>();
            intermodList = new ArrayList<>();
            conflictList = new ArrayList<>();
        }

        @DisplayName("throw when calculating channel/im conflicts with null channel")
        @Test
        final void testAnalyseChannelAndImThrowsOnNullChannel() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.getConflictsChannelAndIM(null, new Intermod(Intermod.Type.IM_2T3O, channel1, channel2, null)));
        }

        @DisplayName("throw when calculating channel/im conflicts with null intermod")
        @Test
        final void testAnalyseChannelAndImThrowsOnNullIntermod() {
            assertThrows(IllegalArgumentException.class,
                    () -> Analyser.getConflictsChannelAndIM(channel1, null));
        }
    }

    @DisplayName("Analysis methods...")
    @Nested
    class Analysis {
        Channel channel1;
        Channel channel2;
        List<Channel> channelList;
        List<Intermod> intermodList;
        List<Conflict> conflictList;
        final Equipment equipment = new Equipment("Test", "Equipment", 0, 1000, 25, 300, 100, 90, 0, 0, 50);
        private final Analyser.Calculate calculations = new Analyser.Calculate();

        @BeforeEach
        final void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(null, 606, equipment);
            channel2 = new Channel(null, 606.2, equipment);
            channelList = new ArrayList<>();
            intermodList = new ArrayList<>();
            conflictList = new ArrayList<>();
        }

        @DisplayName("generate analysis")
        @Test
        final void testAnalysis() throws InvalidFrequencyException {
            List<Double> frequencies = new ArrayList<>(Arrays.asList(
                606.0, 606.3, 606.775, 607.1, 607.525, 607.7, 608.175,
                608.35, 609.0, 610.3, 610.525, 611.0, 611.325
            ));
            int numConflicts = 112;
            int numChannelConflicts = 6;
            int num2T3OConflicts = 18;
            int num2T5OConflicts = 20;
            int num2T7OConflicts = 0;
            int num2T9OConflicts = 0;
            int num3T3OConflicts = 68;

            List<Conflict> channelConflictList = new ArrayList<>();
            for (int i = 0; i < frequencies.size(); i++) {
                // Add new channel
                Channel channel = new Channel(null, frequencies.get(i), equipment);
                channelList.add(channel);

                // Calculate Intermods
                List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, channel, calculations);

                // Get IM Conflicts
                conflictList.addAll(Analyser.analyseIMSpacing(channel, intermodList));
                conflictList.addAll(Analyser.analyseIMSpacing(channelList, newIntermods));

                // Get Channel Conflicts
                channelConflictList.addAll(Analyser.analyseChannelSpacing(channelList, channel));

                // Merge intermods into current list
                intermodList = Analyser.mergeLists(intermodList, newIntermods);
            }

            assertEquals(frequencies.size(), channelList.size());
            assertEquals(numChannelConflicts, channelConflictList.size());
            assertEquals(numConflicts - numChannelConflicts, conflictList.size());
            assertEquals(num2T3OConflicts, TestHelpers.count(conflictList, (Conflict conflict) -> {
                return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                        && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T3O;
            }));
            assertEquals(num2T5OConflicts, TestHelpers.count(conflictList, (Conflict conflict) -> {
                return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                        && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T5O;
            }));
            assertEquals(num2T7OConflicts, TestHelpers.count(conflictList, (Conflict conflict) -> {
                return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                        && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T7O;
            }));
            assertEquals(num2T9OConflicts, TestHelpers.count(conflictList, (Conflict conflict) -> {
                return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                        && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T9O;
            }));
            assertEquals(num3T3OConflicts, TestHelpers.count(conflictList, (Conflict conflict) -> {
                return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                        && conflict.getConflictIntermod().getType() == Intermod.Type.IM_3T3O;
            }));
        }

        @DisplayName("gets first im index")
        @Test
        final void testFirstIMIndex() {
        }
    }
}
