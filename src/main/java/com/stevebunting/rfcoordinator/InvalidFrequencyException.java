package com.stevebunting.rfcoordinator;

class InvalidFrequencyException extends Exception {
    InvalidFrequencyException() {
        super("Invalid Frequency for Selected Equipment");
    }
}
