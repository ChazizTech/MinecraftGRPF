// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

public class Distort extends Synth
{
    private Synth source;
    private Synth distort;
    
    public Distort(final Synth source, final Synth distort) {
        this.source = source;
        this.distort = distort;
    }
    
    @Override
    public double getValue(final double x, final double y) {
        return this.source.getValue(x + this.distort.getValue(x, y), y);
    }
}
