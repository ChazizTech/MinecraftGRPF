// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.renderer;

import java.nio.ByteBuffer;
import java.awt.image.BufferedImage;
import java.nio.IntBuffer;
import java.io.IOException;
import org.lwjgl.util.glu.GLU;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.BufferUtils;
import java.util.HashMap;

public class Textures
{
    private HashMap<String, Integer> idMap;
    
    public Textures() {
        this.idMap = new HashMap<String, Integer>();
    }
    
    public int loadTexture(final String resourceName, final int mode) {
        try {
            if (this.idMap.containsKey(resourceName)) {
                return this.idMap.get(resourceName);
            }
            final IntBuffer ib = BufferUtils.createIntBuffer(1);
            ib.clear();
            GL11.glGenTextures(ib);
            final int id = ib.get(0);
            this.idMap.put(resourceName, id);
            GL11.glBindTexture(3553, id);
            GL11.glTexParameteri(3553, 10241, mode);
            GL11.glTexParameteri(3553, 10240, mode);
            final BufferedImage img = ImageIO.read(Textures.class.getResourceAsStream(resourceName));
            final int w = img.getWidth();
            final int h = img.getHeight();
            final ByteBuffer pixels = BufferUtils.createByteBuffer(w * h * 4);
            final int[] rawPixels = new int[w * h];
            final byte[] newPixels = new byte[w * h * 4];
            img.getRGB(0, 0, w, h, rawPixels, 0, w);
            for (int i = 0; i < rawPixels.length; ++i) {
                final int a = rawPixels[i] >> 24 & 0xFF;
                final int r = rawPixels[i] >> 16 & 0xFF;
                final int g = rawPixels[i] >> 8 & 0xFF;
                final int b = rawPixels[i] & 0xFF;
                newPixels[i * 4 + 0] = (byte)r;
                newPixels[i * 4 + 1] = (byte)g;
                newPixels[i * 4 + 2] = (byte)b;
                newPixels[i * 4 + 3] = (byte)a;
            }
            pixels.put(newPixels);
            pixels.position(0).limit(newPixels.length);
            GLU.gluBuild2DMipmaps(3553, 6408, w, h, 6408, 5121, pixels);
    		System.out.println("Loaded texture " + (resourceName));
            return id;
        }
        catch (IOException e) {
            throw new RuntimeException("Couldn't load texture");
        }
    }
}
