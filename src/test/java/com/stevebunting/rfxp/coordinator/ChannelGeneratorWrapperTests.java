package com.stevebunting.rfxp.coordinator;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

@DisplayName("Frequency generator...")
public class ChannelGeneratorWrapperTests {

    @BeforeEach
    final void setUp() {
    }

    @DisplayName("gets max possible frequencies with standard range")
    @Test
    final void testGetsMaxPossibleFrequenciesWithStandardRange() throws InvalidFrequencyException {
        Range range = new Range(606000, 610000, "Range");
        Equipment equipment = new Equipment("Manufacturer", "Model", 25, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 0, new Range[]{range});
        Channel channel = new Channel(null, 606, equipment);
        channel.setRange(range);
        ChannelGeneratorWrapper channelGeneratorWrapper = new ChannelGeneratorWrapper(channel);
        assertEquals(161, channelGeneratorWrapper.getMaxPossibleFrequencies());
    }

    @DisplayName("gets max possible frequencies with range offset by 1")
    @Test
    final void testGetsMaxPossibleFrequenciesWithRangeOffsetBy1() throws InvalidFrequencyException {
        Range range = new Range(606000, 609999, "Range");
        Equipment equipment = new Equipment("Manufacturer", "Model", 25, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 0, new Range[]{range});
        Channel channel = new Channel(null, 606, equipment);
        channel.setRange(range);
        ChannelGeneratorWrapper channelGeneratorWrapper = new ChannelGeneratorWrapper(channel);
        assertEquals(160, channelGeneratorWrapper.getMaxPossibleFrequencies());
    }

    @DisplayName("gets max possible frequencies with offset range")
    @Test
    final void testGetsMaxPossibleFrequenciesWithOffsetRange() throws InvalidFrequencyException {
        Range range = new Range(606001, 606026, "Range");
        Equipment equipment = new Equipment("Manufacturer", "Model", 25, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 0, new Range[]{range});
        Channel channel = new Channel(null, 606, equipment);
        channel.setRange(range);
        ChannelGeneratorWrapper channelGeneratorWrapper = new ChannelGeneratorWrapper(channel);
        assertEquals(1, channelGeneratorWrapper.getMaxPossibleFrequencies());
    }

    @DisplayName("gets max possible frequencies with minimal range")
    @Test
    final void testGetsMaxPossibleFrequenciesWithMinimalRange() throws InvalidFrequencyException {
        Range range = new Range(606001, 606002, "Range");
        Equipment equipment = new Equipment("Manufacturer", "Model", 25, 0, 0, 0, 0, 0, 0, Equipment.FrontEndType.TRACKING, 0, new Range[]{range});
        Channel channel = new Channel(null, 606, equipment);
        channel.setRange(range);
        ChannelGeneratorWrapper channelGeneratorWrapper = new ChannelGeneratorWrapper(channel);
        assertEquals(0, channelGeneratorWrapper.getMaxPossibleFrequencies());
    }
}
