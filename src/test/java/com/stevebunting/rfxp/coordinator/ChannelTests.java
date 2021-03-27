package com.stevebunting.rfxp.coordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.*;

@DisplayName("Channel Class...")
class ChannelTests {

    @DisplayName("constructor...")
    @Nested
    class ChannelConstructionTests {

        final Equipment equipment = new Equipment("Sennheiser", "SR2050", 25, 300, 100, 90, 0, 0, 50);

        @DisplayName("generates object with integer id, frequency (in MHz) and equipment arguments")
        @Test
        final void testChannel() throws InvalidFrequencyException {
            Channel channel = new Channel(25, 500.450, equipment);
            assertEquals(25, channel.getId());
            assertEquals(500450, channel.getFreq());
            assertEquals("Channel 26", channel.getName());
            assertEquals(channel.getEquipment(), equipment);
            assertEquals(Channel.Validity.VALID, channel.getValidity());
        }

        @DisplayName("generates object with null id, frequency (in MHz) and equipment arguments")
        @Test
        final void testChannelWithNullId() throws InvalidFrequencyException {
            Channel channel = new Channel(null, 636.525, equipment);
            assertEquals(0, channel.getId());
            assertEquals("Channel 1", channel.getName());
        }

        @DisplayName("throws error on null equipment")
        @Test
        final void testChannelWithNullEquipment() {
            assertThrows(IllegalArgumentException.class,
                    () -> new Channel(10, 507.25, null));
        }

        @DisplayName("throws error on untunable frequency/equipment argument combination")
        @Test
        final void testChannelWithInvalidFrequency() {
            assertThrows(InvalidFrequencyException.class,
                    () -> new Channel(null, 501.01, equipment));
        }
    }

    @DisplayName("contains methods that...")
    @Nested
    class ChannelMethodTests {

        // Channel under test
        Channel channel;
        final Equipment equipment = new Equipment("Sennheiser", "SR2050", 25, 300, 100, 90, 0, 0, 50);

        @BeforeEach
        void setUp() throws InvalidFrequencyException {
            channel = new Channel(25, 500.450, equipment);
        }

        @DisplayName("set new frequency (in kHz)")
        @Test
        final void testSetFrequencyKHz() throws InvalidFrequencyException {
            channel.setFreq(670250);
            assertEquals(670250, channel.getFreq());
        }

        @DisplayName("throw error when trying to set invalid frequency/equipment combination (in kHz)")
        @Test
        final void testSetInvalidFrequencyKHz() {
            assertThrows(InvalidFrequencyException.class,
                    () -> channel.setFreq(670253));
        }

        @DisplayName("set new frequency (in MHz)")
        @Test
        final void testSetFrequencyMHz() throws InvalidFrequencyException {
            channel.setFreq(367.575);
            assertEquals(367575, channel.getFreq());
        }

        @DisplayName("throw error when trying to set invalid frequency/equipment combination (in MHz)")
        @Test
        final void testSetInvalidFrequencyMHz() {
            assertThrows(InvalidFrequencyException.class,
                    () -> channel.setFreq(1.054));
        }

        @DisplayName("set new name")
        @Test
        final void testSetName() {
            channel.setName("New Name");
            assertEquals("New Name", channel.getName());
        }

        @DisplayName("set new equipment")
        @Test
        final void testSetEquipment() throws InvalidFrequencyException {
            Equipment newEquipment = new Equipment("Shure", "PSM900", 50, 500, 200, 100, 50, 40, 80);
            channel.setEquipment(newEquipment);
            assertEquals(channel.getEquipment(), newEquipment);
            assertNotEquals(channel.getEquipment(), equipment);
        }

        @DisplayName("throw when setting new equipment as null")
        @Test
        final void testSetEquipmentAsNull() {
            assertThrows(IllegalArgumentException.class,
                    () -> channel.setEquipment(null));
        }

        @DisplayName("do not set null equipment")
        @Test
        final void testSetNullEquipment() {
            assertThrows(IllegalArgumentException.class, () ->
                channel.setEquipment(null));
        }

        @DisplayName("throw error when trying to set invalid equipment/frequency combination")
        @Test
        final void testSetInvalidEquipment() {
            Equipment newEquipment = new Equipment("Shure", "PSM900", 1000, 500, 200, 100, 50, 40, 80);
            assertThrows(InvalidFrequencyException.class,
                    () -> channel.setEquipment(newEquipment));
        }

        @DisplayName("add new channel spacing conflict")
        @Test
        final void testAddNewConflict() throws InvalidFrequencyException {
            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);
            assertEquals(1, channel.getNumConflicts());
        }

        @DisplayName("do not add null conflict")
        @Test
        final void testAddNullConflict() {
            assertEquals(0, channel.getNumConflicts());
            channel.addConflict(null);
            assertEquals(0, channel.getNumConflicts());
        }

        @DisplayName("set validity in INVALID on channel spacing conflict")
        @Test
        final void testUpdateValidityOnNewConflict() throws InvalidFrequencyException {
            assertEquals(Channel.Validity.VALID, channel.getValidity());
            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);
            assertEquals(Channel.Validity.INVALID, channel.getValidity());
        }

        @DisplayName("adds new whitespace conflict")
        @Test
        final void testAddNewWhitespaceConflict() {
            channel.addConflict(new Conflict(channel));
            assertEquals(1, channel.getNumConflicts());
        }

        @DisplayName("set validity to WARNING on new whitespace conflict")
        @Test
        final void testUpdateValidityOnNewWhitespaceConflict() {
            assertEquals(Channel.Validity.VALID, channel.getValidity());
            channel.addConflict(new Conflict(channel));
            assertEquals(Channel.Validity.WARNING, channel.getValidity());
        }

        @DisplayName("add multiple conflicts")
        @Test
        final void testAddMultipleConflict() throws InvalidFrequencyException {
            Conflict whiteSpaceConflict = new Conflict(channel);
            channel.addConflict(whiteSpaceConflict);
            assertEquals(Channel.Validity.WARNING, channel.getValidity());
            assertEquals(1, channel.getNumConflicts());

            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);
            assertEquals(Channel.Validity.INVALID, channel.getValidity());
            assertEquals(2, channel.getNumConflicts());
        }

        @DisplayName("remove specific conflict")
        @Test
        final void testRemoveConflicts() throws InvalidFrequencyException {
            Conflict whiteSpaceConflict = new Conflict(channel);
            channel.addConflict(whiteSpaceConflict);

            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);

            assertEquals(2, channel.getNumConflicts());

            assertTrue(channel.removeConflict(conflict));
            assertEquals(1, channel.getNumConflicts());
        }

        @DisplayName("do not remove any null conflicts")
        @Test
        final void testRemoveNullConflict() {
            Conflict whiteSpaceConflict = new Conflict(channel);
            channel.addConflict(whiteSpaceConflict);
            assertEquals(1, channel.getNumConflicts());

            assertFalse(channel.removeConflict(null));
            assertEquals(1, channel.getNumConflicts());
        }

        @DisplayName("update validity when removing specific conflict")
        @Test
        final void testUpdateValidityOnRemoveConflicts() throws InvalidFrequencyException {
            Conflict whiteSpaceConflict = new Conflict(channel);
            channel.addConflict(whiteSpaceConflict);
            assertEquals(Channel.Validity.WARNING, channel.getValidity());

            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);
            assertEquals(Channel.Validity.INVALID, channel.getValidity());

            channel.removeConflict(conflict);
            assertEquals(Channel.Validity.WARNING, channel.getValidity());

            channel.removeConflict(whiteSpaceConflict);
            assertEquals(Channel.Validity.VALID, channel.getValidity());
        }

        @DisplayName("clear all conflicts")
        @Test
        final void testClearConflicts() throws InvalidFrequencyException {
            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);
            assertEquals(1, channel.getNumConflicts());

            channel.clearConflicts();
            assertEquals(0, channel.getNumConflicts());
        }

        @DisplayName("reset validity when clearing all conflicts")
        @Test
        final void testUpdateValidityOnClearConflicts() throws InvalidFrequencyException {
            assertEquals(Channel.Validity.VALID, channel.getValidity());

            Channel conflictChannel = new Channel(null, 500.600, equipment);
            Conflict conflict = new Conflict(channel, conflictChannel);
            channel.addConflict(conflict);
            assertEquals(Channel.Validity.INVALID, channel.getValidity());

            channel.clearConflicts();
            assertEquals(Channel.Validity.VALID, channel.getValidity());
        }

        @DisplayName("measure equality for each component between 2 channel objects")
        @Test
        final void testChannelEquality() throws InvalidFrequencyException {
            Channel comparisonChannel = new Channel(25, 500.450, equipment);
            assertEquals(channel, comparisonChannel);

            assertNotEquals(channel, null);
            assertEquals(channel, channel);

            comparisonChannel = new Channel(24, 500.450, equipment);
            assertNotEquals(channel, comparisonChannel);

            comparisonChannel = new Channel(25, 503.450, equipment);
            assertNotEquals(channel, comparisonChannel);

            comparisonChannel = new Channel(25, 500.450, new Equipment("Test", "Equipment", 1, 0, 0, 0, 0, 0, 0));
            assertNotEquals(channel, comparisonChannel);
        }

        @DisplayName("sort channels naturally (by frequency)")
        @Test
        final void testChannelSorting() throws InvalidFrequencyException {
            Channel higherChannel = new Channel(null, 600.250, equipment);
            Channel lowerChannel = new Channel(null, 225.500, equipment);
            Channel equalChannel = new Channel(null, 500.450, equipment);
            assertTrue(channel.compareTo(higherChannel) < 0);
            assertTrue(channel.compareTo(lowerChannel) > 0);
            assertEquals(0, channel.compareTo(equalChannel));
        }

        @DisplayName("sort channels by ID")
        @Test
        final void testChannelSortById() throws InvalidFrequencyException {
            Comparator<Channel> sortByID = new ChannelIDComparator();

            Channel higherChannel = new Channel(65, 100.0, equipment);
            Channel lowerChannel = new Channel(null, 0, equipment);
            Channel equalChannel = new Channel(25, 600.250, equipment);
            Channel strictEqualChannel = new Channel(25, 500.450, equipment);
            assertTrue(sortByID.compare(channel, higherChannel) < 0);
            assertTrue(sortByID.compare(channel, lowerChannel) > 0);
            assertTrue(sortByID.compare(channel, equalChannel) < 0);
            assertEquals(0, sortByID.compare(channel, strictEqualChannel));
        }

        @DisplayName("sort channels by name")
        @Test
        final void testChannelSortByName() throws InvalidFrequencyException {
            Comparator<Channel> sortByName = new ChannelNameComparator();

            Channel higherChannel = new Channel(null, 250, equipment);
            Channel lowerChannel = new Channel(null, 250, equipment);
            Channel equalChannel = new Channel(null, 250, equipment);
            Channel strictEqualChannel = new Channel(25, 500.450, equipment);
            channel.setName("Main");
            higherChannel.setName("n comes after m");
            lowerChannel.setName("mab comes before Mai");
            equalChannel.setName("Main");
            strictEqualChannel.setName("Main");
            assertTrue(sortByName.compare(channel, higherChannel) < 0);
            assertTrue(sortByName.compare(channel, lowerChannel) > 0);
            assertTrue(sortByName.compare(channel, equalChannel) > 0);
            assertEquals(0, sortByName.compare(channel, strictEqualChannel));
        }

        @DisplayName("get correctly formatted Channel string (XXX.XXX MHz)")
        @Test
        final void testChannelString() throws InvalidFrequencyException {
            Channel newChannel = new Channel(0, 500.550, equipment);
            assertEquals("500.550", newChannel.toString());
            assertEquals("500.550 MHz", newChannel.toStringWithMHz());

            newChannel = new Channel(0, 3, equipment);
            assertEquals("3.000", newChannel.toString());
            assertEquals("3.000 MHz", newChannel.toStringWithMHz());

            newChannel = new Channel(0, 895.82536452123, equipment);
            assertEquals("895.825", newChannel.toString());
            assertEquals("895.825 MHz", newChannel.toStringWithMHz());
        }

        @DisplayName("create a deep copy")
        @Test
        final void testChannelDeepCopy() throws InvalidFrequencyException {
            Channel channel = new Channel(87, 546.75, "Channel to Copy", equipment);
            Channel channelCopy = channel.deepCopy();

            assertEquals(channel, channelCopy);
            assertNotSame(channel, channelCopy);
            assertEquals(channel.getId(), channelCopy.getId());
            assertEquals(channel.getFreq(), channelCopy.getFreq());
            assertEquals(channel.getName(), channelCopy.getName());
            assertSame(channel.getName(), channelCopy.getName());
            assertEquals(channel.getEquipment(), channelCopy.getEquipment());
            assertSame(channel.getEquipment(), channelCopy.getEquipment());
            assertEquals(channel.getNumConflicts(), channelCopy.getNumConflicts());
            assertEquals(channel.getValidity(), channelCopy.getValidity());
        }
    }

    @DisplayName("can be sorted...")
    @Nested
    public class ChannelSortingTests {

        final Equipment equipment = new Equipment("Test", "Equipment", 25, 300, 100, 90, 0, 0, 50);
        ArrayList<Channel> channelList;
        Channel channel1;
        Channel channel2;
        Channel channel3;
        Channel channel4;
        Channel channel5;

        @BeforeEach
        void setUp() throws InvalidFrequencyException {
            channel1 = new Channel(9, 600, equipment);
            channel1.setName("a");
            channel2 = new Channel(45, 700, equipment);
            channel2.setName("e");
            channel3 = new Channel(44, 200, equipment);
            channel3.setName("b");
            channel4 = new Channel(87, 900, equipment);
            channel4.setName("c");
            channel5 = new Channel(6, 500, equipment);
            channel5.setName("d");
            channelList = new ArrayList<>(Arrays.asList(channel1, channel2, channel3, channel4, channel5));
        }

        @DisplayName("naturally by frequency")
        @Test
        final void testSortChannelListByFrequency()  {
            Collections.sort(channelList);
            assertSame(channel3, channelList.get(0));
            assertSame(channel5, channelList.get(1));
            assertSame(channel1, channelList.get(2));
            assertSame(channel2, channelList.get(3));
            assertSame(channel4, channelList.get(4));
        }

        @DisplayName("by ID")
        @Test
        final void testSortChannelListByID() {
            channelList.sort(new ChannelIDComparator());
            assertSame(channel5, channelList.get(0));
            assertSame(channel1, channelList.get(1));
            assertSame(channel3, channelList.get(2));
            assertSame(channel2, channelList.get(3));
            assertSame(channel4, channelList.get(4));
        }

        @DisplayName("by ID, then frequency")
        @Test
        final void testSortChannelListByIDThenFrequency() throws InvalidFrequencyException {
            channel1 = new Channel(null, 600, equipment);
            channel2 = new Channel(null, 700, equipment);
            channel3 = new Channel(null, 200, equipment);
            channel4 = new Channel(null, 900, equipment);
            channel5 = new Channel(null, 500, equipment);
            channelList = new ArrayList<>(Arrays.asList(channel1, channel2, channel3, channel4, channel5));
            channelList.sort(new ChannelIDComparator());
            assertSame(channel3, channelList.get(0));
            assertSame(channel5, channelList.get(1));
            assertSame(channel1, channelList.get(2));
            assertSame(channel2, channelList.get(3));
            assertSame(channel4, channelList.get(4));
        }

        @DisplayName("by name")
        @Test
        final void testSortChannelListByName() {
            channelList.sort(new ChannelNameComparator());
            assertSame(channel1, channelList.get(0));
            assertSame(channel3, channelList.get(1));
            assertSame(channel4, channelList.get(2));
            assertSame(channel5, channelList.get(3));
            assertSame(channel2, channelList.get(4));
        }

        @DisplayName("by name, then frequency")
        @Test
        final void testSortChannelListByNameThenFrequency() {
            channelList.forEach(channel -> channel.setName("Name"));
            channelList.sort(new ChannelNameComparator());
            assertSame(channel3, channelList.get(0));
            assertSame(channel5, channelList.get(1));
            assertSame(channel1, channelList.get(2));
            assertSame(channel2, channelList.get(3));
            assertSame(channel4, channelList.get(4));
        }
    }

    @DisplayName("contains static helper methods that...")
    @Nested
    class ChannelHelperTests {
        @DisplayName("convert frequency in MHz to kHz")
        @Test
        final void testMHzToKHz() {
            assertEquals(500550, Channel.mHzToKHz(500.550));
            assertEquals(636200, Channel.mHzToKHz(636.2));
            assertEquals(780559, Channel.mHzToKHz(780.558999273947234));
            assertEquals(100000, Channel.mHzToKHz(100));
            assertEquals(-689245, Channel.mHzToKHz(-689.245));
        }

        @DisplayName("convert frequency in kHz to MHz")
        @Test
        final void testKHzToMHz() {
            assertEquals(640.240, Channel.kHzToMHz(640240));
            assertEquals(489.124, Channel.kHzToMHz(489124));
            assertEquals(224.009, Channel.kHzToMHz(224009));
            assertEquals(0.0, Channel.kHzToMHz(0));
            assertEquals(-0.789, Channel.kHzToMHz(-789));
        }

        @DisplayName("convert kHz <--> MHz without losing information")
        @Test
        final void testTwoWayConversions() {
            final int numTests = 1000;
            for (int i = 0; i < numTests; i++) {
                int freq = TestHelpers.generateFrequency(470000, 800000, 1);
                assertEquals(freq, Channel.mHzToKHz(Channel.kHzToMHz(freq)));
            }
            for (int i = 0; i < numTests; i++) {
                double freq = Channel.kHzToMHz(TestHelpers.generateFrequency(470000, 800000, 1));
                assertEquals(freq, Channel.kHzToMHz(Channel.mHzToKHz(freq)));
            }
        }
    }
}
