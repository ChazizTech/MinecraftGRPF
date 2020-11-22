// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen.synth;

public class Emboss extends Synth
{
    private Synth synth;
    
    public Emboss(final Synth synth) {
        this.synth = synth;
    }
    
    @Override
    public double getValue(final double x, final double y) {
        return this.synth.getValue(x, y) - this.synth.getValue(x + 1.0, y + 1.0);
    }
}
