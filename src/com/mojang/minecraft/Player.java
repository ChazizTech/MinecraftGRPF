// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft;

import com.mojang.minecraft.level.Level;

public class Player extends Entity
{
    public static final int KEY_UP = 0;
    public static final int KEY_DOWN = 1;
    public static final int KEY_LEFT = 2;
    public static final int KEY_RIGHT = 3;
    public static final int KEY_JUMP = 4;
    private boolean[] keys;
    
    public Player(final Level level) {
        super(level);
        this.keys = new boolean[10];
        this.heightOffset = 1.62f;
    }
    
    public void setKey(final int key, final boolean state) {
        int id = -1;
        if (key == 200 || key == 17) {
            id = 0;
        }
        if (key == 208 || key == 31) {
            id = 1;
        }
        if (key == 203 || key == 30) {
            id = 2;
        }
        if (key == 205 || key == 32) {
            id = 3;
        }
        if (key == 57 || key == 219) {
            id = 4;
        }
        if (id >= 0) {
            this.keys[id] = state;
        }
    }
    
    public void releaseAllKeys() {
        for (int i = 0; i < 10; ++i) {
            this.keys[i] = false;
        }
    }
    
    @Override
    public void tick() {
        this.xo = this.x;
        this.yo = this.y;
        this.zo = this.z;
        float xa = 0.0f;
        float ya = 0.0f;
        final boolean inWater = this.isInWater();
        final boolean inLava = this.isInLava();
        if (this.keys[0]) {
            --ya;
        }
        if (this.keys[1]) {
            ++ya;
        }
        if (this.keys[2]) {
            --xa;
        }
        if (this.keys[3]) {
            ++xa;
        }
        if (this.keys[4]) {
            if (inWater) {
                this.yd += 0.04f;
            }
            else if (inLava) {
                this.yd += 0.04f;
            }
            else if (this.onGround) {
                this.yd = 0.42f;
                this.keys[4] = false;
            }
        }
        if (inWater) {
            final float yo = this.y;
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.8f;
            this.yd *= 0.8f;
            this.zd *= 0.8f;
            this.yd -= (float)0.02;
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6f - this.y + yo, this.zd)) {
                this.yd = 0.3f;
            }
        }
        else if (inLava) {
            final float yo = this.y;
            this.moveRelative(xa, ya, 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.5f;
            this.yd *= 0.5f;
            this.zd *= 0.5f;
            this.yd -= (float)0.02;
            if (this.horizontalCollision && this.isFree(this.xd, this.yd + 0.6f - this.y + yo, this.zd)) {
                this.yd = 0.3f;
            }
        }
        else {
            this.moveRelative(xa, ya, this.onGround ? 0.1f : 0.02f);
            this.move(this.xd, this.yd, this.zd);
            this.xd *= 0.91f;
            this.yd *= 0.98f;
            this.zd *= 0.91f;
            this.yd -= (float)0.08;
            if (this.onGround) {
                this.xd *= 0.6f;
                this.zd *= 0.6f;
            }
        }
    }
}
