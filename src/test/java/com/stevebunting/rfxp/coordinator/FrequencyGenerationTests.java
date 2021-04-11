package com.stevebunting.rfxp.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Frequency generator...")
class FrequencyGenerationTests {
    Coordination coordination;
    Equipment equipment;
    Range range;

    @BeforeEach
    final void setUp() {
        coordination = new Coordination();
        range = new Range(606000, 614000, "GB");
        equipment = new Equipment("Test", "Equipment", 25, 300, 100, 90, 0, 0, 50, Equipment.FrontEndType.TRACKING, 100);
    }

    @DisplayName("throws on channel with no range")
    @Test
    final void testThrowsOnChannelWithNoRange() throws InvalidFrequencyException {
        final List<Channel> frequenciesToUpdate = new ArrayList<>();
        frequenciesToUpdate.add(new Channel(null, 687, equipment));

        assertThrows(ChannelMissingRangeException.class,
                () -> coordination.updateFrequencies(frequenciesToUpdate));
    }

    @DisplayName("generates 11 frequencies in 8MHz range")
    @Test
    final void testGenerates11FrequenciesIn8MHz() throws InvalidFrequencyException, ChannelMissingRangeException {
        final Equipment uhfr = new Equipment("Shure", "UHF-R", 25, 325, 175, 0, 0, 0, 50, Equipment.FrontEndType.TRACKING, 25, new Range[]{new Range(606000, 614000, "Channel 38")});

        final List<Channel> frequenciesToUpdate = new ArrayList<>();

        for (int i = 0; i < 11; i++) {
            int id = coordination.addChannel(606, uhfr);
            Channel channel = coordination.getChannelById(id);
            List<Range> ranges = channel.getAssignableRanges();
            channel.setRange(ranges.get(0));
            frequenciesToUpdate.add(channel);
        }

        assertTrue(coordination.getNumConflicts() > 0);
        assertEquals(0, coordination.getAnalyser().getValidChannels());

        coordination.updateFrequencies(frequenciesToUpdate);
        System.out.println(coordination);

        assertEquals(0, coordination.getNumConflicts());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(11, coordination.getAnalyser().getValidChannels());
    }

    @DisplayName("generates 21 valid frequencies in 606-648MHz")
    @Test
    final void testGenerate21Frequencies() throws InvalidFrequencyException, ChannelMissingRangeException {
        final Range range = new Range(606000, 648000, "Higher Range");
        final Equipment uhfr = new Equipment("Shure", "UHF-R", 25, 325, 175, 0, 0, 0, 50, Equipment.FrontEndType.TRACKING, 25, new Range[]{range});

        final List<Channel> frequenciesToUpdate = new ArrayList<>();

        for (int i = 0; i < 21; i++) {
            int id = coordination.addChannel(606, uhfr);
            Channel channel = coordination.getChannelById(id);
            channel.setRange(range);
            frequenciesToUpdate.add(channel);
        }

        coordination.updateFrequencies(frequenciesToUpdate);

        assertEquals(21, coordination.getChannels().length);
        assertEquals(21, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with 3 existing channels")
    @Test
    @Disabled
    final void testGenerateFrequenciesWith3ExistingChannels() throws InvalidFrequencyException, ChannelMissingRangeException {
        coordination.addChannel(470, equipment);
        coordination.addChannel(473.975, equipment);
        coordination.addChannel(471.775, equipment);

        Range range = new Range(470000, 474000, "Higher Range");
        final Equipment uhfr = new Equipment("Shure", "UHF-R", 25, 325, 175, 0, 0, 0, 50, Equipment.FrontEndType.TRACKING, 25, new Range[]{range});

        final List<Channel> frequenciesToUpdate = new ArrayList<>();

        for (int i = 0; i < 5; i++) {
            int id = coordination.addChannel(718, uhfr);
            Channel channel = coordination.getChannelById(id);
            channel.setRange(range);
            frequenciesToUpdate.add(channel);
        }

        coordination.updateFrequencies(frequenciesToUpdate);

        assertEquals(8, coordination.getChannels().length);
        assertEquals(8, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with existing coordination")
    @Test
    @Disabled
    final void testGenerateFrequenciesWithExistingCoordination() throws InvalidFrequencyException, ChannelMissingRangeException {
        Equipment sennheiserIEM = EquipmentProfiles.INSTANCE.get("sennheiser", "2000 iem");
        Equipment shureUHFR = EquipmentProfiles.INSTANCE.get("shure", "uhf-r");
        if (sennheiserIEM == null || shureUHFR == null) {
            fail("Could not get equipment models from Equipment Profiles");
        }
        final Range k4e = shureUHFR.getRanges()[3];
        assertEquals("K4E", k4e.getName());

        double[] frequencies = new double[]{
                516.150, 517.475, 522.725, 526.150, 530.200, 532.975, 534.500,
                535.050, 540.650, 546.475, 553.725, 554.475, 555.600
        };
        for (double frequency : frequencies) {
            coordination.addChannel(frequency, sennheiserIEM);
        }

        assertEquals(0, coordination.getNumConflicts());
        assertEquals(13, coordination.getNumChannels());

        final List<Channel> frequenciesToUpdate = new ArrayList<>();

        for (int i = 0; i < 20; i++) {
            int id = coordination.addChannel(
                    Channel.khzToMhz(TestHelpers.generateFrequency(607000, 665000, 25)),
                    shureUHFR);
            Channel channel = coordination.getChannelById(id);
            channel.setRange(k4e);
            frequenciesToUpdate.add(channel);
        }

        coordination.updateFrequencies(frequenciesToUpdate);
        assertEquals(33, coordination.getChannels().length);
        assertEquals(33, coordination.getAnalyser().getValidChannels());
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(0, coordination.getAnalyser().getNumIMConflicts());
    }

    @DisplayName("generates valid frequencies with existing bad coordination")
    @Test
    @Disabled
    final void testGenerateFrequenciesWithExistingBadCoordination() throws InvalidFrequencyException, ChannelMissingRangeException {
        Equipment sennheiserIEM = EquipmentProfiles.INSTANCE.get("sennheiser", "2000 iem");
        Equipment shureUHFR = EquipmentProfiles.INSTANCE.get("shure", "uhf-r");
        if (sennheiserIEM == null || shureUHFR == null) {
            fail("Could not get equipment models from Equipment Profiles");
        }
        final Range k4e = shureUHFR.getRanges()[3];
        assertEquals("K4E", k4e.getName());

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

        final List<Channel> frequenciesToUpdate = new ArrayList<>();

        for (int i = 0; i < 14; i++) {
            int id = coordination.addChannel(
                    Channel.khzToMhz(TestHelpers.generateFrequency(607000, 665000, 25)),
                    shureUHFR);
            Channel channel = coordination.getChannelById(id);
            channel.setRange(k4e);
            frequenciesToUpdate.add(channel);
        }

        coordination.updateFrequencies(frequenciesToUpdate);
        assertEquals(31, coordination.getChannels().length);
        assertEquals(27, coordination.getAnalyser().getValidChannels());
        assertEquals(4, coordination.getAnalyser().getConflictList().size());
        assertEquals(0, coordination.getAnalyser().getNumChannelConflicts());
        assertEquals(4, coordination.getAnalyser().getNumIMConflicts());
    }
}
