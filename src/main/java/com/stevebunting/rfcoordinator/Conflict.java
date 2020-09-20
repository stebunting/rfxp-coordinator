package com.stevebunting.rfcoordinator;

import java.util.Locale;

class Conflict {

    // INSTANCE VARIABLES
    enum Type {
        CHANNEL_SPACING,
        INTERMOD_SPACING,
        WHITE_SPACE
    }

    // Locale for string formatting
    private final Locale locale = Locale.getDefault();

    // Channel object to store affected channel
    private final Channel channel;

    // Intermodulation object to store conflicting intermodulation
    private final Intermod conflictIntermod;

    // Channel object to store conflicting channel
    private final Channel conflictChannel;

    // Variable to store conflict type
    private final Type type;

    // CONSTRUCTORS
    // Constructor to create spacing conflict between two channels
    Conflict(Channel channel, Channel conflictChannel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel may not be null");
        }
        if (conflictChannel == null) {
            throw new IllegalArgumentException("Conflict Channel may not be null");
        }
        this.channel = channel;
        this.conflictChannel = conflictChannel;
        this.conflictIntermod = null;
        this.type = Type.CHANNEL_SPACING;
    }

    // Constructor to create spacing conflict between a channel and an intermodulation
    Conflict(Channel channel, Intermod conflictIntermod) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel may not be null");
        }
        if (conflictIntermod == null) {
            throw new IllegalArgumentException("Intermod may not be null");
        }
        this.channel = channel;
        this.conflictIntermod = conflictIntermod;
        this.conflictChannel = null;
        this.type = Type.INTERMOD_SPACING;
    }

    // Constructor to create white space conflict
    Conflict(Channel channel) {
        if (channel == null) {
            throw new IllegalArgumentException("Channel may not be null");
        }
        this.channel = channel;
        this.conflictIntermod = null;
        this.conflictChannel = null;
        this.type = Type.WHITE_SPACE;
    }

    @Override
    public String toString() {
        // Channel Spacing Conflict
        if (conflictChannel != null) {
            return String.format(locale, "Channel spacing violation (%s)", conflictChannel.toStringWithMHz());

        // Intermodulation Spacing Conflict
        } else if (conflictIntermod != null) {
            return String.format(locale, "%s violation (%s)", conflictIntermod.getType().pretty(), conflictIntermod);
        }

        // Whitespace Conflict
        return "Possible white space violation";
    }

    // GETTERS AND SETTERS
    Channel getChannel() {
        return channel;
    }

    Channel getConflictChannel() {
        return conflictChannel;
    }

    Intermod getConflictIntermod() {
        return conflictIntermod;
    }

    Type getType() {
        return type;
    }
}
