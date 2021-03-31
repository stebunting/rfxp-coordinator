package com.stevebunting.rfxp.coordinator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

final class Range implements Comparable<Range> {
    final private int lo;
    final private int hi;
    @NotNull final private String name;

    @JsonCreator
    Range(@JsonProperty("lo") final int lo,
          @JsonProperty("hi") final int hi,
          @JsonProperty("name") @NotNull final String name) {
        this.lo = lo;
        this.hi = hi;
        this.name = name != null ? name : "";
    }

    @Override
    public int compareTo(@NotNull Range that) {
        if (this.lo != that.lo) {
            return Integer.compare(this.lo, that.lo);
        } else if (this.hi != that.hi) {
            return Integer.compare(this.hi, that.hi);
        } else return this.name.compareTo(that.getName());
    }

    final boolean isValidFrequency(final int frequency) {
        return frequency >= lo && frequency <= hi;
    }

    final int getLo() {
        return lo;
    }

    final int getHi() {
        return hi;
    }

    @NotNull
    final String getName() {
        return name;
    }

    @Override
    public String toString() {
        return String.format("%s (%s - %s)",
                name,
                Channel.getPrintableFrequency(lo),
                Channel.getPrintableFrequency(hi));
    }
}
