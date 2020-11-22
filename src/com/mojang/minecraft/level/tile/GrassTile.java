// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.tile;

import java.util.Random;
import com.mojang.minecraft.level.Level;

public class GrassTile extends Tile
{
    protected GrassTile(final int id) {
        super(id);
        this.tex = 3;
        this.setTicking(true);
    }
    
    @Override
    protected int getTexture(final int face) {
        if (face == 1) {
            return 0;
        }
        if (face == 0) {
            return 2;
        }
        return 3;
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        if (random.nextInt(4) != 0) {
            return;
        }
        if (!level.isLit(x, y + 1, z)) {
            level.setTile(x, y, z, Tile.dirt.id);
        }
        else {
            for (int i = 0; i < 4; ++i) {
                final int xt = x + random.nextInt(3) - 1;
                final int yt = y + random.nextInt(5) - 3;
                final int zt = z + random.nextInt(3) - 1;
                if (level.getTile(xt, yt, zt) == Tile.dirt.id && level.isLit(xt, yt + 1, zt)) {
                    level.setTile(xt, yt, zt, Tile.grass.id);
                }
            }
        }
    }
}
