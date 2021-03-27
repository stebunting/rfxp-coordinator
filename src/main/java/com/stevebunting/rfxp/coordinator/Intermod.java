package com.stevebunting.rfxp.coordinator;

class Intermod implements Comparable<Intermod> {
    // INSTANCE VARIABLES
    enum Type {
        IM_2T3O("2T3O"),
        IM_2T5O("2T5O"),
        IM_2T7O("2T7O"),
        IM_2T9O("2T9O"),
        IM_3T3O("3T3O");

        private final String label;

        Type(String label) {
            this.label = label;
        }

        public String pretty() {
            return this.label;
        }
    }

    // Integer to store frequency (kHz)
    private final int frequency;

    // String to store intermodulation type
    private final Type type;

    // Integer to store generator frequencies (kHz)
    private final Channel f1;
    private final Channel f2;
    private final Channel f3;

    // CONSTRUCTORS
    // Constructor to create second-order intermodulation
    Intermod(Type type, Channel f1, Channel f2, Channel f3) {
        if (type == null) {
            throw new IllegalArgumentException("Type may not be null");
        }
        if (f1 == null) {
            throw new IllegalArgumentException("Channel 1 may not be null");
        }
        if (f2 == null) {
            throw new IllegalArgumentException("Channel 2 may not be null");
        }
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = type == Type.IM_3T3O ? f3 : null;
        this.type = type;
        switch (this.type) {
            case IM_2T3O:
                this.frequency = (2 * f1.getFreq()) - f2.getFreq();
                break;

            case IM_2T5O:
                this.frequency = (3 * f1.getFreq()) - (2 * f2.getFreq());
                break;

            case IM_2T7O:
                this.frequency = (4 * f1.getFreq()) - (3 * f2.getFreq());
                break;

            case IM_2T9O:
                this.frequency = (5 * f1.getFreq()) - (4 * f2.getFreq());
                break;

            default:
            case IM_3T3O:
                this.frequency = f1.getFreq() + f2.getFreq() - f3.getFreq();
                break;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Intermod)) {
            return false;
        }
        Intermod that = (Intermod) obj;
        return (this.getFreq() == that.getFreq()
                && this.getType() == that.getType()
                && this.getF1().getFreq() == that.getF1().getFreq()
                && this.getF2().getFreq() == that.getF2().getFreq());
    }

    @Override
    public int compareTo(Intermod that) {
        if (this.getFreq() - that.getFreq() != 0) {
            return this.getFreq() - that.getFreq();
        } else if (this.getType().compareTo(that.getType()) != 0) {
            return this.getType().compareTo(that.getType());
        } else if (this.getF1().getFreq() - that.getF1().getFreq() != 0) {
            return this.getF1().getFreq() - that.getF1().getFreq();
        } else if (this.getF2().getFreq() - that.getF2().getFreq() != 0) {
            return this.getF2().getFreq() - that.getF2().getFreq();
        } else if (this.getF3() != null && that.getF3() != null) {
            return this.getF3().getFreq() - that.getF3().getFreq();
        } else {
            return 0;
        }
    }

    @Override
    public String toString() {
        StringBuilder text = new StringBuilder();
        text.append(this.f1.toStringWithMHz()).append(" & ").append(this.f2.toStringWithMHz());
        if (this.type == Type.IM_3T3O) {
            text.append(" & ").append(this.f3.toStringWithMHz());
        }
        return text.toString();
    }

    // GETTERS AND SETTERS
    public int getFreq() {
        return frequency;
    }

    public Type getType() {
        return type;
    }

    public Channel getF1() {
        return f1;
    }

    public Channel getF2() {
        return f2;
    }

    public Channel getF3() {
        return f3;
    }
}
