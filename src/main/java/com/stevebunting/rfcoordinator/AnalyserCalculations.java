package com.stevebunting.rfcoordinator;

/**
 * The AnalyserCalculations class defines which intermodulations are required
 * for a coordination. If an intermodulation is not required, it should not be
 * calculated to optimise performance.
 */
final class AnalyserCalculations {
    private boolean im2t3o = true;
    private boolean im2t5o = true;
    private boolean im2t7o = true;
    private boolean im2t9o = true;
    private boolean im3t3o = true;

    final boolean getIM2t3o() {
        return im2t3o;
    }

    final boolean getIM2t5o() {
        return im2t5o;
    }

    final boolean getIM2t7o() {
        return im2t7o;
    }

    final boolean getIM2t9o() {
        return im2t9o;
    }

    final boolean getIM3t3o() {
        return im3t3o;
    }

    final void setIM2t3o(final boolean im2t3o) {
        this.im2t3o = im2t3o;
    }

    final void setIM2t5o(final boolean im2t5o) {
        this.im2t5o = im2t5o;
    }

    final void setIM2t7o(final boolean im2t7o) {
        this.im2t7o = im2t7o;
    }

    final void setIM2t9o(final boolean im2t9o) {
        this.im2t9o = im2t9o;
    }

    final void setIM3t3o(final boolean im3t3o) {
        this.im3t3o = im3t3o;
    }
}
