// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Random;
import com.mojang.minecraft.level.Level;

public class WhiteCloth extends Tile
{
    protected WhiteCloth(final int id) {
        super(id);
        this.tex = 79;
        //this.setTicking(true);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        final int below = level.getTile(x, y - 1, z);
        if (!level.isLit(x, y, z) || (below != Tile.dirt.id && below != Tile.grass.id)) {
            level.setTile(x, y, z, 0);
        }
    }
    
//    @Override
//    public AABB getAABB(final int x, final int y, final int z) {
//        return null;
//    }
    
    @Override
    public boolean blocksLight() {
        return false;
    }
    
    @Override
    public boolean isSolid() {
        return true;
    }
}
