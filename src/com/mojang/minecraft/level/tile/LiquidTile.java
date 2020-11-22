// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Random;
import com.mojang.minecraft.level.Level;

public class LiquidTile extends Tile
{
    protected int liquidType;
    protected int calmTileId;
    protected int tileId;
    protected int spreadSpeed;
    
    protected LiquidTile(final int id, final int liquidType) {
        super(id);
        this.spreadSpeed = 1;
        this.liquidType = liquidType;
        this.tex = 14;
        if (liquidType == 2) {
            this.tex = 30;
        }
        if (liquidType == 1) {
            this.spreadSpeed = 8;
        }
        if (liquidType == 2) {
            this.spreadSpeed = 2;
        }
        this.tileId = id;
        this.calmTileId = id + 1;
        final float dd = 0.1f;
        this.setShape(0.0f, 0.0f - dd, 0.0f, 1.0f, 1.0f - dd, 1.0f);
        this.setTicking(true);
    }
    
    @Override
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
        this.updateWater(level, x, y, z, 0);
    }
    
    public boolean updateWater(final Level level, final int x, int y, final int z, final int depth) {
        boolean hasChanged = false;
        while (level.getTile(x, --y, z) == 0) {
            final boolean change = level.setTile(x, y, z, this.tileId);
            if (change) {
                hasChanged = true;
            }
            if (!change) {
                break;
            }
            if (this.liquidType == 2) {
                break;
            }
        }
        ++y;
        if (this.liquidType == 1 || !hasChanged) {
            hasChanged |= this.checkWater(level, x - 1, y, z, depth);
            hasChanged |= this.checkWater(level, x + 1, y, z, depth);
            hasChanged |= this.checkWater(level, x, y, z - 1, depth);
            hasChanged |= this.checkWater(level, x, y, z + 1, depth);
        }
        if (!hasChanged) {
            level.setTileNoUpdate(x, y, z, this.calmTileId);
        }
        return hasChanged;
    }
    
    private boolean checkWater(final Level level, final int x, final int y, final int z, final int depth) {
        boolean hasChanged = false;
        final int type = level.getTile(x, y, z);
        if (type == 0) {
            final boolean changed = level.setTile(x, y, z, this.tileId);
            if (changed && depth < this.spreadSpeed) {
                hasChanged |= this.updateWater(level, x, y, z, depth + 1);
            }
        }
        return hasChanged;
    }
    
    @Override
    protected boolean shouldRenderFace(final Level level, final int x, final int y, final int z, final int layer, final int face) {
        if (x < 0 || y < 0 || z < 0 || x >= level.width || z >= level.height) {
            return false;
        }
        if (layer != 2 && this.liquidType == 1) {
            return false;
        }
        final int id = level.getTile(x, y, z);
        return id != this.tileId && id != this.calmTileId && super.shouldRenderFace(level, x, y, z, -1, face);
    }
    
    @Override
    public void renderFace(final Tesselator t, final int x, final int y, final int z, final int face) {
        super.renderFace(t, x, y, z, face);
        super.renderBackFace(t, x, y, z, face);
    }
    
    @Override
    public boolean mayPick() {
        return false;
    }
    
    @Override
    public AABB getAABB(final int x, final int y, final int z) {
        return null;
    }
    
    @Override
    public boolean blocksLight() {
        return true;
    }
    
    @Override
    public boolean isSolid() {
        return false;
    }
    
    @Override
    public int getLiquidType() {
        return this.liquidType;
    }
    
    @Override
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
        if (this.liquidType == 1 && (type == Tile.lava.id || type == Tile.calmLava.id)) {
            level.setTileNoUpdate(x, y, z, Tile.rock.id);
        }
        if (this.liquidType == 2 && (type == Tile.water.id || type == Tile.calmWater.id)) {
            level.setTileNoUpdate(x, y, z, Tile.rock.id);
        }
    }
}
