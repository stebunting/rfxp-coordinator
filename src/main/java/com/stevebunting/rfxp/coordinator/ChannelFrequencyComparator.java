package com.stevebunting.rfxp.coordinator;

import java.util.Comparator;

// Comparator to sort channels by ID
final class ChannelFrequencyComparator implements Comparator<Channel> {
    @Override
    public int compare(Channel a, Channel b) {
        return Integer.compare(a.getFreq(), b.getFreq());
    }
}
