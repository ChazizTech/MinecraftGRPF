// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;
import java.util.Comparator;

public class DistanceSorter implements Comparator<Chunk>
{
    private Player player;
    
    public DistanceSorter(final Player player) {
        this.player = player;
    }
    
    public int compare(final Chunk c0, final Chunk c1) {
        return (c0.distanceToSqr(this.player) < c1.distanceToSqr(this.player)) ? -1 : 1;
    }
}
