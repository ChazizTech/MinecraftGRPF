// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.gui;


import javax.swing.JOptionPane;

import org.lwjgl.input.Keyboard;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.GL11;
import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.Minecraft;

public class Screen
{
    protected Minecraft minecraft;
    protected int width;
    protected int height;
    
    public void render(final int xMouse, final int yMouse) {
    }
    
    public void init(final Minecraft minecraft, final int width, final int height) {
        this.minecraft = minecraft;
        this.width = width;
        this.height = height;
        this.init();
    }
    
    public void init() {
    }
    
    protected void fill(final int x0, final int y0, final int x1, final int y1, final int col) {
        final float a = (col >> 24 & 0xFF) / 255.0f;
        final float r = (col >> 16 & 0xFF) / 255.0f;
        final float g = (col >> 8 & 0xFF) / 255.0f;
        final float b = (col & 0xFF) / 255.0f;
        final Tesselator t = Tesselator.instance;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glColor4f(r, g, b, a);
        t.begin();
        t.vertex((float)x0, (float)y1, 0.0f);
        t.vertex((float)x1, (float)y1, 0.0f);
        t.vertex((float)x1, (float)y0, 0.0f);
        t.vertex((float)x0, (float)y0, 0.0f);
        t.end();
        GL11.glDisable(3042);
    }
    
    protected void fillGradient(final int x0, final int y0, final int x1, final int y1, final int col1, final int col2) {
        final float a1 = (col1 >> 24 & 0xFF) / 255.0f;
        final float r1 = (col1 >> 16 & 0xFF) / 255.0f;
        final float g1 = (col1 >> 8 & 0xFF) / 255.0f;
        final float b1 = (col1 & 0xFF) / 255.0f;
        final float a2 = (col2 >> 24 & 0xFF) / 255.0f;
        final float r2 = (col2 >> 16 & 0xFF) / 255.0f;
        final float g2 = (col2 >> 8 & 0xFF) / 255.0f;
        final float b2 = (col2 & 0xFF) / 255.0f;
        GL11.glEnable(3042);
        GL11.glBlendFunc(770, 771);
        GL11.glBegin(7);
        GL11.glColor4f(r1, g1, b1, a1);
        GL11.glVertex2f((float)x1, (float)y0);
        GL11.glVertex2f((float)x0, (float)y0);
        GL11.glColor4f(r2, g2, b2, a2);
        GL11.glVertex2f((float)x0, (float)y1);
        GL11.glVertex2f((float)x1, (float)y1);
        GL11.glEnd();
        GL11.glDisable(3042);
    }
    
    public void drawCenteredString(final String str, final int x, final int y, final int color) {
        final Font font = this.minecraft.font;
        font.drawShadow(str, x - font.width(str) / 2, y, color);
    }
    
    public void drawString(final String str, final int x, final int y, final int color) {
        final Font font = this.minecraft.font;
        font.drawShadow(str, x, y, color);
    }
    
    public void updateEvents() {
        while (Mouse.next()) {
            if (Mouse.getEventButtonState()) {
                final int xm = Mouse.getEventX() * this.width / this.minecraft.width;
                final int ym = this.height - Mouse.getEventY() * this.height / this.minecraft.height - 1;
                this.mouseClicked(xm, ym, Mouse.getEventButton());
            }
        }
        while (Keyboard.next()) {
            if (Keyboard.getEventKeyState()) {
                this.keyPressed(Keyboard.getEventCharacter(), Keyboard.getEventKey());
            }
        }
    }
    
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    protected void mouseClicked(final int x, final int y, final int button) {
    }
    
    public void tick() {
    }
}
