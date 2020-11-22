// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.particle;

import com.mojang.minecraft.renderer.Tesselator;
import org.lwjgl.opengl.GL11;
import com.mojang.minecraft.Player;
import java.util.ArrayList;
import com.mojang.minecraft.renderer.Textures;
import java.util.List;
import com.mojang.minecraft.level.Level;

public class ParticleEngine
{
    protected Level level;
    private List<Particle> particles;
    private Textures textures;
    
    public ParticleEngine(final Level level, final Textures textures) {
        this.particles = new ArrayList<Particle>();
        this.level = level;
        this.textures = textures;
    }
    
    public void add(final Particle p) {
        this.particles.add(p);
    }
    
    public void tick() {
        for (int i = 0; i < this.particles.size(); ++i) {
            final Particle p = this.particles.get(i);
            p.tick();
            if (p.removed) {
                this.particles.remove(i--);
            }
        }
    }
    
    public void render(final Player player, final float a, final int layer) {
        if (this.particles.size() == 0) {
            return;
        }
        GL11.glEnable(3553);
        final int id = this.textures.loadTexture("/terrain.png", 9728);
        GL11.glBindTexture(3553, id);
        final float xa = -(float)Math.cos(player.yRot * 3.141592653589793 / 180.0);
        final float za = -(float)Math.sin(player.yRot * 3.141592653589793 / 180.0);
        final float xa2 = -za * (float)Math.sin(player.xRot * 3.141592653589793 / 180.0);
        final float za2 = xa * (float)Math.sin(player.xRot * 3.141592653589793 / 180.0);
        final float ya = (float)Math.cos(player.xRot * 3.141592653589793 / 180.0);
        final Tesselator t = Tesselator.instance;
        GL11.glColor4f(0.8f, 0.8f, 0.8f, 1.0f);
        t.begin();
        for (int i = 0; i < this.particles.size(); ++i) {
            final Particle p = this.particles.get(i);
            if (p.isLit() ^ layer == 1) {
                p.render(t, a, xa, ya, za, xa2, za2);
            }
        }
        t.end();
        GL11.glDisable(3553);
    }
}
