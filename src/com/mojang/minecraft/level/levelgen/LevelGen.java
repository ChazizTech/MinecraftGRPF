// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.levelgen;

import java.util.ArrayList;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.level.PerlinNoiseFilter;
import java.util.Random;
import com.mojang.minecraft.level.LevelLoaderListener;

public class LevelGen
{
    private LevelLoaderListener levelLoaderListener;
    private int width;
    private int height;
    private int depth;
    private Random random;
    private byte[] blocks;
    private int[] coords;
    
    public LevelGen(final LevelLoaderListener levelLoaderListener) {
        this.random = new Random();
        this.coords = new int[1048576];
        this.levelLoaderListener = levelLoaderListener;
    }
    
    public boolean generateLevel(final Level level, final String userName, final int width, final int height, final int depth, final String levelName) {
        this.levelLoaderListener.beginLevelLoading("Generating level");
        this.width = width;
        this.height = height;
        this.depth = depth;
        this.blocks = new byte[width * height * depth];
        this.levelLoaderListener.levelLoadUpdate("Raising..");
        final double[] heightMap = this.buildHeightmap(width, height);
        this.levelLoaderListener.levelLoadUpdate("Eroding..");
        this.buildBlocks(heightMap);
        this.levelLoaderListener.levelLoadUpdate("Carving..");
        this.carveTunnels();
        this.levelLoaderListener.levelLoadUpdate("Watering..");
        this.addWater();
        this.levelLoaderListener.levelLoadUpdate("Melting..");
        this.addLava();
        level.setData(width, depth, height, this.blocks);
        level.createTime = System.currentTimeMillis();
        level.creator = userName;
        level.name = levelName;
        return true;
    }
    
    private void buildBlocks(final double[] heightMap) {
        final int w = this.width;
        final int h = this.height;
        final int d = this.depth;
        final int[] heightmap1 = new PerlinNoiseFilter(0).read(w, h);
        final int[] heightmap2 = new PerlinNoiseFilter(0).read(w, h);
        final int[] cf = new PerlinNoiseFilter(1).read(w, h);
        final int[] rockMap = new PerlinNoiseFilter(1).read(w, h);
        final byte[] blocks = new byte[this.width * this.height * this.depth];
        for (int x = 0; x < w; ++x) {
            for (int y = 0; y < d; ++y) {
                for (int z = 0; z < h; ++z) {
                    final int dh = d / 2;
                    //final int rh = d / 3;
                    //final int i = (y * this.height + z) * this.width + x;
                    final int dh1 = heightmap1[x + z * this.width];
                    int dh2 = heightmap2[x + z * this.width];
                    final int cfh = cf[x + z * this.width];
                    if (cfh < 128) {
                        dh2 = dh1;
                    }
                    int dh3 = dh1;
                    if (dh2 > dh3) {
                        dh3 = dh2;
                    }
                    else {
                        dh2 = dh1;
                    }
                    dh3 = dh3 / 8 + d / 3;
                    int rh = rockMap[x + z * this.width] / 8 + d / 3;
                    if (rh > dh3 - 2) {
                        rh = dh3 - 2;
                    }
                    final int i = (y * this.height + z) * this.width + x;
                    int id = 0;
                    if (y == dh3) {
                        id = Tile.grass.id;
                    }
                    if (y < dh3) {
                        id = Tile.dirt.id;
                    }
                    if (y <= rh) {
                        id = Tile.rock.id;
                    }
                    this.blocks[i] = (byte)id;
                }
            }
        }
    }
    
    private double[] buildHeightmap(final int width, final int height) {
        final double[] heightmap = new double[width * height];
        return heightmap;
    }
    
    public void carveTunnels() {
        final int w = this.width;
        final int h = this.height;
        final int d = this.depth;
        for (int count = w * h * d / 256 / 64, i = 0; i < count; ++i) {
            float x = this.random.nextFloat() * w;
            float y = this.random.nextFloat() * d;
            float z = this.random.nextFloat() * h;
            final int length = (int)(this.random.nextFloat() + this.random.nextFloat() * 150.0f);
            float dir1 = (float)(this.random.nextFloat() * 3.141592653589793 * 2.0);
            float dira1 = 0.0f;
            float dir2 = (float)(this.random.nextFloat() * 3.141592653589793 * 2.0);
            float dira2 = 0.0f;
            for (int l = 0; l < length; ++l) {
                x += (float)(Math.sin(dir1) * Math.cos(dir2));
                z += (float)(Math.cos(dir1) * Math.cos(dir2));
                y += (float)Math.sin(dir2);
                dir1 += dira1 * 0.2f;
                dira1 *= 0.9f;
                dira1 += this.random.nextFloat() - this.random.nextFloat();
                dir2 += dira2 * 0.5f;
                dir2 *= 0.5f;
                dira2 *= 0.9f;
                dira2 += this.random.nextFloat() - this.random.nextFloat();
                final float size = (float)(Math.sin(l * 3.141592653589793 / length) * 2.5 + 1.0);
                for (int xx = (int)(x - size); xx <= (int)(x + size); ++xx) {
                    for (int yy = (int)(y - size); yy <= (int)(y + size); ++yy) {
                        for (int zz = (int)(z - size); zz <= (int)(z + size); ++zz) {
                            final float xd = xx - x;
                            final float yd = yy - y;
                            final float zd = zz - z;
                            final float dd = xd * xd + yd * yd * 2.0f + zd * zd;
                            if (dd < size * size && xx >= 1 && yy >= 1 && zz >= 1 && xx < this.width - 1 && yy < this.depth - 1 && zz < this.height - 1) {
                                final int ii = (yy * this.height + zz) * this.width + xx;
                                if (this.blocks[ii] == Tile.rock.id) {
                                    this.blocks[ii] = 0;
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void addWater() {
        final long before = System.nanoTime();
        long tiles = 0L;
        final int source = 0;
        final int target = Tile.calmWater.id;
        for (int x = 0; x < this.width; ++x) {
            tiles += this.floodFillLiquid(x, this.depth / 2 - 1, 0, source, target);
            tiles += this.floodFillLiquid(x, this.depth / 2 - 1, this.height - 1, source, target);
        }
        for (int y = 0; y < this.height; ++y) {
            tiles += this.floodFillLiquid(0, this.depth / 2 - 1, y, source, target);
            tiles += this.floodFillLiquid(this.width - 1, this.depth / 2 - 1, y, source, target);
        }
        for (int i = 0; i < this.width * this.height / 5000; ++i) {
            final int x2 = this.random.nextInt(this.width);
            final int y2 = this.depth / 2 - 1;
            final int z = this.random.nextInt(this.height);
            if (this.blocks[(y2 * this.height + z) * this.width + x2] == 0) {
                tiles += this.floodFillLiquid(x2, y2, z, 0, target);
            }
        }
        final long after = System.nanoTime();
        System.out.println("Flood filled " + tiles + " tiles in " + (after - before) / 1000000.0 + " ms");
    }
    
    public void addLava() {
        int lavaCount = 0;
        for (int i = 0; i < this.width * this.height * this.depth / 10000; ++i) {
            final int x = this.random.nextInt(this.width);
            final int y = this.random.nextInt(this.depth / 2);
            final int z = this.random.nextInt(this.height);
            if (this.blocks[(y * this.height + z) * this.width + x] == 0) {
                ++lavaCount;
                this.floodFillLiquid(x, y, z, 0, Tile.calmLava.id);
            }
        }
        System.out.println("LavaCount: " + lavaCount);
    }
    
    public long floodFillLiquid(final int x, final int y, final int z, final int source, final int tt) {
        final byte target = (byte)tt;
        final ArrayList<int[]> coordBuffer = new ArrayList<int[]>();
        int p = 0;
        int wBits = 1;
        int hBits = 1;
        while (1 << wBits < this.width) {
            ++wBits;
        }
        while (1 << hBits < this.height) {
            ++hBits;
        }
        final int hMask = this.height - 1;
        final int wMask = this.width - 1;
        this.coords[p++] = ((y << hBits) + z << wBits) + x;
        long tiles = 0L;
        final int upStep = this.width * this.height;
        while (p > 0) {
            int cl = this.coords[--p];
            if (p == 0 && coordBuffer.size() > 0) {
                System.out.println("IT HAPPENED!");
                this.coords = coordBuffer.remove(coordBuffer.size() - 1);
                p = this.coords.length;
            }
            final int z2 = cl >> wBits & hMask;
            final int y2 = cl >> wBits + hBits;
            int x3;
            int x2;
            for (x2 = (x3 = (cl & wMask)); x2 > 0; --x2, --cl) {
                if (this.blocks[cl - 1] != source) {
                    break;
                }
            }
            while (x3 < this.width && this.blocks[cl + x3 - x2] == source) {
                ++x3;
            }
            final int z3 = cl >> wBits & hMask;
            final int y3 = cl >> wBits + hBits;
            if (z3 != z2 || y3 != y2) {
                System.out.println("hoooly fuck");
            }
            boolean lastNorth = false;
            boolean lastSouth = false;
            boolean lastBelow = false;
            tiles += x3 - x2;
            for (int xx = x2; xx < x3; ++xx) {
                this.blocks[cl] = target;
                if (z2 > 0) {
                    final boolean north = this.blocks[cl - this.width] == source;
                    if (north && !lastNorth) {
                        if (p == this.coords.length) {
                            coordBuffer.add(this.coords);
                            this.coords = new int[1048576];
                            p = 0;
                        }
                        this.coords[p++] = cl - this.width;
                    }
                    lastNorth = north;
                }
                if (z2 < this.height - 1) {
                    final boolean south = this.blocks[cl + this.width] == source;
                    if (south && !lastSouth) {
                        if (p == this.coords.length) {
                            coordBuffer.add(this.coords);
                            this.coords = new int[1048576];
                            p = 0;
                        }
                        this.coords[p++] = cl + this.width;
                    }
                    lastSouth = south;
                }
                if (y2 > 0) {
                    final int belowId = this.blocks[cl - upStep];
                    if ((target == Tile.lava.id || target == Tile.calmLava.id) && (belowId == Tile.water.id || belowId == Tile.calmWater.id)) {
                        this.blocks[cl - upStep] = (byte)Tile.rock.id;
                    }
                    final boolean below = belowId == source;
                    if (below && !lastBelow) {
                        if (p == this.coords.length) {
                            coordBuffer.add(this.coords);
                            this.coords = new int[1048576];
                            p = 0;
                        }
                        this.coords[p++] = cl - upStep;
                    }
                    lastBelow = below;
                }
                ++cl;
            }
        }
        return tiles;
    }
}
