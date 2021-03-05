package com.stevebunting.rfcoordinator;

import org.jetbrains.annotations.NotNull;

final class Range {
    final private int lo;
    final private int hi;
    @NotNull final private String name;

    Range(final int lo, final int hi, @NotNull final String name) {
        this.lo = lo;
        this.hi = hi;
        this.name = name != null ? name : "";
    }

    final int getLo() {
        return lo;
    }

    final int getHi() {
        return hi;
    }

    final String getName() {
        return name;
    }
}
