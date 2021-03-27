package com.stevebunting.rfxp.coordinator;

class InvalidFrequencyException extends Exception {
    InvalidFrequencyException() {
        super("Invalid Frequency for Selected Equipment");
    }
}
