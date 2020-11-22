// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.tile;

import java.util.Random;
import com.mojang.minecraft.level.Level;

public class CalmLiquidTile extends LiquidTile
{
    protected CalmLiquidTile(final int id, final int liquidType) {
        super(id, liquidType);
        this.tileId = id - 1;
        this.calmTileId = id;
        this.setTicking(false);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        boolean hasAirNeighbor = false;
        if (level.getTile(x - 1, y, z) == 0) {
            hasAirNeighbor = true;
        }
        if (level.getTile(x + 1, y, z) == 0) {
            hasAirNeighbor = true;
        }
        if (level.getTile(x, y, z - 1) == 0) {
            hasAirNeighbor = true;
        }
        if (level.getTile(x, y, z + 1) == 0) {
            hasAirNeighbor = true;
        }
        if (level.getTile(x, y - 1, z) == 0) {
            hasAirNeighbor = true;
        }
        if (hasAirNeighbor) {
            level.setTileNoUpdate(x, y, z, this.tileId);
        }
        if (this.liquidType == 1 && type == Tile.lava.id) {
            level.setTileNoUpdate(x, y, z, Tile.rock.id);
        }
        if (this.liquidType == 2 && type == Tile.water.id) {
            level.setTileNoUpdate(x, y, z, Tile.rock.id);
        }
    }
}
