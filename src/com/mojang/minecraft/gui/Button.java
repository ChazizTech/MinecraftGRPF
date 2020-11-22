// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.gui;

public class Button
{
    public int x;
    public int y;
    public int w;
    public int h;
    public String msg;
    public int id;
    
    public Button(final int id, final int x, final int y, final int w, final int h, final String msg) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.w = w;
        this.h = h;
        this.msg = msg;
    }
}
