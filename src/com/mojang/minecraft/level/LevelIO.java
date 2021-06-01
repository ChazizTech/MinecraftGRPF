// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.level;

import java.io.DataOutputStream;
import java.util.zip.GZIPOutputStream;
import java.io.OutputStream;
import java.io.DataInputStream;
import java.util.zip.GZIPInputStream;
import java.io.InputStream;

import com.mojang.minecraft.Minecraft;

public class LevelIO
{
    private static final int MAGIC_NUMBER = 656127880;
    private static final int CURRENT_VERSION = 1;
    private LevelLoaderListener levelLoaderListener;
    public String error;
    
    public LevelIO(final LevelLoaderListener levelLoaderListener) {
        this.error = null;
        this.levelLoaderListener = levelLoaderListener;
    }
    
    public boolean load(final Level level, final InputStream in) {
    	System.out.println("Save file exists, loading.");
        this.levelLoaderListener.beginLevelLoading("Loading level");
        this.levelLoaderListener.levelLoadUpdate("Reading..");
        try {
            final DataInputStream dis = new DataInputStream(new GZIPInputStream(in));
            final int magic = dis.readInt();
            if (magic != MAGIC_NUMBER) {
                this.error = "Bad level file format";
                return false;
            }
            final byte version = dis.readByte();
            if (version > CURRENT_VERSION) {
                this.error = "Bad level file format";
                return false;
            }
            final String name = dis.readUTF();
            final String creator = dis.readUTF();
            final long createTime = dis.readLong();
            final int width = dis.readShort();
            final int height = dis.readShort();
            final int depth = dis.readShort();
            final byte[] blocks = new byte[width * height * depth];
            dis.readFully(blocks);
            dis.close();
            level.setData(width, depth, height, blocks);
            level.name = name;
            level.creator = creator;
            level.createTime = createTime;
            System.out.println(level.name + " by " + level.creator + ". ");
            Minecraft.nameLevel = level.name;
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.error = "Failed to load level: " + e.toString();
            return false;
        }
    }
    
    public boolean loadLegacy(final Level level, final InputStream in) {
    	System.out.println("Save file does exist, but it's in a legacy pre-classic format, loading with alternative function.");
        this.levelLoaderListener.beginLevelLoading("Loading legacy level");
        this.levelLoaderListener.levelLoadUpdate("Reading..");
        try {
            final DataInputStream dis = new DataInputStream(new GZIPInputStream(in));
            final String name = "--";
            final String creator = "unknown";
            final long createTime = 0L;
            final int width = 256;
            final int height = 256;
            final int depth = 64;
            final byte[] blocks = new byte[width * height * depth];
            dis.readFully(blocks);
            dis.close();
            level.setData(width, depth, height, blocks);
            level.name = name;
            level.creator = creator;
            level.createTime = createTime;
            return true;
        }
        catch (Exception e) {
            e.printStackTrace();
            this.error = "Failed to load level: " + e.toString();
            return false;
        }
    }
    
    public void save(final Level level, final OutputStream out) {
        try {
            final DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(out));
            dos.writeInt(MAGIC_NUMBER);
            dos.writeByte(CURRENT_VERSION);
            dos.writeUTF(level.name);
            dos.writeUTF(level.creator);
            dos.writeLong(level.createTime);
            dos.writeShort(level.width);
            dos.writeShort(level.height);
            dos.writeShort(level.depth);
            dos.write(level.blocks);
            dos.close();
			System.out.println("Saved.");
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
