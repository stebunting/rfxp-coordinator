package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;
import java.util.ArrayList;
import java.util.Collections;

@DisplayName("Coordination Class...")
class CoordinationTests {

    final EquipmentProfiles equipmentProfiles = EquipmentProfiles.INSTANCE;
    Coordination coordination;

    @DisplayName("contains methods that...")
    @Nested
    class CoordinationMethodTests {

        @BeforeEach
        void setUp() {
            coordination = new Coordination();
        }

        @DisplayName("add a single channel to the coordination")
        @Test
        final void testAddSingleChannel() throws InvalidFrequencyException {
            int newId = coordination.addChannel(560.500, equipmentProfiles.get(0));
            assertEquals(0, newId);
            assertEquals(1, coordination.getNumChannels());
            Channel addedChannel = coordination.getChannelById(0);
            assertEquals(560500, addedChannel.getFreq());
        }

        @DisplayName("throws an error when null equipment is passed to add channel method")
        @Test
        final void testAddSingleInvalidEquipmentChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    coordination.addChannel(600.0, null));
        }

        @DisplayName("throws an error when a single invalid channel is added to coordination")
        @Test
        final void testAddSingleInvalidChannel() {
            assertThrows(InvalidFrequencyException.class, () ->
                coordination.addChannel(442.002, equipmentProfiles.get(2)));
        }

        @DisplayName("remove a single channel from coordination")
        @Test
        final void testRemoveSingleChannel() throws InvalidFrequencyException {
            int newId = coordination.addChannel(560.500, equipmentProfiles.get(0));
            Channel removedChannel = coordination.removeChannel(newId);
            assertEquals(560500, removedChannel.getFreq());
            assertEquals("Generic IEM", removedChannel.getEquipment().toString());
        }

        @DisplayName("return null when trying to remove non-existent channel from coordination")
        @Test
        final void testRemoveNonExistentChannel() {
            Channel removedChannel = coordination.removeChannel(25);
            assertNull(removedChannel);
        }

        @DisplayName("return null when trying to get non-existent channel from coordination")
        @Test
        final void testGetNonExistentChannel() {
            Channel channel = coordination.getChannelById(1);
            assertNull(channel);
        }

        @DisplayName("add and remove multiple channels to coordination")
        @Test
        final void testAddMultipleChannels() throws InvalidFrequencyException {
            int ch1id = coordination.addChannel(760.525, equipmentProfiles.get(0));
            assertEquals(0, ch1id);
            assertEquals(1, coordination.getNumChannels());

            int ch2id = coordination.addChannel(420.425, equipmentProfiles.get(0));
            assertEquals(1, ch2id);
            assertEquals(2, coordination.getNumChannels());

            int ch3id = coordination.addChannel(600, equipmentProfiles.get(1));
            assertEquals(2, ch3id);
            assertEquals(3, coordination.getNumChannels());

            Channel removedChannel = coordination.removeChannel(ch2id);
            assertEquals(420425, removedChannel.getFreq());
            assertEquals("Channel 2", removedChannel.getName());
            assertEquals(2, coordination.getNumChannels());
        }

        @DisplayName("update channel frequency")
        @Test
        final void testUpdateChannelFrequency() throws InvalidFrequencyException {
            int ch1id = coordination.addChannel(869.200, equipmentProfiles.get(0));
            assertEquals(0, ch1id);
            assertEquals(1, coordination.getNumChannels());

            int ch2id = coordination.addChannel(420.425, equipmentProfiles.get(0));
            assertEquals(1, ch2id);
            assertEquals(2, coordination.getNumChannels());

            int ch3id = coordination.addChannel(600, equipmentProfiles.get(1));
            assertEquals(2, ch3id);
            assertEquals(3, coordination.getNumChannels());

            assertEquals(600000, coordination.getChannelById(ch3id).getFreq());
            coordination.updateChannel(2, 770.600);
            assertEquals(770600, coordination.getChannelById(ch3id).getFreq());
        }

        @DisplayName("do not update channel with invalid channel frequency")
        @Test
        final void testUpdateInvalidChannelFrequency() throws InvalidFrequencyException {
            Channel updatedChannel = coordination.updateChannel(3, 500.525);
            assertNull(updatedChannel);
        }

        @DisplayName("update channel name")
        @Test
        final void testUpdateName() throws InvalidFrequencyException {
            int ch1id = coordination.addChannel(256.500, equipmentProfiles.get(2));
            assertEquals("Channel 1", coordination.getChannelById(ch1id).getName());
            coordination.updateChannel(ch1id, "New Channel Name");
            assertEquals("New Channel Name", coordination.getChannelById(ch1id).getName());
        }

        @DisplayName("do not update channel with invalid channel name")
        @Test
        final void testUpdateInvalidChannelName() {
            Channel updatedChannel = coordination.updateChannel(0, "Invalid Channel");
            assertNull(updatedChannel);
        }

        @DisplayName("update channel equipment")
        @Test
        final void testUpdateEquipment() throws InvalidFrequencyException {
            int ch1id = coordination.addChannel(312.0, equipmentProfiles.get(3));
            assertSame(equipmentProfiles.get(3), coordination.getChannelById(ch1id).getEquipment());
            coordination.updateChannel(ch1id, equipmentProfiles.get(1));
            assertSame(equipmentProfiles.get(1), coordination.getChannelById(ch1id).getEquipment());
        }

        @DisplayName("do not update channel with invalid equipment")
        @Test
        final void testUpdateInvalidChannelEquipment() throws InvalidFrequencyException {
            Channel updatedChannel = coordination.updateChannel(-1, equipmentProfiles.get(4));
            assertNull(updatedChannel);
        }

        @DisplayName("get a plain array of channels")
        @Test
        final void testGetChannelArray() throws InvalidFrequencyException {
            Channel[] channelArray = coordination.getChannels();
            assertEquals(0, channelArray.length);

            coordination.addChannel(760.525, equipmentProfiles.get(0));
            coordination.addChannel(420.425, equipmentProfiles.get(0));
            coordination.addChannel(600, equipmentProfiles.get(1));
            channelArray = coordination.getChannels();
            assertEquals(3, channelArray.length);
            assertEquals(760525, channelArray[0].getFreq());
            assertEquals(420425, channelArray[1].getFreq());
            assertEquals(600000, channelArray[2].getFreq());
        }

        @DisplayName("throws an error when null equipment is passed to check channel method")
        @Test
        final void testCheckInvalidEquipmentChannel() {
            assertThrows(IllegalArgumentException.class, () ->
                    coordination.checkChannel(600.0, null));
        }

        @DisplayName("check a duplicate channel")
        @Test
        final void testCheckDuplicateChannel() throws InvalidFrequencyException {
            double[] frequencies = new double[]{ 780.125, 780.550, 780.900, 781.125, 781.375, 781.775 };
            for (int i = 0; i < frequencies.length; i++) {
                coordination.addChannel(frequencies[i], equipmentProfiles.get(0));
            }
            assertEquals(19, coordination.getNumConflicts());

            NewChannelReport channelReport = coordination.checkChannel(780.900, equipmentProfiles.get(3));
            assertEquals(true, channelReport.isDuplicate());
            assertEquals(Channel.Validity.INVALID, channelReport.isValid());
            assertEquals(10, channelReport.getConflicts());
        }

        @DisplayName("check a valid channel")
        @Test
        final void testCheckValidChannel() throws InvalidFrequencyException {
            double[] frequencies = new double[]{ 546.45, 582.625, 618.5, 629, 663.275, 790.575, 861.125 };
            for (int i = 0; i < frequencies.length; i++) {
                coordination.addChannel(frequencies[i], equipmentProfiles.get(0));
            }
            assertEquals(0, coordination.getNumConflicts());

            NewChannelReport channelReport = coordination.checkChannel(788.875, equipmentProfiles.get(3));
            assertEquals(false, channelReport.isDuplicate());
            assertEquals(Channel.Validity.VALID, channelReport.isValid());
            assertEquals(0, channelReport.getConflicts());
        }
    }

    @DisplayName("calculates intermodulations...")
    @Nested
    class IntermodulationCalculationTests {

        @BeforeEach
        void setUp() {
            coordination = new Coordination();
        }

        @DisplayName("when adding channels")
        @Test
        final void testCalculateAdditionOfIntermodulations() throws InvalidFrequencyException {
            coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(660.750, equipmentProfiles.get(0));
            assertEquals(8, coordination.getNumIntermods());

            coordination.addChannel(720.150, equipmentProfiles.get(0));
            assertEquals(27, coordination.getNumIntermods());
        }

        @DisplayName("that may be specified to exclude 3T3O")
        @Test
        final void testCalculateAdditionOfIntermodulationsNo3T3O() throws InvalidFrequencyException {
            coordination.setCalculate3t3o(false);

            coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(660.750, equipmentProfiles.get(0));
            assertEquals(8, coordination.getNumIntermods());

            coordination.addChannel(720.150, equipmentProfiles.get(0));
            assertEquals(24, coordination.getNumIntermods());
        }

        @DisplayName("that may be specified to exclude 2T9O")
        @Test
        final void testCalculateAdditionOfIntermodulationsNo2T9O() throws InvalidFrequencyException {
            coordination.setCalculate2t9o(false);

            coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(660.750, equipmentProfiles.get(0));
            assertEquals(6, coordination.getNumIntermods());

            coordination.addChannel(720.150, equipmentProfiles.get(0));
            assertEquals(21, coordination.getNumIntermods());
        }

        @DisplayName("that may be specified to exclude 2T7O/2T9O")
        @Test
        final void testCalculateAdditionOfIntermodulationsNo2T7O() throws InvalidFrequencyException {
            coordination.setCalculate2t9o(false);
            coordination.setCalculate2t7o(false);

            coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(660.750, equipmentProfiles.get(0));
            assertEquals(4, coordination.getNumIntermods());

            coordination.addChannel(720.150, equipmentProfiles.get(0));
            assertEquals(15, coordination.getNumIntermods());
        }

        @DisplayName("that may be specified to exclude 2T5O/2T7O/2T9O")
        @Test
        final void testCalculateAdditionOfIntermodulationsNo2T5O() throws InvalidFrequencyException {
            coordination.setCalculate2t5o(false);
            coordination.setCalculate2t9o(false);
            coordination.setCalculate2t7o(false);

            coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(660.750, equipmentProfiles.get(0));
            assertEquals(2, coordination.getNumIntermods());

            coordination.addChannel(720.150, equipmentProfiles.get(0));
            assertEquals(9, coordination.getNumIntermods());
        }

        @DisplayName("that may be specified to exclude all 2nd order products")
        @Test
        final void testCalculateAdditionOfIntermodulationsNo2ndOrder() throws InvalidFrequencyException {
            coordination.setCalculate2t3o(false);
            coordination.setCalculate2t5o(false);
            coordination.setCalculate2t9o(false);
            coordination.setCalculate2t7o(false);

            coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(660.750, equipmentProfiles.get(0));
            assertEquals(0, coordination.getNumIntermods());

            coordination.addChannel(720.150, equipmentProfiles.get(0));
            assertEquals(3, coordination.getNumIntermods());
        }

        @DisplayName("that are removed when removing channels")
        @Test
        final void testRemovalOfIntermodulations() throws InvalidFrequencyException {
            int id1 = coordination.addChannel(656.750, equipmentProfiles.get(0));
            int id2 = coordination.addChannel(656.750, equipmentProfiles.get(0));
            int id3 = coordination.addChannel(656.750, equipmentProfiles.get(0));
            int id4 = coordination.addChannel(656.750, equipmentProfiles.get(0));
            assertEquals(4, coordination.getNumChannels());
            assertEquals(60, coordination.getNumIntermods());

            coordination.removeChannel(id3);
            assertEquals(27, coordination.getNumIntermods());

            coordination.removeChannel(id2);
            assertEquals(8, coordination.getNumIntermods());

            coordination.removeChannel(id1);
            assertEquals(0, coordination.getNumIntermods());

            coordination.removeChannel(id4);
        }
    }

    @DisplayName("analyses coordination...")
    @Nested
    class CoordinationAnalysisTests {

        @DisplayName("for channel spacing conflicts...")
        @Nested
        class ChannelConflictAnalysisTests {

            final Equipment equipment = new Equipment("Test", "Device", 0, 1000, 1, 300, 100, 90, 0, 0, 50);

            @BeforeEach
            void setUp() {
                coordination = new Coordination();
                coordination.setCalculate2t3o(false);
                coordination.setCalculate2t5o(false);
                coordination.setCalculate2t7o(false);
                coordination.setCalculate2t9o(false);
                coordination.setCalculate3t3o(false);
            }

            @DisplayName("and generates 2 conflicts when 2 channels are too close")
            @Test
            final void testChannelSpacing() throws InvalidFrequencyException {
                int id1 = coordination.addChannel(500, equipment);
                assertEquals(0, coordination.getChannelById(id1).getNumConflicts());

                int id2 = coordination.addChannel(500.2, equipment);
                assertEquals(1, coordination.getChannelById(id1).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(id1).getValidity());
                assertEquals(1, coordination.getChannelById(id2).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(id2).getValidity());
                assertEquals(2, coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
                assertEquals(coordination.getNumConflicts(), coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
            }

            @DisplayName("and generates multiple conflicts when multiple channels are too close")
            @Test
            final void testMultipleChannelFailSpacing() throws InvalidFrequencyException {
                int id1 = coordination.addChannel(500, equipment);
                assertEquals(0, coordination.getChannelById(id1).getNumConflicts());

                coordination.addChannel(500.1, equipment);
                assertEquals(1, coordination.getChannelById(id1).getNumConflicts());

                coordination.addChannel(500.2, equipment);
                assertEquals(2, coordination.getChannelById(id1).getNumConflicts());

                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(id1).getValidity());
                assertEquals(2, coordination.getChannelById(id1).getNumConflicts());
                assertEquals(6, coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
                assertEquals(coordination.getNumConflicts(), coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
            }

            @DisplayName("and generates no conflicts when spacing is equal to specified channel spacing")
            @Test
            final void testMultipleChannelSpacing() throws InvalidFrequencyException {
                coordination.addChannel(400, equipment);
                int id1 = coordination.addChannel(500, equipment);
                coordination.addChannel(500.3, equipment);
                coordination.addChannel(500.6, equipment);
                coordination.addChannel(500.899, equipment);
                coordination.addChannel(501.2, equipment);
                assertEquals(Channel.Validity.VALID, coordination.getChannelById(id1).getValidity());
                assertEquals(2, coordination.getNumConflicts());
                assertEquals(coordination.getNumConflicts(), coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
            }

            @DisplayName("and removes conflicts when relevant channels removed")
            @Test
            final void testRemoveChannelConflicts() throws InvalidFrequencyException {
                coordination.addChannel(400, equipment);
                int id1 = coordination.addChannel(500, equipment);
                int id2 = coordination.addChannel(500.2, equipment);
                int id3 = coordination.addChannel(500.4, equipment);
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(id1).getValidity());
                assertEquals(1, coordination.getChannelById(id1).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(id2).getValidity());
                assertEquals(2, coordination.getChannelById(id2).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(id3).getValidity());
                assertEquals(1, coordination.getChannelById(id3).getNumConflicts());
                assertEquals(4, coordination.getNumConflicts());

                coordination.removeChannel(id2);
                assertEquals(0, coordination.getNumConflicts());

                System.out.println(coordination.getChannelById(id1).getNumConflicts());
                assertEquals(Channel.Validity.VALID, coordination.getChannelById(id1).getValidity());
                assertEquals(0, coordination.getChannelById(id1).getNumConflicts());
                assertEquals(Channel.Validity.VALID, coordination.getChannelById(id3).getValidity());
                assertEquals(0, coordination.getChannelById(id3).getNumConflicts());

            }
        }

        @DisplayName("for IM spacing conflicts...")
        @Nested
        class IMConflictAnalysisTests {

            final Equipment equipment = new Equipment("Test", "Device", 0, 1000, 25, 300, 100, 90, 0, 0, 50);

            @BeforeEach
            void setUp() {
                coordination = new Coordination();
            }

            @DisplayName("and generates IM conflicts when detected")
            @Test
            final void testIMSpacing() throws InvalidFrequencyException {
                int id1 = coordination.addChannel(500, equipment);
                coordination.addChannel(500.3, equipment);
                int id3 = coordination.addChannel(500.6, equipment);
                assertEquals(1, coordination.getChannelById(id1).getNumConflicts());
                assertEquals(1, coordination.getChannelById(id3).getNumConflicts());
                assertEquals(2, coordination.getNumConflicts());
            }

            @DisplayName("and generates correct number of conflicts test case")
            @Test
            final void testSpacingCase() throws InvalidFrequencyException {
                int[] idArray = new int[] {
                        coordination.addChannel(500, equipment),
                        coordination.addChannel(501, equipment),
                        coordination.addChannel(501.5, equipment),
                        coordination.addChannel(501.9, equipment),
                        coordination.addChannel(502.4, equipment),
                        coordination.addChannel(502.7, equipment),
                        coordination.addChannel(503.7, equipment),
                        coordination.addChannel(504, equipment),
                        coordination.addChannel(504.9, equipment),
                        coordination.addChannel(506, equipment)
                };
                assertEquals(29, coordination.getNumConflicts());
                assertEquals(29, coordination.getNumConflictsOfType(Conflict.Type.INTERMOD_SPACING));
                assertEquals(0, coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
                assertEquals(3, coordination.getChannelById(idArray[0]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[0]).getValidity());
                assertEquals(3, coordination.getChannelById(idArray[1]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[1]).getValidity());
                assertEquals(3, coordination.getChannelById(idArray[2]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[2]).getValidity());
                assertEquals(2, coordination.getChannelById(idArray[3]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[3]).getValidity());
                assertEquals(3, coordination.getChannelById(idArray[4]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[4]).getValidity());
                assertEquals(5, coordination.getChannelById(idArray[5]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[5]).getValidity());
                assertEquals(4, coordination.getChannelById(idArray[6]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[6]).getValidity());
                assertEquals(3, coordination.getChannelById(idArray[7]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[7]).getValidity());
                assertEquals(3, coordination.getChannelById(idArray[8]).getNumConflicts());
                assertEquals(Channel.Validity.INVALID, coordination.getChannelById(idArray[8]).getValidity());
                assertEquals(0, coordination.getChannelById(idArray[9]).getNumConflicts());
                assertEquals(Channel.Validity.VALID, coordination.getChannelById(idArray[9]).getValidity());

                coordination.removeChannel(idArray[5]);
                assertEquals(15, coordination.getNumConflicts());
                assertEquals(15, coordination.getNumConflictsOfType(Conflict.Type.INTERMOD_SPACING));
            }

            @DisplayName("ang generates no conflicts in passing test case (11 frequencies in 1 TV Ch)")
            @Test
            final void testPassingCase() throws InvalidFrequencyException {
                double[] frequencies = new double[]{
                    606.200, 606.600, 607.300, 608.175, 608.525, 609.350,
                    611.525, 611.825, 612.275, 613.275, 613.925
                };
                for (int i = 0; i < frequencies.length; i++) {
                    coordination.addChannel(frequencies[i], equipment);
                }
                assertEquals(0, coordination.getNumConflicts());
            }
        }
    }

    @DisplayName("performs multiple operations...")
    @Nested
    class CoordinationStressTests {

        private final EquipmentProfiles equipmentProfiles = EquipmentProfiles.INSTANCE;

        @BeforeEach
        void setUp() {
            coordination = new Coordination();
        }

        @DisplayName("with correct output")
        @Test
        final void testStress() throws InvalidFrequencyException {
            final int numTests = 60;
            int channelCounter = 0;
            final ArrayList<Integer> channelIds = new ArrayList<>();

            for (int i = 0; i < numTests; i++) {
                final int equipmentIndex = (int) Math.floor(Math.random() * equipmentProfiles.getCount());
                final Equipment equipment = equipmentProfiles.get(equipmentIndex);
                assertNotNull(equipment);
                final double frequency = Channel.kHzToMHz(TestHelpers.generateFrequency(500000, 538000, equipment.getTuningAccuracy()));

                // Add or remove a channel
                if (i % 4 != 2) {
                    try {
                        channelIds.add(coordination.addChannel(frequency, equipment));
                        channelCounter++;
                    } catch (Exception e) {
                        // Caught
                    }
                } else {
                    Collections.shuffle(channelIds);
                    final int idToDelete = channelIds.remove(0);
                    Channel removedChannel = coordination.removeChannel(idToDelete);
                    assertNotNull(removedChannel);
                    assertEquals(idToDelete, removedChannel.getId());
                    channelCounter--;
                }
                assertEquals(channelCounter, coordination.getNumChannels());
                assertEquals(TestHelpers.expectedIntermods(coordination.getAnalyser()), coordination.getNumIntermods());
            }

            // Duplicate array and check against original
            Coordination coordinationCheck = new Coordination();
            Channel[] channelArray = coordination.getChannels();
            for (Channel channel : channelArray) {
                coordinationCheck.addChannel(Channel.kHzToMHz(channel.getFreq()), channel.getEquipment());
            }
            assertEquals(coordination.getNumChannels(), coordinationCheck.getNumChannels());
            assertEquals(coordination.getNumIntermods(), coordinationCheck.getNumIntermods());

            // TODO This one is failing
            assertEquals(coordination.getNumConflicts(), coordinationCheck.getNumConflicts());

            assertEquals(coordination.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING), coordinationCheck.getNumConflictsOfType(Conflict.Type.CHANNEL_SPACING));
            assertEquals(coordination.getNumConflictsOfType(Conflict.Type.INTERMOD_SPACING), coordinationCheck.getNumConflictsOfType(Conflict.Type.INTERMOD_SPACING));

            Channel[] channelArrayCheck = coordinationCheck.getChannels();
            for (int i = 0; i < channelArray.length; i++) {
                assertEquals(channelArray[i].getFreq(), channelArrayCheck[i].getFreq());
                assertEquals(channelArray[i].getEquipment(), channelArrayCheck[i].getEquipment());
                assertEquals(channelArray[i].getValidity(), channelArrayCheck[i].getValidity());
                assertEquals(channelArray[i].getNumConflicts(), channelArrayCheck[i].getNumConflicts());
            }
        }
    }
}
