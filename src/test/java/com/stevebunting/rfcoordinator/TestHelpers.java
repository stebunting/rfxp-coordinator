package com.stevebunting.rfcoordinator;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

import java.util.List;

class TestHelpers {
    // Generate a random integer within start and end bounds
    static int generateFrequency(int start, int end, int accuracy) {
        return start + (accuracy * ((int) (Math.random() * ((end - start) / accuracy))));
    }

    @Test
    final void testGenerateFrequency() {
        for (int i = 0; i <= 1000; i++) {
            int frequency = generateFrequency(470000, 900000, 25);
            assertTrue(frequency >= 470000);
            assertTrue(frequency <= 900000);
            assertEquals(0, frequency % 25);
        }
    }

    static <E extends Comparable<E>> boolean isSorted(List<E> list) {
        final int listSize = list.size();
        for (int i = 0; i < listSize - 1; i++) {
            if (list.get(i).compareTo(list.get(i + 1)) > 0) {
                return false;
            }
        }
        return true;
    }

    // Function to calculate number of expected intermodulations from a number of channels
    static int expectedIntermods(Analyser analyser) {
        int numChannels = analyser.getChannelList().size();
        final int secondOrderMultiplier = 4
                - (analyser.getCalculations().im2t3o ? 0 : 1)
                - (analyser.getCalculations().im2t5o ? 0 : 1)
                - (analyser.getCalculations().im2t7o ? 0 : 1)
                - (analyser.getCalculations().im2t9o ? 0 : 1);
        final int thirdOrderMultiplier = 1 - (analyser.getCalculations().im3t3o ? 0 : 1);
        final int secondOrder = numChannels * (numChannels - 1) * secondOrderMultiplier;
        final int thirdOrder = numChannels * (numChannels - 1) * (numChannels - 2) / 2 * thirdOrderMultiplier;
        return secondOrder + thirdOrder;
    }

    interface Predicate<T> {
        boolean test(T t);
    }

    static <T> int count(List<T> list, Predicate<T> predicate) {
        int count = 0;
        for (int i = 0; i < list.size(); i++) {
            if (predicate.test(list.get(i))) {
                count++;
            }
        }
        return count;
    }

    // Test Analyser Numbers Add Up
    static void assertConflicts(
            List<Conflict> conflicts,
            int numConflicts,
            int numChannelConflicts,
            int num2T3OConflicts,
            int num2T5OConflicts,
            int num2T7OConflicts,
            int num2T9OConflicts,
            int num3T3OConflicts
    ) {
        assertEquals(numConflicts, conflicts.size());
        assertEquals(numChannelConflicts, count(conflicts, conflict -> {
            return conflict.getType() == Conflict.Type.CHANNEL_SPACING;
        }));
        assertEquals(num2T3OConflicts, count(conflicts, conflict -> {
            return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                    && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T3O;
        }));
        assertEquals(num2T5OConflicts, count(conflicts, conflict -> {
            return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                    && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T5O;
        }));
        assertEquals(num2T7OConflicts, count(conflicts, conflict -> {
            return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                    && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T7O;
        }));
        assertEquals(num2T9OConflicts, count(conflicts, conflict -> {
            return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                    && conflict.getConflictIntermod().getType() == Intermod.Type.IM_2T9O;
        }));
        assertEquals(num3T3OConflicts, count(conflicts, (Conflict conflict) -> {
            return conflict.getType() == Conflict.Type.INTERMOD_SPACING
                    && conflict.getConflictIntermod().getType() == Intermod.Type.IM_3T3O;
        }));
    }
}
