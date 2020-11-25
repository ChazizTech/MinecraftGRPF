// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level.tile;

import com.mojang.minecraft.particle.Particle;
import com.mojang.minecraft.particle.ParticleEngine;
import java.util.Random;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.Player;
import com.mojang.minecraft.level.Level;
import com.mojang.minecraft.renderer.Tesselator;

public class Tile
{
    public static final int NOT_LIQUID = 0;
    public static final int LIQUID_WATER = 1;
    public static final int LIQUID_LAVA = 2;
    public static final Tile[] tiles;
    public static final boolean[] shouldTick;
    public static final Tile empty;
    public static final Tile rock;
    public static final Tile grass;
    public static final Tile dirt;
    public static final Tile stoneBrick;
    public static final Tile wood;
    public static final Tile bush;
    public static final Tile unbreakable;
    public static final Tile water;
    public static final Tile calmWater;
    public static final Tile lava;
    public static final Tile calmLava;
    public static final Tile WhiteCloth;
    public static final Tile GrayCloth;
    public static final Tile BlackCloth;
    public static final Tile HotCloth;
    public static final Tile MageCloth;
    public static final Tile Purp1Cloth;
    public static final Tile Purp2Cloth;
    public static final Tile BlueCloth;
    public static final Tile BlueBrightCloth;
    public static final Tile YellCloth;
    public static final Tile TealCloth;
    public static final Tile GreenCloth;
    public static final Tile GreenLimeCloth;
    public static final Tile OrangeCloth;
    public static final Tile RedCloth;
    public static final Tile Bob;
    public static final Tile BlueGuy;
    public static final Tile BlackMud;
    public static final Tile Glass;
    public static final Tile DarkGlass;
    public int tex;
    public final int id;
    protected float xx0;
    protected float yy0;
    protected float zz0;
    protected float xx1;
    protected float yy1;
    protected float zz1;
    
    static {
        tiles = new Tile[256];
        shouldTick = new boolean[256];
        empty = null;
        rock = new Tile(1, 1);
        grass = new GrassTile(2);
        dirt = new DirtTile(3, 2);
        stoneBrick = new Tile(4, 16);
        wood = new Tile(5, 4);
        bush = new Bush(6);
        unbreakable = new Tile(7, 17);
        water = new LiquidTile(8, 1);
        calmWater = new CalmLiquidTile(9, 1);
        lava = new LiquidTile(10, 2);
        calmLava = new CalmLiquidTile(11, 2);
        WhiteCloth = new ClothWhite(12);
        GrayCloth = new ClothGray(13);
        BlackCloth = new ClothBlack(14);
        HotCloth = new ClothHotPink(15);
        MageCloth = new ClothMagenta(16);
        Purp1Cloth = new ClothPurple(17);
        Purp2Cloth = new ClothPurple2(18);
        BlueCloth = new ClothBlue(19);
        BlueBrightCloth = new ClothLightblue(20);
        YellCloth = new ClothYellow(21);
        GreenCloth = new ClothGreen(22);
        GreenLimeCloth = new ClothLime(23);
        TealCloth = new ClothLime(24);
        OrangeCloth = new ClothOrange(25);
        RedCloth = new ClothRed(26);
        Bob = new BobFur(27);
        BlueGuy = new BlueGuy(28);
        BlackMud = new DarkMud(29);
        Glass = new Glass(30);
        DarkGlass = new GlassDark(31);
    }
    
    protected Tile(final int id) {
        Tile.tiles[id] = this;
        this.id = id;
        this.setShape(0.0f, 0.0f, 0.0f, 1.0f, 1.0f, 1.0f);
    }
    
    protected void setTicking(final boolean tick) {
        Tile.shouldTick[this.id] = tick;
    }
    
    protected void setShape(final float x0, final float y0, final float z0, final float x1, final float y1, final float z1) {
        this.xx0 = x0;
        this.yy0 = y0;
        this.zz0 = z0;
        this.xx1 = x1;
        this.yy1 = y1;
        this.zz1 = z1;
    }
    
    protected Tile(final int id, final int tex) {
        this(id);
        this.tex = tex;
    }
    
    public void render(final Tesselator t, final Level level, final int layer, final int x, final int y, final int z) {
        final byte c1 = -1;
        final byte c2 = -52;
        final byte c3 = -103;
        if (this.shouldRenderFace(level, x, y - 1, z, layer, 0)) {
            t.color(c1, c1, c1);
            this.renderFace(t, x, y, z, 0);
        }
        if (this.shouldRenderFace(level, x, y + 1, z, layer, 1)) {
            t.color(c1, c1, c1);
            this.renderFace(t, x, y, z, 1);
        }
        if (this.shouldRenderFace(level, x, y, z - 1, layer, 2)) {
            t.color(c2, c2, c2);
            this.renderFace(t, x, y, z, 2);
        }
        if (this.shouldRenderFace(level, x, y, z + 1, layer, 3)) {
            t.color(c2, c2, c2);
            this.renderFace(t, x, y, z, 3);
        }
        if (this.shouldRenderFace(level, x - 1, y, z, layer, 4)) {
            t.color(c3, c3, c3);
            this.renderFace(t, x, y, z, 4);
        }
        if (this.shouldRenderFace(level, x + 1, y, z, layer, 5)) {
            t.color(c3, c3, c3);
            this.renderFace(t, x, y, z, 5);
        }
    }
    
    protected boolean shouldRenderFace(final Level level, final int x, final int y, final int z, final int layer, final int face) {
        boolean layerOk = true;
        if (layer == 2) {
            return false;
        }
        if (layer >= 0) {
            layerOk = (level.isLit(x, y, z) ^ layer == 1);
        }
        return !level.isSolidTile(x, y, z) && layerOk;
    }
    
    protected int getTexture(final int face) {
        return this.tex;
    }
    
    public void renderFace(final Tesselator t, final int x, final int y, final int z, final int face) {
        final int tex = this.getTexture(face);
        final int xt = tex % 16 * 16;
        final int yt = tex / 16 * 16;
        final float u0 = xt / 256.0f;
        final float u2 = (xt + 15.99f) / 256.0f;
        final float v0 = yt / 256.0f;
        final float v2 = (yt + 15.99f) / 256.0f;
        final float x2 = x + this.xx0;
        final float x3 = x + this.xx1;
        final float y2 = y + this.yy0;
        final float y3 = y + this.yy1;
        final float z2 = z + this.zz0;
        final float z3 = z + this.zz1;
        if (face == 0) {
            t.vertexUV(x2, y2, z3, u0, v2);
            t.vertexUV(x2, y2, z2, u0, v0);
            t.vertexUV(x3, y2, z2, u2, v0);
            t.vertexUV(x3, y2, z3, u2, v2);
            return;
        }
        if (face == 1) {
            t.vertexUV(x3, y3, z3, u2, v2);
            t.vertexUV(x3, y3, z2, u2, v0);
            t.vertexUV(x2, y3, z2, u0, v0);
            t.vertexUV(x2, y3, z3, u0, v2);
            return;
        }
        if (face == 2) {
            t.vertexUV(x2, y3, z2, u2, v0);
            t.vertexUV(x3, y3, z2, u0, v0);
            t.vertexUV(x3, y2, z2, u0, v2);
            t.vertexUV(x2, y2, z2, u2, v2);
            return;
        }
        if (face == 3) {
            t.vertexUV(x2, y3, z3, u0, v0);
            t.vertexUV(x2, y2, z3, u0, v2);
            t.vertexUV(x3, y2, z3, u2, v2);
            t.vertexUV(x3, y3, z3, u2, v0);
            return;
        }
        if (face == 4) {
            t.vertexUV(x2, y3, z3, u2, v0);
            t.vertexUV(x2, y3, z2, u0, v0);
            t.vertexUV(x2, y2, z2, u0, v2);
            t.vertexUV(x2, y2, z3, u2, v2);
            return;
        }
        if (face == 5) {
            t.vertexUV(x3, y2, z3, u0, v2);
            t.vertexUV(x3, y2, z2, u2, v2);
            t.vertexUV(x3, y3, z2, u2, v0);
            t.vertexUV(x3, y3, z3, u0, v0);
        }
    }
    
    public void renderBackFace(final Tesselator t, final int x, final int y, final int z, final int face) {
        final int tex = this.getTexture(face);
        final float u0 = tex % 16 / 16.0f;
        final float u2 = u0 + 0.0624375f;
        final float v0 = tex / 16 / 16.0f;
        final float v2 = v0 + 0.0624375f;
        final float x2 = x + this.xx0;
        final float x3 = x + this.xx1;
        final float y2 = y + this.yy0;
        final float y3 = y + this.yy1;
        final float z2 = z + this.zz0;
        final float z3 = z + this.zz1;
        if (face == 0) {
            t.vertexUV(x3, y2, z3, u2, v2);
            t.vertexUV(x3, y2, z2, u2, v0);
            t.vertexUV(x2, y2, z2, u0, v0);
            t.vertexUV(x2, y2, z3, u0, v2);
        }
        if (face == 1) {
            t.vertexUV(x2, y3, z3, u0, v2);
            t.vertexUV(x2, y3, z2, u0, v0);
            t.vertexUV(x3, y3, z2, u2, v0);
            t.vertexUV(x3, y3, z3, u2, v2);
        }
        if (face == 2) {
            t.vertexUV(x2, y2, z2, u2, v2);
            t.vertexUV(x3, y2, z2, u0, v2);
            t.vertexUV(x3, y3, z2, u0, v0);
            t.vertexUV(x2, y3, z2, u2, v0);
        }
        if (face == 3) {
            t.vertexUV(x3, y3, z3, u2, v0);
            t.vertexUV(x3, y2, z3, u2, v2);
            t.vertexUV(x2, y2, z3, u0, v2);
            t.vertexUV(x2, y3, z3, u0, v0);
        }
        if (face == 4) {
            t.vertexUV(x2, y2, z3, u2, v2);
            t.vertexUV(x2, y2, z2, u0, v2);
            t.vertexUV(x2, y3, z2, u0, v0);
            t.vertexUV(x2, y3, z3, u2, v0);
        }
        if (face == 5) {
            t.vertexUV(x3, y3, z3, u0, v0);
            t.vertexUV(x3, y3, z2, u2, v0);
            t.vertexUV(x3, y2, z2, u2, v2);
            t.vertexUV(x3, y2, z3, u0, v2);
        }
    }
    
    public void renderFaceNoTexture(final Player player, final Tesselator t, final int x, final int y, final int z, final int face) {
        final float x2 = x + 0.0f;
        final float x3 = x + 1.0f;
        final float y2 = y + 0.0f;
        final float y3 = y + 1.0f;
        final float z2 = z + 0.0f;
        final float z3 = z + 1.0f;
        if (face == 0 && y > player.y) {
            t.vertex(x2, y2, z3);
            t.vertex(x2, y2, z2);
            t.vertex(x3, y2, z2);
            t.vertex(x3, y2, z3);
        }
        if (face == 1 && y < player.y) {
            t.vertex(x3, y3, z3);
            t.vertex(x3, y3, z2);
            t.vertex(x2, y3, z2);
            t.vertex(x2, y3, z3);
        }
        if (face == 2 && z > player.z) {
            t.vertex(x2, y3, z2);
            t.vertex(x3, y3, z2);
            t.vertex(x3, y2, z2);
            t.vertex(x2, y2, z2);
        }
        if (face == 3 && z < player.z) {
            t.vertex(x2, y3, z3);
            t.vertex(x2, y2, z3);
            t.vertex(x3, y2, z3);
            t.vertex(x3, y3, z3);
        }
        if (face == 4 && x > player.x) {
            t.vertex(x2, y3, z3);
            t.vertex(x2, y3, z2);
            t.vertex(x2, y2, z2);
            t.vertex(x2, y2, z3);
        }
        if (face == 5 && x < player.x) {
            t.vertex(x3, y2, z3);
            t.vertex(x3, y2, z2);
            t.vertex(x3, y3, z2);
            t.vertex(x3, y3, z3);
        }
    }
    
    public final AABB getTileAABB(final int x, final int y, final int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }
    
    public AABB getAABB(final int x, final int y, final int z) {
        return new AABB((float)x, (float)y, (float)z, (float)(x + 1), (float)(y + 1), (float)(z + 1));
    }
    
    public boolean blocksLight() {
        return true;
    }
    
    public boolean isSolid() {
        return true;
    }
    
    public boolean mayPick() {
        return true;
    }
    
    public void tick(final Level level, final int x, final int y, final int z, final Random random) {
    }
    
    public void destroy(final Level level, final int x, final int y, final int z, final ParticleEngine particleEngine) {
        for (int SD = 4, xx = 0; xx < SD; ++xx) {
            for (int yy = 0; yy < SD; ++yy) {
                for (int zz = 0; zz < SD; ++zz) {
                    final float xp = x + (xx + 0.5f) / SD;
                    final float yp = y + (yy + 0.5f) / SD;
                    final float zp = z + (zz + 0.5f) / SD;
                    particleEngine.add(new Particle(level, xp, yp, zp, xp - x - 0.5f, yp - y - 0.5f, zp - z - 0.5f, this.tex));
                }
            }
        }
    }
    
    public int getLiquidType() {
        return 0;
    }
    
    public void neighborChanged(final Level level, final int x, final int y, final int z, final int type) {
    }
}
