package com.stevebunting.rfxp.coordinator;

class ChannelMissingRangeException extends Exception {
    ChannelMissingRangeException() {
        super("Invalid Frequency for Selected Equipment");
    }
}
