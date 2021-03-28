package com.stevebunting.rfxp.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

@DisplayName("Frequency generator...")
class FrequencyGenerationTests {
    Coordination coordination;
    Equipment equipment;

    @BeforeEach
    final void setUp() {
        coordination = new Coordination();
        final Range range = new Range(606000, 614000, "GB");
        equipment = new Equipment("Test", "Equipment", 25, 300, 100, 90, 0, 0, 50, range);
    }

    @DisplayName("generates 2 valid frequencies")
    @Test
    final void testGenerate2Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(2, equipment, false);
        assertEquals(2, coordination.getChannels().length);
        assertEquals(2, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates 3 valid frequencies")
    @Test
    final void testGenerate3Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(3, equipment, false);
        assertEquals(3, coordination.getChannels().length);
        assertEquals(3, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates 4 valid frequencies")
    @Test
    final void testGenerate4Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(4, equipment, false);
        assertEquals(4, coordination.getChannels().length);
        assertEquals(4, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates 6 valid frequencies")
    @Test
    final void testGenerate6Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(6, equipment, false);
        assertEquals(6, coordination.getChannels().length);
        assertEquals(6, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates 11 valid frequencies in 1 TV Channel")
    @Test
    final void testGenerate11Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(11, equipment, false);
        assertEquals(11, coordination.getChannels().length);
        assertEquals(11, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates 21 valid frequencies in 606-648MHz")
    @Test
    final void testGenerate21Frequencies() throws InvalidFrequencyException {
        Range range = new Range(606000, 648000, "Higher Range");
        equipment.setRange(range);

        coordination.addNewChannels(21, equipment, false);
        assertEquals(21, coordination.getChannels().length);
        assertEquals(21, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with 2 existing channels")
    @Test
    final void testGenerateFrequenciesWith2ExistingChannels() throws InvalidFrequencyException {
        Range range = new Range(718000, 726000, "Higher Range");
        equipment.setRange(range);

        coordination.addChannel(720, equipment);
        coordination.addChannel(720.3, equipment);

        coordination.addNewChannels(9, equipment, false);
        assertEquals(11, coordination.getChannels().length);
        assertEquals(11, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with 3 existing channels")
    @Test
    final void testGenerateFrequenciesWith3ExistingChannels() throws InvalidFrequencyException {
        Range range = new Range(470000, 474000, "Higher Range");
        equipment.setRange(range);

        coordination.addChannel(470, equipment);
        coordination.addChannel(473.975, equipment);
        coordination.addChannel(471.775, equipment);

        coordination.addNewChannels(5, equipment, false);
        assertEquals(8, coordination.getChannels().length);
        assertEquals(8, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with existing coordination")
    @Test
    final void testGenerateFrequenciesWithExistingCoordination() throws InvalidFrequencyException {
        Equipment sennheiserIEM = EquipmentProfiles.INSTANCE.get("sennheiser", "2000 iem");
        Equipment shureUHFR = new Equipment("Shure", "UHF-R", 25, 325, 175, 0, 0, 0, 50, Equipment.FrontEndType.TRACKING, 25000);
        if (sennheiserIEM == null) {
            fail("Could not get equipment models from Equipment Profiles");
        }
        shureUHFR.setRange(new Range(606000, 666000, "K4E"));

        double[] frequencies = new double[]{
                516.150, 517.475, 522.725, 526.150, 530.200, 532.975, 534.500,
                535.050, 540.650, 546.475, 553.725, 554.475, 555.600
        };
        for (double frequency : frequencies) {
            coordination.addChannel(frequency, sennheiserIEM);
        }

        assertEquals(0, coordination.getNumConflicts());
        assertEquals(13, coordination.getNumChannels());

        coordination.addNewChannels(20, shureUHFR, true);
        Channel[] channels = coordination.getChannels();
        Arrays.sort(channels);
        System.out.println(Arrays.toString(channels));
        System.out.printf("FREQUENCIES FOUND: %d%n", coordination.getChannels().length);
        assertEquals(33, coordination.getChannels().length);
        assertEquals(33, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with existing bad coordination")
    @Test
    final void testGenerateFrequenciesWithExistingBadCoordination() throws InvalidFrequencyException {
        Equipment sennheiserIEM = EquipmentProfiles.INSTANCE.get("sennheiser", "2000 iem");
        Equipment shureUHFR = new Equipment("Shure", "UHF-R", 25, 325, 175, 0, 0, 0, 50, Equipment.FrontEndType.TRACKING, 25000);
        if (sennheiserIEM == null) {
            fail("Could not get equipment models from Equipment Profiles");
        }
        shureUHFR.setRange(new Range(606000, 666000, "K4E"));

        double[] frequencies = new double[]{
                718.175, 725.050, 745.650, 747.075, 755.100, 756.525,
                721.375, 722.300, 730.550, 740.400, 740.925, 768.625,
                774.350, 781.500, 783.125, 784.950, 787.450
        };
        for (double frequency : frequencies) {
            coordination.addChannel(frequency, sennheiserIEM);
        }

        assertEquals(4, coordination.getNumConflicts());
        assertEquals(4, coordination.getAnalyser().getNumIMConflicts(Intermod.Type.IM_3T3O));
        assertEquals(17, coordination.getNumChannels());
        assertEquals(13, coordination.getAnalyser().getValidChannels());

        coordination.addNewChannels(14, shureUHFR, false);
        assertEquals(31, coordination.getChannels().length);
        assertEquals(27, coordination.getAnalyser().getValidChannels());
        assertEquals(4, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(4, coordination.getAnalyser().getNumIMConflicts());
    }
}
