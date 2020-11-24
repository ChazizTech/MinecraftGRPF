// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level;

import com.mojang.minecraft.HitResult;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.a.a;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.renderer.Frustum;
import java.util.Collections;
import com.mojang.minecraft.renderer.Tesselator;
import java.util.Comparator;
import java.util.Arrays;
import com.mojang.minecraft.Player;
import java.util.ArrayList;
import java.util.List;
import org.lwjgl.opengl.GL11;
import com.mojang.minecraft.renderer.Textures;

public class LevelRenderer implements LevelListener
{
    public static final int MAX_REBUILDS_PER_FRAME = 4;
    public static final int CHUNK_SIZE = 16;
    private Level level;
    private Chunk[] chunks;
    private Chunk[] sortedChunks;
    private int xChunks;
    private int yChunks;
    private int zChunks;
    private Textures textures;
    public int c; //obfuscated shit i can't fix for fuck sakes
    private Textures g;
    private int surroundLists;
    private int drawDistance;
    float lX;
    float lY;
    float lZ;
    
    public LevelRenderer(final Level level, final Textures textures) {
        this.drawDistance = 0;
        this.c = 0;
        this.lX = 0.0f;
        this.c = 0;
        this.lY = 0.0f;
        this.lZ = 0.0f;
        this.level = level;
        this.textures = textures;
        level.addListener(this);
        this.surroundLists = GL11.glGenLists(2);
        this.allChanged();
    }
    
    public void allChanged() {
        this.lX = -900000.0f;
        this.lY = -900000.0f;
        this.lZ = -900000.0f;
        this.xChunks = (this.level.width + 16 - 1) / 16;
        this.yChunks = (this.level.depth + 16 - 1) / 16;
        this.zChunks = (this.level.height + 16 - 1) / 16;
        this.chunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
        this.sortedChunks = new Chunk[this.xChunks * this.yChunks * this.zChunks];
        for (int x = 0; x < this.xChunks; ++x) {
            for (int y = 0; y < this.yChunks; ++y) {
                for (int z = 0; z < this.zChunks; ++z) {
                    final int x2 = x * 16;
                    final int y2 = y * 16;
                    final int z2 = z * 16;
                    int x3 = (x + 1) * 16;
                    int y3 = (y + 1) * 16;
                    int z3 = (z + 1) * 16;
                    if (x3 > this.level.width) {
                        x3 = this.level.width;
                    }
                    if (y3 > this.level.depth) {
                        y3 = this.level.depth;
                    }
                    if (z3 > this.level.height) {
                        z3 = this.level.height;
                    }
                    this.chunks[(x + y * this.xChunks) * this.zChunks + z] = new Chunk(this.level, x2, y2, z2, x3, y3, z3);
                    this.sortedChunks[(x + y * this.xChunks) * this.zChunks + z] = this.chunks[(x + y * this.xChunks) * this.zChunks + z];
                }
            }
        }
        GL11.glNewList(this.surroundLists + 0, 4864);
        this.compileSurroundingGround();
        GL11.glEndList();
        GL11.glNewList(this.surroundLists + 1, 4864);
        this.compileSurroundingWater();
        GL11.glEndList();
        for (int i = 0; i < this.chunks.length; ++i) {
            this.chunks[i].reset();
        }
    }
    
    //clouds do not work because deobfuscating is hard.
/*    public final void a(float n) {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, this.textures.loadTexture("/clouds.png", 9728));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final c a = com.mojang.minecraft.Textures.c.a;
        final float n2 = 4.8828125E-4f;
        final float n3 = (float)(this.a.depth + 2);
        n = (this.f + n) * n2 * 0.03f;
        a.b();
        for (int i = -2048; i < this.a.width + 2048; i += 512) {
            for (int j = -2048; j < this.a.height + 2048; j += 512) {
                this.textures.loadTexture((float)i, n3, (float)(j + 512), i * n2 + n, (j + 512) * n2);
                a.a((float)(i + 512), n3, (float)(j + 512), (i + 512) * n2 + n, (j + 512) * n2);
                a.a((float)(i + 512), n3, (float)j, (i + 512) * n2 + n, j * n2);
                a.a((float)i, n3, (float)j, i * n2 + n, j * n2);
                a.a((float)i, n3, (float)j, i * n2 + n, j * n2);
                a.a((float)(i + 512), n3, (float)j, (i + 512) * n2 + n, j * n2);
                a.a((float)(i + 512), n3, (float)(j + 512), (i + 512) * n2 + n, (j + 512) * n2);
                a.a((float)i, n3, (float)(j + 512), i * n2 + n, (j + 512) * n2);
        }
     }*/
    
    public List<Chunk> getAllDirtyChunks() {
        ArrayList<Chunk> dirty = null;
        for (int i = 0; i < this.chunks.length; ++i) {
            final Chunk chunk = this.chunks[i];
            if (chunk.isDirty()) {
                if (dirty == null) {
                    dirty = new ArrayList<Chunk>();
                }
                dirty.add(chunk);
            }
        }
        return dirty;
    }
    
    public void render(final Player player, final int layer) {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, this.textures.loadTexture("/terrain.png", 9728));
        final float xd = player.x - this.lX;
        final float yd = player.y - this.lY;
        final float zd = player.z - this.lZ;
        if (xd * xd + yd * yd + zd * zd > 64.0f) {
            this.lX = player.x;
            this.lY = player.y;
            this.lZ = player.z;
            Arrays.sort(this.sortedChunks, new DistanceSorter(player));
        }
        for (int i = 0; i < this.sortedChunks.length; ++i) {
            if (this.sortedChunks[i].visible) {
                final float dd = (float)(256 / (1 << this.drawDistance));
                if (this.drawDistance == 0 || this.sortedChunks[i].distanceToSqr(player) < dd * dd) {
                    this.sortedChunks[i].render(layer);
                }
            }
        }
        GL11.glDisable(3553);
    }
    
    public void renderSurroundingGround() {
        GL11.glCallList(this.surroundLists + 0);
    }
    
    public void compileSurroundingGround() {
        GL11.glEnable(3553);
        GL11.glBindTexture(3553, this.textures.loadTexture("/rock.png", 9728));
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        final Tesselator t = Tesselator.instance;
        final float y = this.level.getGroundLevel() - 2.0f;
        int s = 128;
        if (s > this.level.width) {
            s = this.level.width;
        }
        if (s > this.level.height) {
            s = this.level.height;
        }
        final int d = 5;
        t.begin();
        for (int xx = -s * d; xx < this.level.width + s * d; xx += s) {
            for (int zz = -s * d; zz < this.level.height + s * d; zz += s) {
                float yy = y;
                if (xx >= 0 && zz >= 0 && xx < this.level.width && zz < this.level.height) {
                    yy = 0.0f;
                }
                t.vertexUV((float)(xx + 0), yy, (float)(zz + s), 0.0f, (float)s);
                t.vertexUV((float)(xx + s), yy, (float)(zz + s), (float)s, (float)s);
                t.vertexUV((float)(xx + s), yy, (float)(zz + 0), (float)s, 0.0f);
                t.vertexUV((float)(xx + 0), yy, (float)(zz + 0), 0.0f, 0.0f);
            }
        }
        t.end();
        GL11.glBindTexture(3553, this.textures.loadTexture("/rock.png", 9728));
        GL11.glColor3f(0.8f, 0.8f, 0.8f);
        t.begin();
        for (int xx = 0; xx < this.level.width; xx += s) {
            t.vertexUV((float)(xx + 0), 0.0f, 0.0f, 0.0f, 0.0f);
            t.vertexUV((float)(xx + s), 0.0f, 0.0f, (float)s, 0.0f);
            t.vertexUV((float)(xx + s), y, 0.0f, (float)s, y);
            t.vertexUV((float)(xx + 0), y, 0.0f, 0.0f, y);
            t.vertexUV((float)(xx + 0), y, (float)this.level.height, 0.0f, y);
            t.vertexUV((float)(xx + s), y, (float)this.level.height, (float)s, y);
            t.vertexUV((float)(xx + s), 0.0f, (float)this.level.height, (float)s, 0.0f);
            t.vertexUV((float)(xx + 0), 0.0f, (float)this.level.height, 0.0f, 0.0f);
        }
        GL11.glColor3f(0.6f, 0.6f, 0.6f);
        for (int zz2 = 0; zz2 < this.level.height; zz2 += s) {
            t.vertexUV(0.0f, y, (float)(zz2 + 0), 0.0f, 0.0f);
            t.vertexUV(0.0f, y, (float)(zz2 + s), (float)s, 0.0f);
            t.vertexUV(0.0f, 0.0f, (float)(zz2 + s), (float)s, y);
            t.vertexUV(0.0f, 0.0f, (float)(zz2 + 0), 0.0f, y);
            t.vertexUV((float)this.level.width, 0.0f, (float)(zz2 + 0), 0.0f, y);
            t.vertexUV((float)this.level.width, 0.0f, (float)(zz2 + s), (float)s, y);
            t.vertexUV((float)this.level.width, y, (float)(zz2 + s), (float)s, 0.0f);
            t.vertexUV((float)this.level.width, y, (float)(zz2 + 0), 0.0f, 0.0f);
        }
        t.end();
        GL11.glDisable(3042);
        GL11.glDisable(3553);
    }
    
    public void renderSurroundingWater() {
        GL11.glCallList(this.surroundLists + 1);
    }
    
    public void compileSurroundingWater() {
        GL11.glEnable(3553);
        GL11.glColor3f(1.0f, 1.0f, 1.0f);
        GL11.glBindTexture(3553, this.textures.loadTexture("/water.png", 9728));
        final float y = this.level.getGroundLevel();
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        final Tesselator t = Tesselator.instance;
        int s = 128;
        if (s > this.level.width) {
            s = this.level.width;
        }
        if (s > this.level.height) {
            s = this.level.height;
        }
        final int d = 5;
        t.begin();
        for (int xx = -s * d; xx < this.level.width + s * d; xx += s) {
            for (int zz = -s * d; zz < this.level.height + s * d; zz += s) {
                final float yy = y - 0.1f;
                if (xx < 0 || zz < 0 || xx >= this.level.width || zz >= this.level.height) {
                    t.vertexUV((float)(xx + 0), yy, (float)(zz + s), 0.0f, (float)s);
                    t.vertexUV((float)(xx + s), yy, (float)(zz + s), (float)s, (float)s);
                    t.vertexUV((float)(xx + s), yy, (float)(zz + 0), (float)s, 0.0f);
                    t.vertexUV((float)(xx + 0), yy, (float)(zz + 0), 0.0f, 0.0f);
                    t.vertexUV((float)(xx + 0), yy, (float)(zz + 0), 0.0f, 0.0f);
                    t.vertexUV((float)(xx + s), yy, (float)(zz + 0), (float)s, 0.0f);
                    t.vertexUV((float)(xx + s), yy, (float)(zz + s), (float)s, (float)s);
                    t.vertexUV((float)(xx + 0), yy, (float)(zz + s), 0.0f, (float)s);
                }
            }
        }
        t.end();
        GL11.glDisable(3042);
        GL11.glDisable(3553);
    }
    
    public void updateDirtyChunks(final Player player) {
        final List<Chunk> dirty = this.getAllDirtyChunks();
        if (dirty == null) {
            return;
        }
        Collections.sort(dirty, new DirtyChunkSorter(player));
        for (int i = 0; i < 4 && i < dirty.size(); ++i) {
            dirty.get(i).rebuild();
        }
    }
    
    public void pick(final Player player, final Frustum frustum) {
        final Tesselator t = Tesselator.instance;
        final float r = 2.5f;
        final AABB box = player.bb.grow(r, r, r);
        final int x0 = (int)box.x0;
        final int x2 = (int)(box.x1 + 1.0f);
        final int y0 = (int)box.y0;
        final int y2 = (int)(box.y1 + 1.0f);
        final int z0 = (int)box.z0;
        final int z2 = (int)(box.z1 + 1.0f);
        GL11.glInitNames();
        GL11.glPushName(0);
        GL11.glPushName(0);
        for (int x3 = x0; x3 < x2; ++x3) {
            GL11.glLoadName(x3);
            GL11.glPushName(0);
            for (int y3 = y0; y3 < y2; ++y3) {
                GL11.glLoadName(y3);
                GL11.glPushName(0);
                for (int z3 = z0; z3 < z2; ++z3) {
                    final Tile tile = Tile.tiles[this.level.getTile(x3, y3, z3)];
                    if (tile != null && tile.mayPick() && frustum.isVisible(tile.getTileAABB(x3, y3, z3))) {
                        GL11.glLoadName(z3);
                        GL11.glPushName(0);
                        for (int i = 0; i < 6; ++i) {
                            GL11.glLoadName(i);
                            t.begin();
                            tile.renderFaceNoTexture(player, t, x3, y3, z3, i);
                            t.end();
                        }
                        GL11.glPopName();
                    }
                }
                GL11.glPopName();
            }
            GL11.glPopName();
        }
        GL11.glPopName();
        GL11.glPopName();
    }
    
    public void renderHit(final Player player, final HitResult h, final int mode, final int tileType) {
        final Tesselator t = Tesselator.instance;
        GL11.glEnable(3042);
        GL11.glEnable(3008);
        GL11.glBlendFunc(770, 1);
        GL11.glColor4f(1.0f, 1.0f, 1.0f, ((float)Math.sin(System.currentTimeMillis() / 100.0) * 0.2f + 0.4f) * 0.5f);
        if (mode == 0) {
            t.begin();
            for (int i = 0; i < 6; ++i) {
                Tile.rock.renderFaceNoTexture(player, t, h.x, h.y, h.z, i);
            }
            t.end();
        }
        else {
            GL11.glBlendFunc(770, 771);
            final float br = (float)Math.sin(System.currentTimeMillis() / 100.0) * 0.2f + 0.8f;
            GL11.glColor4f(br, br, br, (float)Math.sin(System.currentTimeMillis() / 200.0) * 0.2f + 0.5f);
            GL11.glEnable(3553);
            final int id = this.textures.loadTexture("/terrain.png", 9728);
            GL11.glBindTexture(3553, id);
            int x = h.x;
            int y = h.y;
            int z = h.z;
            if (h.f == 0) {
                --y;
            }
            if (h.f == 1) {
                ++y;
            }
            if (h.f == 2) {
                --z;
            }
            if (h.f == 3) {
                ++z;
            }
            if (h.f == 4) {
                --x;
            }
            if (h.f == 5) {
                ++x;
            }
            t.begin();
            t.noColor();
            Tile.tiles[tileType].render(t, this.level, 0, x, y, z);
            Tile.tiles[tileType].render(t, this.level, 1, x, y, z);
            t.end();
            GL11.glDisable(3553);
        }
        GL11.glDisable(3042);
        GL11.glDisable(3008);
    }
    
    public void renderHitOutline(final Player player, final HitResult h, final int mode, final int tileType) {
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(0.0f, 0.0f, 0.0f, 0.4f);
        float x = (float)h.x;
        float y = (float)h.y;
        float z = (float)h.z;
        if (mode == 1) {
            if (h.f == 0) {
                --y;
            }
            if (h.f == 1) {
                ++y;
            }
            if (h.f == 2) {
                --z;
            }
            if (h.f == 3) {
                ++z;
            }
            if (h.f == 4) {
                --x;
            }
            if (h.f == 5) {
                ++x;
            }
        }
        GL11.glBegin(3);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x + 1.0f, y, z);
        GL11.glVertex3f(x + 1.0f, y, z + 1.0f);
        GL11.glVertex3f(x, y, z + 1.0f);
        GL11.glVertex3f(x, y, z);
        GL11.glEnd();
        GL11.glBegin(3);
        GL11.glVertex3f(x, y + 1.0f, z);
        GL11.glVertex3f(x + 1.0f, y + 1.0f, z);
        GL11.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
        GL11.glVertex3f(x, y + 1.0f, z + 1.0f);
        GL11.glVertex3f(x, y + 1.0f, z);
        GL11.glEnd();
        GL11.glBegin(1);
        GL11.glVertex3f(x, y, z);
        GL11.glVertex3f(x, y + 1.0f, z);
        GL11.glVertex3f(x + 1.0f, y, z);
        GL11.glVertex3f(x + 1.0f, y + 1.0f, z);
        GL11.glVertex3f(x + 1.0f, y, z + 1.0f);
        GL11.glVertex3f(x + 1.0f, y + 1.0f, z + 1.0f);
        GL11.glVertex3f(x, y, z + 1.0f);
        GL11.glVertex3f(x, y + 1.0f, z + 1.0f);
        GL11.glEnd();
        GL11.glDisable(3042);
    }
    
    public void setDirty(int x0, int y0, int z0, int x1, int y1, int z1) {
        x0 /= 16;
        x1 /= 16;
        y0 /= 16;
        y1 /= 16;
        z0 /= 16;
        z1 /= 16;
        if (x0 < 0) {
            x0 = 0;
        }
        if (y0 < 0) {
            y0 = 0;
        }
        if (z0 < 0) {
            z0 = 0;
        }
        if (x1 >= this.xChunks) {
            x1 = this.xChunks - 1;
        }
        if (y1 >= this.yChunks) {
            y1 = this.yChunks - 1;
        }
        if (z1 >= this.zChunks) {
            z1 = this.zChunks - 1;
        }
        for (int x2 = x0; x2 <= x1; ++x2) {
            for (int y2 = y0; y2 <= y1; ++y2) {
                for (int z2 = z0; z2 <= z1; ++z2) {
                    this.chunks[(x2 + y2 * this.xChunks) * this.zChunks + z2].setDirty();
                }
            }
        }
    }
    
    public void tileChanged(final int x, final int y, final int z) {
        this.setDirty(x - 1, y - 1, z - 1, x + 1, y + 1, z + 1);
    }
    
    public void lightColumnChanged(final int x, final int z, final int y0, final int y1) {
        this.setDirty(x - 1, y0 - 1, z - 1, x + 1, y1 + 1, z + 1);
    }
    
    public void toggleDrawDistance() {
        this.drawDistance = (this.drawDistance + 1) % 4;
    }
    
    public void cull(final Frustum frustum) {
        for (int i = 0; i < this.chunks.length; ++i) {
            this.chunks[i].visible = frustum.isVisible(this.chunks[i].aabb);
        }
    }
}
