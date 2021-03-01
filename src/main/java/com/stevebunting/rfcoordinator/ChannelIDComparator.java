package com.stevebunting.rfcoordinator;

import java.util.Comparator;

// Comparator to sort channels by ID
final class ChannelIDComparator implements Comparator<Channel> {
    @Override
    public int compare(Channel a, Channel b) {
        int result = Integer.compare(a.getId(), b.getId());
        return result == 0 ? a.compareTo(b) : result;
    }
}
