// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

import java.util.Random;

public class ImprovedNoise extends Synth
{
    private int[] p;
    public double scale;
    
    public ImprovedNoise() {
        this(new Random());
    }
    
    public ImprovedNoise(final Random random) {
        this.p = new int[512];
        for (int i = 0; i < 256; ++i) {
            this.p[i] = i;
        }
        for (int i = 0; i < 256; ++i) {
            final int j = random.nextInt(256 - i) + i;
            final int tmp = this.p[i];
            this.p[i] = this.p[j];
            this.p[j] = tmp;
            this.p[i + 256] = this.p[i];
        }
    }
    
    public double noise(double x, double y, double z) {
        final int X = (int)Math.floor(x) & 0xFF;
        final int Y = (int)Math.floor(y) & 0xFF;
        final int Z = (int)Math.floor(z) & 0xFF;
        x -= Math.floor(x);
        y -= Math.floor(y);
        z -= Math.floor(z);
        final double u = this.fade(x);
        final double v = this.fade(y);
        final double w = this.fade(z);
        final int A = this.p[X] + Y;
        final int AA = this.p[A] + Z;
        final int AB = this.p[A + 1] + Z;
        final int B = this.p[X + 1] + Y;
        final int BA = this.p[B] + Z;
        final int BB = this.p[B + 1] + Z;
        return this.lerp(w, this.lerp(v, this.lerp(u, this.grad(this.p[AA], x, y, z), this.grad(this.p[BA], x - 1.0, y, z)), this.lerp(u, this.grad(this.p[AB], x, y - 1.0, z), this.grad(this.p[BB], x - 1.0, y - 1.0, z))), this.lerp(v, this.lerp(u, this.grad(this.p[AA + 1], x, y, z - 1.0), this.grad(this.p[BA + 1], x - 1.0, y, z - 1.0)), this.lerp(u, this.grad(this.p[AB + 1], x, y - 1.0, z - 1.0), this.grad(this.p[BB + 1], x - 1.0, y - 1.0, z - 1.0))));
    }
    
    public double fade(final double t) {
        return t * t * t * (t * (t * 6.0 - 15.0) + 10.0);
    }
    
    public double lerp(final double t, final double a, final double b) {
        return a + t * (b - a);
    }
    
    public double grad(final int hash, final double x, final double y, final double z) {
        final int h = hash & 0xF;
        final double u = (h < 8) ? x : y;
        final double v = (h < 4) ? y : ((h == 12 || h == 14) ? x : z);
        return (((h & 0x1) == 0x0) ? u : (-u)) + (((h & 0x2) == 0x0) ? v : (-v));
    }
    
    @Override
    public double getValue(final double x, final double y) {
        return this.noise(x, y, 0.0);
    }
}
