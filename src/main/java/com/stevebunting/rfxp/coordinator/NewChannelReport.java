package com.stevebunting.rfxp.coordinator;

import org.jetbrains.annotations.NotNull;

class NewChannelReport {
    final private int conflicts;
    final private Channel.Validity validity;
    final private boolean duplicate;

    NewChannelReport(
            final int conflicts,
            @NotNull final Channel.Validity validity,
            final boolean duplicate
    ) {
        this.conflicts = conflicts;
        this.validity = validity;
        this.duplicate = duplicate;
    }

    final int getConflicts() { return conflicts; }
    final Channel.Validity isValid() { return validity; }
    final boolean isDuplicate() { return duplicate; }
}
