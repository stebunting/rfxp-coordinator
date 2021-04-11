package com.stevebunting.rfxp.coordinator;

import org.jetbrains.annotations.NotNull;

class Intermod implements Comparable<Intermod>, FrequencyComponent {
    enum Type {
        IM_2T3O("2T3O"),
        IM_2T5O("2T5O"),
        IM_2T7O("2T7O"),
        IM_2T9O("2T9O"),
        IM_3T3O("3T3O");

        private final String label;

        Type(@NotNull final String label) {
            this.label = label;
        }

        public String pretty() {
            return this.label;
        }
    }

    private final int frequency;
    @NotNull private final Type type;
    @NotNull private final Channel f1;
    @NotNull private final Channel f2;
    private final Channel f3;

    Intermod(@NotNull final Type type,
             @NotNull final Channel f1,
             @NotNull final Channel f2,
             final Channel f3) {
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
        switch (type) {
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

            case IM_3T3O:
                this.frequency = f1.getFreq() + f2.getFreq() - f3.getFreq();
                break;

            default:
                this.frequency = -1;
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

    public int getFreq() {
        return frequency;
    }

    final Type getType() {
        return type;
    }

    final Channel getF1() {
        return f1;
    }

    final Channel getF2() {
        return f2;
    }

    final Channel getF3() {
        return f3;
    }
}
