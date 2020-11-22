// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

public class Scale extends Synth
{
    private Synth synth;
    private double xScale;
    private double yScale;
    
    public Scale(final Synth synth, final double xScale, final double yScale) {
        this.synth = synth;
        this.xScale = 1.0 / xScale;
        this.yScale = 1.0 / yScale;
    }
    
    @Override
    public double getValue(final double x, final double y) {
        return this.synth.getValue(x * this.xScale, y * this.yScale);
    }
}
