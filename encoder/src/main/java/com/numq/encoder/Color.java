package com.numq.encoder;

/**
 * BGR Color representation
 */

public final class Color {

    final int blue;
    final int green;
    final int red;

    final int alpha;

    public Color(int blue, int green, int red, int alpha) {
        this.blue = blue;
        this.green = green;
        this.red = red;
        this.alpha = alpha;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Color)) return false;
        Color that = (Color) o;
        return this.blue == that.blue
                && this.green == that.green
                && this.red == that.red
                && this.alpha == that.alpha;
    }

    @Override
    public int hashCode() {
        int result;
        long temp;
        temp = Double.doubleToLongBits(blue);
        result = (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(green);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        temp = Double.doubleToLongBits(red);
        result = 31 * result + (int) (temp ^ (temp >>> 32));
        return result;
    }

    @Override
    public String toString() {
        return String.format("Color[%d, %d, %d, %d]", blue, green, red, alpha);
    }
}