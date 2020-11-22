// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

public abstract class Synth
{
    public abstract double getValue(final double p0, final double p1);
    
    public double[] create(final int width, final int height) {
        final double[] result = new double[width * height];
        for (int y = 0; y < height; ++y) {
            for (int x = 0; x < width; ++x) {
                result[x + y * width] = this.getValue(x, y);
            }
        }
        return result;
    }
}
