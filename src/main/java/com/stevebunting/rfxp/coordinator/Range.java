package com.stevebunting.rfxp.coordinator;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.jetbrains.annotations.NotNull;

final class Range {
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
}
