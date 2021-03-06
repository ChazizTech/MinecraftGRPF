// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.tile;

import java.util.Random;
import com.mojang.minecraft.level.Level;

public class GlassDark extends Tile
{
    protected GlassDark(final int id) {
        super(id);
        this.tex = 50;
        //this.setTicking(true);
    }
    
    //@Override
    //public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        //final int below = level.getTile(x, y - 1, z);
        //if (!level.isLit(x, y, z) || (below != Tile.dirt.id && below != Tile.grass.id)) {
        //    level.setTile(x, y, z, 0);
        //}
    //}
    
    @Override
    public boolean blocksLight() {
        return true;
    }
    
    @Override
    protected boolean shouldRenderFace(final Level level, final int x, final int y, final int z, final int layer, final int face) {
        final int id = level.getTile(x, y, z);
        return id != this.id && super.shouldRenderFace(level, x, y, z, -1, face);
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
}
