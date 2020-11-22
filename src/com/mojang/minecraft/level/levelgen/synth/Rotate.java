// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

public class Rotate extends Synth
{
    private Synth synth;
    private double sin;
    private double cos;
    
    public Rotate(final Synth synth, final double angle) {
        this.synth = synth;
        this.sin = Math.sin(angle);
        this.cos = Math.cos(angle);
    }
    
    @Override
    public double getValue(final double x, final double y) {
        return this.synth.getValue(x * this.cos + y * this.sin, y * this.cos - x * this.sin);
    }
}
