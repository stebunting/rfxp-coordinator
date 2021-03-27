package com.stevebunting.rfxp.coordinator;

import java.util.Comparator;

// Comparator to sort channels by Name
final class ChannelNameComparator implements Comparator<Channel> {
    @Override
    public int compare(Channel a, Channel b) {
        int result = a.getName().compareToIgnoreCase(b.getName());
        return result == 0 ? a.compareTo(b) : result;
    }
}
