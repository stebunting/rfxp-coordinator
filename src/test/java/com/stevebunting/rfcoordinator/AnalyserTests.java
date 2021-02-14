package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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
        final void testGenerateIntermodsThreeChannels() {
            channelList.add(channel1);
            channelList.add(channel2);

            List<Intermod> intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            assertEquals(8, intermods.size());

            channelList.add(channel3);

            List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, channel3, calculations);
            assertEquals(19, newIntermods.size());

            Analyser.mergeIntermods(intermods, newIntermods);
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
        final void testCorrectNumberOfIntermods() throws InvalidFrequencyException {
            channelList.add(channel1);
            List<Intermod> intermods = new ArrayList<>();

            int NUM_TESTS = 30;
            for (int i = 0; i < NUM_TESTS; i++) {
                Channel newChannel = new Channel(null, (i * 4) + 400, equipment);
                channelList.add(newChannel);
                assertEquals(channelList.size(), i + 2);

                List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, newChannel, calculations);
                Analyser.mergeIntermods(intermods, newIntermods);
                assertEquals(
                    TestHelpers.expectedIntermods(channelList.size(), calculations),
                    intermods.size());
            }
        }

        @DisplayName("return sorted list of intermods")
        @Test
        final void testSorting() {
            channelList.add(channel1);
            channelList.add(channel2);
            List<Intermod> intermods = Analyser.calculateIntermods(channelList, channel2, calculations);
            assertTrue(TestHelpers.isSorted(intermods));

            channelList.add(channel3);
            List<Intermod> newIntermods = Analyser.calculateIntermods(channelList, channel3, calculations);
            assertTrue(TestHelpers.isSorted(newIntermods));

            Analyser.mergeIntermods(intermods, newIntermods);
            assertTrue(TestHelpers.isSorted(intermods));
        }

        @DisplayName("removes all intermods associated with a channel")
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
            Analyser.mergeIntermods(intermods, newIntermods);
            assertEquals(27, intermods.size());
        }

        @DisplayName("removes all intermods from large list")
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
                Analyser.mergeIntermods(intermods, newIntermods);
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

        @DisplayName("calculates channel spacing conflicts for 2 conflicting channels")
        @Test
        final void testTwoChannelAnalysis() {
            List<Conflict> newConflicts;
            channelList.add(channel1);
            channelList.add(channel2);
            newConflicts = Analyser.analyseChannelSpacing(1, channelList);
            assertEquals(2, newConflicts.size());
        }

        @DisplayName("calculates channel spacing conflicts for 2 conflicting channels with different equipment")
        @Test
        final void testTwoChannelAnalysisWithDifferentEquipment() throws InvalidFrequencyException {
            List<Conflict> newConflicts;
            channelList.add(channel1);
            channel2 = new Channel(24, 606.2, new Equipment("Efficient", "Equipment", 0, 1000, 25, 100, 100, 90, 0, 0, 50));
            channelList.add(channel2);
            newConflicts = Analyser.analyseChannelSpacing(1, channelList);
            assertEquals(1, newConflicts.size());
        }
    }

    @DisplayName("IM conflict calculation methods...")
    @Nested
    class AnalyserIMConflictTests {
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
        }

        @DisplayName("calculates channel spacing conflicts for 2 conflicting channels")
        @Test
        final void testTwoChannelAnalysis() {
            List<Conflict> newConflicts;
            channelList.add(channel1);
            channelList.add(channel2);
            newConflicts = Analyser.analyseChannelSpacing(1, channelList);
            assertEquals(2, newConflicts.size());
        }
    }
}
