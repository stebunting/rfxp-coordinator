package com.stevebunting.rfcoordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import java.util.Arrays;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class FrequencyGenerationTests {
    Coordination coordination;
    Equipment equipment;

    @BeforeEach
    final void setUp() {
        coordination = new Coordination();
        final Range range = new Range(606000, 614000, "GB");
        equipment = new Equipment("Test", "Equipment", 25, 300, 100, 90, 0, 0, 50, range);
    }

    @DisplayName("generates frequency list of 2")
    @Test
    final void testGenerate2Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(2, equipment, false);
        assertEquals(2, coordination.getChannels().length);
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
    }

    @DisplayName("generates frequency list of 3")
    @Test
    final void testGenerate3Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(3, equipment, false);
        assertEquals(3, coordination.getChannels().length);
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
    }

    @DisplayName("generates frequency list of 4")
    @Test
    final void testGenerate4Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(4, equipment, false);
        assertEquals(4, coordination.getChannels().length);
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
    }

    @DisplayName("generates frequency list of 6")
    @Test
    final void testGenerate6Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(6, equipment, false);
        assertEquals(6, coordination.getChannels().length);
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
    }

    @DisplayName("generates frequency list of 10")
    @Test
    final void testGenerate10Frequencies() throws InvalidFrequencyException {
        coordination.addNewChannels(10, equipment, false);
        assertEquals(10, coordination.getChannels().length);
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
    }

    @DisplayName("generates frequency list of 21 in 606-648MHz")
    @Test
    final void testGenerateFrequencies() throws InvalidFrequencyException {
        Range range = new Range(606000, 648000, "Higher Range");
        equipment.setRange(range);

        coordination.addNewChannels(21, equipment, true);
        Channel[] channels = coordination.getChannels();
        Arrays.sort(channels);
        System.out.println(Arrays.toString(channels));
        System.out.println(String.format("FREQUENCIES FOUND: %d", coordination.getChannels().length));
        assertEquals(21, coordination.getChannels().length);
        assertEquals(0, coordination.getAnalyser().getConflictList().size());
    }
}
