// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level;

import com.mojang.minecraft.Player;
import java.util.Comparator;

public class DirtyChunkSorter implements Comparator<Chunk>
{
    private Player player;
    
    public DirtyChunkSorter(final Player player) {
        this.player = player;
    }
    
    public int compare(final Chunk c0, final Chunk c1) {
        final boolean i0 = c0.visible;
        final boolean i2 = c1.visible;
        if (i0 && !i2) {
            return -1;
        }
        if (i2 && !i0) {
            return 1;
        }
        return (c0.distanceToSqr(this.player) < c1.distanceToSqr(this.player)) ? -1 : 1;
    }
}
