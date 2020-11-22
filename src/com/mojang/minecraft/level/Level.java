// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level;

import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.level.tile.Tile;
import java.util.Random;
import java.util.ArrayList;

public class Level
{
    private static final int TILE_UPDATE_INTERVAL = 200;
    public int width;
    public int height;
    public int depth;
    byte[] blocks;
    private int[] lightDepths;
    private ArrayList<LevelListener> levelListeners;
    private Random random;
    private int randValue;
    public String name;
    public String creator;
    public long createTime;
    int unprocessed;
    private static final int multiplier = 1664525;
    private static final int addend = 1013904223;
    
    public Level() {
        this.levelListeners = new ArrayList<LevelListener>();
        this.random = new Random();
        this.randValue = this.random.nextInt();
        this.unprocessed = 0;
    }
    
    public void setData(final int w, final int d, final int h, final byte[] blocks) {
        this.width = w;
        this.height = h;
        this.depth = d;
        this.blocks = blocks;
        this.lightDepths = new int[w * h];
        this.calcLightDepths(0, 0, w, h);
        for (int i = 0; i < this.levelListeners.size(); ++i) {
            this.levelListeners.get(i).allChanged();
        }
    }
    
    public void calcLightDepths(final int x0, final int y0, final int x1, final int y1) {
        for (int x2 = x0; x2 < x0 + x1; ++x2) {
            for (int z = y0; z < y0 + y1; ++z) {
                final int oldDepth = this.lightDepths[x2 + z * this.width];
                int y2;
                for (y2 = this.depth - 1; y2 > 0 && !this.isLightBlocker(x2, y2, z); --y2) {}
                this.lightDepths[x2 + z * this.width] = y2 + 1;
                if (oldDepth != y2) {
                    final int yl0 = (oldDepth < y2) ? oldDepth : y2;
                    final int yl2 = (oldDepth > y2) ? oldDepth : y2;
                    for (int i = 0; i < this.levelListeners.size(); ++i) {
                        this.levelListeners.get(i).lightColumnChanged(x2, z, yl0, yl2);
                    }
                }
            }
        }
    }
    
    public void addListener(final LevelListener levelListener) {
        this.levelListeners.add(levelListener);
    }
    
    public void removeListener(final LevelListener levelListener) {
        this.levelListeners.remove(levelListener);
    }
    
    public boolean isLightBlocker(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.blocksLight();
    }
    
    public ArrayList<AABB> getCubes(final AABB box) {
        final ArrayList<AABB> boxes = new ArrayList<AABB>();
        final int x0 = (int)Math.floor(box.x0);
        final int x2 = (int)Math.floor(box.x1 + 1.0f);
        final int y0 = (int)Math.floor(box.y0);
        final int y2 = (int)Math.floor(box.y1 + 1.0f);
        final int z0 = (int)Math.floor(box.z0);
        final int z2 = (int)Math.floor(box.z1 + 1.0f);
        for (int x3 = x0; x3 < x2; ++x3) {
            for (int y3 = y0; y3 < y2; ++y3) {
                for (int z3 = z0; z3 < z2; ++z3) {
                    if (x3 >= 0 && y3 >= 0 && z3 >= 0 && x3 < this.width && y3 < this.depth && z3 < this.height) {
                        final Tile tile = Tile.tiles[this.getTile(x3, y3, z3)];
                        if (tile != null) {
                            final AABB aabb = tile.getAABB(x3, y3, z3);
                            if (aabb != null) {
                                boxes.add(aabb);
                            }
                        }
                    }
                    else if (x3 < 0 || y3 < 0 || z3 < 0 || x3 >= this.width || z3 >= this.height) {
                        final AABB aabb2 = Tile.unbreakable.getAABB(x3, y3, z3);
                        if (aabb2 != null) {
                            boxes.add(aabb2);
                        }
                    }
                }
            }
        }
        return boxes;
    }
    
    public boolean setTile(final int x, final int y, final int z, final int type) {
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return false;
        }
        if (type == this.blocks[(y * this.height + z) * this.width + x]) {
            return false;
        }
        this.blocks[(y * this.height + z) * this.width + x] = (byte)type;
        this.neighborChanged(x - 1, y, z, type);
        this.neighborChanged(x + 1, y, z, type);
        this.neighborChanged(x, y - 1, z, type);
        this.neighborChanged(x, y + 1, z, type);
        this.neighborChanged(x, y, z - 1, type);
        this.neighborChanged(x, y, z + 1, type);
        this.calcLightDepths(x, z, 1, 1);
        for (int i = 0; i < this.levelListeners.size(); ++i) {
            this.levelListeners.get(i).tileChanged(x, y, z);
        }
        return true;
    }
    
    public boolean setTileNoUpdate(final int x, final int y, final int z, final int type) {
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return false;
        }
        if (type == this.blocks[(y * this.height + z) * this.width + x]) {
            return false;
        }
        this.blocks[(y * this.height + z) * this.width + x] = (byte)type;
        return true;
    }
    
    private void neighborChanged(final int x, final int y, final int z, final int type) {
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return;
        }
        final Tile tile = Tile.tiles[this.blocks[(y * this.height + z) * this.width + x]];
        if (tile != null) {
            tile.neighborChanged(this, x, y, z, type);
        }
    }
    
    public boolean isLit(final int x, final int y, final int z) {
        return x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height || y >= this.lightDepths[x + z * this.width];
    }
    
    public int getTile(final int x, final int y, final int z) {
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.depth || z >= this.height) {
            return 0;
        }
        return this.blocks[(y * this.height + z) * this.width + x];
    }
    
    public boolean isSolidTile(final int x, final int y, final int z) {
        final Tile tile = Tile.tiles[this.getTile(x, y, z)];
        return tile != null && tile.isSolid();
    }
    
    public void tick() {
        this.unprocessed += this.width * this.height * this.depth;
        final int ticks = this.unprocessed / 200;
        this.unprocessed -= ticks * 200;
        for (int i = 0; i < ticks; ++i) {
            this.randValue = this.randValue * 1664525 + 1013904223;
            final int x = this.randValue >> 16 & this.width - 1;
            this.randValue = this.randValue * 1664525 + 1013904223;
            final int y = this.randValue >> 16 & this.depth - 1;
            this.randValue = this.randValue * 1664525 + 1013904223;
            final int z = this.randValue >> 16 & this.height - 1;
            final int id = this.blocks[(y * this.height + z) * this.width + x];
            if (Tile.shouldTick[id]) {
                Tile.tiles[id].tick(this, x, y, z, this.random);
            }
        }
    }
    
    public float getGroundLevel() {
        return 32.0f;
    }
    
    public boolean containsAnyLiquid(final AABB box) {
        int x0 = (int)Math.floor(box.x0);
        int x2 = (int)Math.floor(box.x1 + 1.0f);
        int y0 = (int)Math.floor(box.y0);
        int y2 = (int)Math.floor(box.y1 + 1.0f);
        int z0 = (int)Math.floor(box.z0);
        int z2 = (int)Math.floor(box.z1 + 1.0f);
        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x2 > this.width) {
            x2 = this.width;
        }
        if (y2 > this.depth) {
            y2 = this.depth;
        }
        if (z2 > this.height) {
            z2 = this.height;
        }
        for (int x3 = x0; x3 < x2; ++x3) {
            for (int y3 = y0; y3 < y2; ++y3) {
                for (int z3 = z0; z3 < z2; ++z3) {
                    final Tile tile = Tile.tiles[this.getTile(x3, y3, z3)];
                    if (tile != null && tile.getLiquidType() > 0) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
    
    public boolean containsLiquid(final AABB box, final int liquidId) {
        int x0 = (int)Math.floor(box.x0);
        int x2 = (int)Math.floor(box.x1 + 1.0f);
        int y0 = (int)Math.floor(box.y0);
        int y2 = (int)Math.floor(box.y1 + 1.0f);
        int z0 = (int)Math.floor(box.z0);
        int z2 = (int)Math.floor(box.z1 + 1.0f);
        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x2 > this.width) {
            x2 = this.width;
        }
        if (y2 > this.depth) {
            y2 = this.depth;
        }
        if (z2 > this.height) {
            z2 = this.height;
        }
        for (int x3 = x0; x3 < x2; ++x3) {
            for (int y3 = y0; y3 < y2; ++y3) {
                for (int z3 = z0; z3 < z2; ++z3) {
                    final Tile tile = Tile.tiles[this.getTile(x3, y3, z3)];
                    if (tile != null && tile.getLiquidType() == liquidId) {
                        return true;
                    }
                }
            }
        }
        return false;
    }
}
