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
    static int expectedIntermods(final int numChannels, Analyser.Calculate calculations) {
        final int secondOrderMultiplier = 4 - (calculations.im2t3o ? 0 : 1)
                - (calculations.im2t5o ? 0 : 1)
                - (calculations.im2t7o ? 0 : 1)
                - (calculations.im2t9o ? 0 : 1);
        final int thirdOrderMultiplier = 1 - (calculations.im3t3o ? 0 : 1);
        final int secondOrder = numChannels * (numChannels - 1) * secondOrderMultiplier;
        final int thirdOrder = numChannels * (numChannels - 1) * (numChannels - 2) / 2 * thirdOrderMultiplier;
        return secondOrder + thirdOrder;
    }

    // Function to calculate number of expected intermodulations from a number of channels
    static int expectedIntermods(Coordination coordination) {
        final int numChannels = coordination.getNumChannels();
        final int secondOrderMultiplier = 4 - (coordination.getCalculate2t3o() ? 0 : 1)
                - (coordination.getCalculate2t5o() ? 0 : 1)
                - (coordination.getCalculate2t7o() ? 0 : 1)
                - (coordination.getCalculate2t9o() ? 0 : 1);
        final int thirdOrderMultiplier = 1 - (coordination.getCalculate3t3o() ? 0 : 1);
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
}
