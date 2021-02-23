package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

@DisplayName("Analysis methods...")
class AnalyserTests {

    Analyser analyser;
    final Equipment equipment = new Equipment("Test", "Equipment", 0, 1000, 25, 300, 100, 90, 0, 0, 50);
    final EquipmentProfiles equipmentProfiles = EquipmentProfiles.INSTANCE;

    @BeforeEach
    final void setUp() {
        analyser = new Analyser();
    }

    @DisplayName("throw when adding null channel")
    @Test
    final void testThrowOnAddNullChannel() {
        assertThrows(IllegalArgumentException.class, () -> analyser.addChannel(null));
    }

    @DisplayName("throw when removing null channel")
    @Test
    final void testThrowOnRemoveNullChannel() {
        assertThrows(IllegalArgumentException.class, () -> analyser.removeChannel(null));
    }

    @DisplayName("generate valid analysis")
    @Test
    final void testGenerateValidAnalysis() throws InvalidFrequencyException{
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                470.05, 470.6, 471.35, 471.95, 472.35, 474.8,
                475.25, 475.6, 476.75, 477.05, 478.0
        ));
        Collections.shuffle(frequencies);

        int numConflicts = 0;
        int numChannelConflicts = 0;
        int num2T3OConflicts = 0;
        int num2T5OConflicts = 0;
        int num2T7OConflicts = 0;
        int num2T9OConflicts = 0;
        int num3T3OConflicts = 0;

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        TestHelpers.assertConflicts(analyser.getConflictList(),
                numConflicts,
                numChannelConflicts,
                num2T3OConflicts,
                num2T5OConflicts,
                num2T7OConflicts,
                num2T9OConflicts,
                num3T3OConflicts);
    }

    @DisplayName("generate invalid analysis")
    @Test
    final void testGenerateInvalidAnalysis() throws InvalidFrequencyException {
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                606.0, 606.3, 606.775, 607.1, 607.525, 607.7, 608.175,
                608.35, 609.0, 610.3, 610.525, 611.0, 611.325
        ));
        Collections.shuffle(frequencies);

        int numConflicts = 112;
        int numChannelConflicts = 6;
        int num2T3OConflicts = 18;
        int num2T5OConflicts = 20;
        int num2T7OConflicts = 0;
        int num2T9OConflicts = 0;
        int num3T3OConflicts = 68;

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        TestHelpers.assertConflicts(analyser.getConflictList(),
                numConflicts,
                numChannelConflicts,
                num2T3OConflicts,
                num2T5OConflicts,
                num2T7OConflicts,
                num2T9OConflicts,
                num3T3OConflicts);
    }

    @DisplayName("remove channels cleanly")
    @Test
    final void testRemoveChannelsCleanly() throws InvalidFrequencyException {
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                768.275, 769.1, 765.25, 765.8, 766.125, 767.975,
                764.25, 768.55, 770.025, 763.975, 770.575, 771.1
        ));
        Collections.shuffle(frequencies);

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        int numConflicts = 61;
        int numChannelConflicts = 4;
        int num2T3OConflicts = 10;
        int num2T5OConflicts = 3;
        int num2T7OConflicts = 0;
        int num2T9OConflicts = 0;
        int num3T3OConflicts = 44;

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        TestHelpers.assertConflicts(analyser.getConflictList(),
                numConflicts,
                numChannelConflicts,
                num2T3OConflicts,
                num2T5OConflicts,
                num2T7OConflicts,
                num2T9OConflicts,
                num3T3OConflicts);

        int[] frequenciesToRemove = new int[]{
                769100, 765800, 768275, 763975, 767975, 770575,
                771100, 770025, 765250, 764250, 768550, 766125};
        int[] conflicts = new int[]{44, 24, 10, 8, 2, 0, 0, 0, 0, 0, 0, 0};
        int[] channelConflicts = new int[]{4, 4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t3oConflicts = new int[]{8, 8, 4, 4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t5oConflicts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t7oConflicts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t9oConflicts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im3t3oConflicts = new int[]{32, 12, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        for (int i = 0; i < frequenciesToRemove.length; i++) {
            for (int j = 0; j < analyser.getChannelList().size(); j++) {
                if (analyser.getChannelList().get(j).getFreq() == frequenciesToRemove[i]) {
                    Channel channel = analyser.getChannelList().get(j);
                    analyser.removeChannel(channel);

                    assertEquals(frequenciesToRemove.length - i - 1, analyser.getChannelList().size());
                    assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
                    TestHelpers.assertConflicts(analyser.getConflictList(),
                            conflicts[i],
                            channelConflicts[i],
                            im2t3oConflicts[i],
                            im2t5oConflicts[i],
                            im2t7oConflicts[i],
                            im2t9oConflicts[i],
                            im3t3oConflicts[i]);
                    TestHelpers.assertIsSorted(analyser.getIntermodList());
                    break;
                }
            }
        }
    }

    @DisplayName("generate valid analysis from updates")
    @Test
    final void testGenerateValidAnalysisFromUpdates() throws InvalidFrequencyException{
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                470.05, 470.6, 471.35, 471.95, 472.35, 474.8,
                475.25, 475.6, 476.75, 477.05, 478.0
        ));

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        List<Double> newFrequencies = new ArrayList<>(Arrays.asList(
                578.250, 590.500, 563.100, 562.250, 575.250, 577.075,
                581.000, 583.450, 577.825, 578.100, 592.250
        ));

        for (int i = 0; i < analyser.getChannelList().size(); i++) {
            Channel channelToUpdate = analyser.getChannelList().get(0);

            channelToUpdate.setFreq(newFrequencies.get(i));
            analyser.updateChannel(channelToUpdate);

            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        int numConflicts = 11;
        int numChannelConflicts = 4;
        int num2T3OConflicts = 2;
        int num2T5OConflicts = 1;
        int num2T7OConflicts = 0;
        int num2T9OConflicts = 0;
        int num3T3OConflicts = 4;

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        TestHelpers.assertConflicts(analyser.getConflictList(),
                numConflicts,
                numChannelConflicts,
                num2T3OConflicts,
                num2T5OConflicts,
                num2T7OConflicts,
                num2T9OConflicts,
                num3T3OConflicts);
    }

    @DisplayName("generate analysis with different equipment types")
    @Test
    final void testGenerateAnalysisWithDifferentEquipment() throws InvalidFrequencyException {
        Equipment ias = equipmentProfiles.get(0);
        Equipment sennheiser = equipmentProfiles.get(2);
        Equipment uhfr = equipmentProfiles.get(10);

        analyser.addChannel(new Channel(null, 578, sennheiser));
        analyser.addChannel(new Channel(null, 590.2, sennheiser));
        analyser.addChannel(new Channel(null, 584.65, sennheiser));
        analyser.addChannel(new Channel(null, 589.225, sennheiser));
        analyser.addChannel(new Channel(null, 578.650, sennheiser));
        analyser.addChannel(new Channel(null, 583.475, uhfr));
        analyser.addChannel(new Channel(null, 587.05, uhfr));
        analyser.addChannel(new Channel(null, 588.75, uhfr));
        analyser.addChannel(new Channel(null, 590.95, uhfr));
        analyser.addChannel(new Channel(null, 591.9, uhfr));
        analyser.addChannel(new Channel(null, 578.425, ias));
        analyser.addChannel(new Channel(null, 590, ias));
        analyser.addChannel(new Channel(null, 587.8, ias));
        analyser.addChannel(new Channel(null, 585.95, ias));
        analyser.addChannel(new Channel(null, 591.45, ias));
        analyser.addChannel(new Channel(null, 585.225, ias));

        int numConflicts = 200;
        int numChannelConflicts = 6;
        int num2T3OConflicts = 21;
        int num2T5OConflicts = 5;
        int num2T7OConflicts = 0;
        int num2T9OConflicts = 0;
        int num3T3OConflicts = 168;

        TestHelpers.assertConflicts(analyser.getConflictList(),
                numConflicts,
                numChannelConflicts,
                num2T3OConflicts,
                num2T5OConflicts,
                num2T7OConflicts,
                num2T9OConflicts,
                num3T3OConflicts);
    }
}
