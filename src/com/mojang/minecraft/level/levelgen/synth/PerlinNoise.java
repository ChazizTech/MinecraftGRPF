// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

import java.util.Random;

public class PerlinNoise extends Synth
{
    private ImprovedNoise[] noiseLevels;
    private int levels;
    
    public PerlinNoise(final int levels) {
        this(new Random(), levels);
    }
    
    public PerlinNoise(final Random random, final int levels) {
        this.levels = levels;
        this.noiseLevels = new ImprovedNoise[levels];
        for (int i = 0; i < levels; ++i) {
            this.noiseLevels[i] = new ImprovedNoise(random);
        }
    }
    
    @Override
    public double getValue(final double x, final double y) {
        double value = 0.0;
        double pow = 1.0;
        for (int i = 0; i < this.levels; ++i) {
            value += this.noiseLevels[i].getValue(x / pow, y / pow) * pow;
            pow *= 2.0;
        }
        return value;
    }
}
