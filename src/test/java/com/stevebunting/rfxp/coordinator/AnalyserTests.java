package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.*;

@DisplayName("Analysis methods...")
class AnalyserTests {

    Analyser analyser;
    final Equipment equipment = new Equipment("Test", "Equipment", 25, 300, 100, 90, 0, 0, 50);
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

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
            assertEquals(i + 1, analyser.getValidChannels());
        }

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        assertEquals(11, analyser.getValidChannels());
        assertEquals(0, analyser.getConflictList().size());
        assertEquals(0, analyser.getNumChannelConflicts());
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
    }

    @DisplayName("generate invalid analysis")
    @Test
    final void testGenerateInvalidAnalysis() throws InvalidFrequencyException {
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                606.0, 606.3, 606.775, 607.1, 607.525, 607.7, 608.175,
                608.35, 609.0, 610.3, 610.525, 611.0, 611.325
        ));
        Collections.shuffle(frequencies);

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        assertEquals(0, analyser.getValidChannels());
        assertEquals(112, analyser.getConflictList().size());
        assertEquals(6, analyser.getNumChannelConflicts());
        assertEquals(18, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
        assertEquals(20, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
        assertEquals(68, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
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

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        assertEquals(0, analyser.getValidChannels());
        assertEquals(61, analyser.getConflictList().size());
        assertEquals(4, analyser.getNumChannelConflicts());
        assertEquals(10, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
        assertEquals(3, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
        assertEquals(44, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));

        int[] frequenciesToRemove = new int[]{
                769100, 765800, 768275, 763975, 767975, 770575,
                771100, 770025, 765250, 764250, 768550, 766125};
        int[] conflicts = new int[]{44, 24, 10, 8, 2, 0, 0, 0, 0, 0, 0, 0};
        int[] channelConflicts = new int[]{4, 4, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t3oConflicts = new int[]{8, 8, 4, 4, 2, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t5oConflicts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t7oConflicts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im2t9oConflicts = new int[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] im3t3oConflicts = new int[]{32, 12, 4, 4, 0, 0, 0, 0, 0, 0, 0, 0};
        int[] validChannels = new int[]{0, 2, 2, 2, 5, 6, 5, 4, 3, 2, 1, 0};
        for (int i = 0; i < frequenciesToRemove.length; i++) {
            for (int j = 0; j < analyser.getChannelList().size(); j++) {
                if (analyser.getChannelList().get(j).getFreq() == frequenciesToRemove[i]) {
                    Channel channel = analyser.getChannelList().get(j);
                    analyser.removeChannel(channel);

                    assertEquals(validChannels[i], analyser.getValidChannels());
                    assertEquals(frequenciesToRemove.length - i - 1, analyser.getChannelList().size());
                    assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
                    assertEquals(conflicts[i], analyser.getConflictList().size());
                    assertEquals(channelConflicts[i], analyser.getNumChannelConflicts());
                    assertEquals(im2t3oConflicts[i], analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
                    assertEquals(im2t5oConflicts[i], analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
                    assertEquals(im2t7oConflicts[i], analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
                    assertEquals(im2t9oConflicts[i], analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
                    assertEquals(im3t3oConflicts[i], analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
                    TestHelpers.assertIsSorted(analyser.getIntermodList());
                    break;
                }
            }
        }
    }

    @DisplayName("generate valid analysis from updates")
    @Test
    final void testGenerateValidAnalysisFromUpdates() throws InvalidFrequencyException  {
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                470.05, 470.6, 471.35, 471.95, 472.35, 474.8,
                475.25, 475.6, 476.75, 477.05, 478.0
        ));

        for (int i = 0; i < frequencies.size(); i++) {
            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
            assertEquals(i + 1, analyser.getValidChannels());
            assertEquals(0, analyser.getConflictList().size());
            assertEquals(0, analyser.getNumChannelConflicts());
            assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
            assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
            assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
            assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
            assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
        }

        List<Double> newFrequencies = new ArrayList<>(Arrays.asList(
                578.250, 590.500, 563.100, 562.250, 575.250, 577.075,
                581.000, 583.450, 577.825, 578.100, 592.250
        ));
        int[] validChannels = new int[]{11, 11, 11, 11, 11, 4, 7, 11, 11, 6, 3};

        for (int i = 0; i < analyser.getChannelList().size(); i++) {
            Channel channelToUpdate = analyser.getChannelList().get(0);

            channelToUpdate.setFreq(newFrequencies.get(i));
            analyser.updateChannel(channelToUpdate);

            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
            assertEquals(validChannels[i], analyser.getValidChannels());
        }

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        assertEquals(11, analyser.getConflictList().size());
        assertEquals(4, analyser.getNumChannelConflicts());
        assertEquals(2, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
        assertEquals(1, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
        assertEquals(4, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
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

        assertEquals(0, analyser.getValidChannels());
        assertEquals(200, analyser.getConflictList().size());
        assertEquals(6, analyser.getNumChannelConflicts());
        assertEquals(21, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
        assertEquals(5, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
        assertEquals(0, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
        assertEquals(168, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
    }

    @DisplayName("generate analysis with checking")
    @Test
    final void testGenerateAnalysisWithChecking() throws InvalidFrequencyException {
        Equipment equipment = new Equipment("RFXp", "Equipment", 5, 500, 200, 100, 50, 25, 100);
        List<Double> frequencies = new ArrayList<>(Arrays.asList(
                720.560, 721.980, 723.605, 724.255, 724.785,
                726.025, 726.745, 727.255, 728.140, 729.185,
                729.765, 730.915, 732.195, 733.040, 734.500,
                735.410, 736.280, 737.505, 738.135, 738.735
        ));
        Collections.shuffle(frequencies);

        for (int i = 0; i < frequencies.size(); i++) {
            final int channelsBefore = analyser.getChannelList().size();
            final int intermodsBefore = analyser.getIntermodList().size();
            final int conflictsBefore = analyser.getConflictList().size();

            final int newConflicts = analyser.checkArtifacts(new Channel(null, frequencies.get(i), equipment));
            final int expectedConflicts = analyser.getConflictList().size() + newConflicts;

            assertEquals(channelsBefore, analyser.getChannelList().size());
            assertEquals(intermodsBefore, analyser.getIntermodList().size());
            assertEquals(conflictsBefore, analyser.getConflictList().size());

            analyser.addChannel(new Channel(null, frequencies.get(i), equipment));
            assertEquals(expectedConflicts, analyser.getConflictList().size());
            assertEquals(TestHelpers.expectedIntermods(analyser), analyser.getIntermodList().size());
            TestHelpers.assertIsSorted(analyser.getIntermodList());
        }

        assertEquals(frequencies.size(), analyser.getChannelList().size());
        assertEquals(540, analyser.getConflictList().size());
        assertEquals(0, analyser.getNumChannelConflicts());
        assertEquals(68, analyser.getNumIMConflicts(Intermod.Type.IM_2T3O));
        assertEquals(24, analyser.getNumIMConflicts(Intermod.Type.IM_2T5O));
        assertEquals(10, analyser.getNumIMConflicts(Intermod.Type.IM_2T7O));
        assertEquals(2, analyser.getNumIMConflicts(Intermod.Type.IM_2T9O));
        assertEquals(436, analyser.getNumIMConflicts(Intermod.Type.IM_3T3O));
    }
}
