// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.gui;

import java.util.ArrayList;
import java.util.List;

import org.lwjgl.opengl.GL11;

import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Textures;
import com.mojang.minecraft.gui.Screen;
import com.mojang.minecraft.gui.Font;

public class MenuScreen extends Screen
{
    private List<Button> buttons;
    public Textures textures;
    
    public MenuScreen() {
        this.buttons = new ArrayList<Button>();
    }
    
    @Override
    public void init() {
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 0, 200, 20, "Generate new level"));
        this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 32, 200, 20, "Save level.."));
        this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 64, 200, 20, "Load level.."));
        this.buttons.add(new Button(3, this.width / 2 - 100, this.height / 4 + 96, 200, 20, "Back to game"));
        this.buttons.add(new Button(4, this.width / 2 - 100, this.height / 4 + 128, 200, 20, "Exit"));
    }
    
    @Override
    protected void keyPressed(final char eventCharacter, final int eventKey) {
    }
    
    @Override
    protected void mouseClicked(final int x, final int y, final int buttonNum) {
        if (buttonNum == 0) {
            for (int i = 0; i < this.buttons.size(); ++i) {
                final Button button = this.buttons.get(i);
                if (x >= button.x && y >= button.y && x < button.x + button.w && y < button.y + button.h) {
                    this.buttonClicked(button);
                }
            }
        }
    }
    
    private void buttonClicked(final Button button) {
        if (button.id == 0) {
            this.minecraft.generateNewLevel();
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        if (button.id == 3) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        if (button.id == 4) {
        	System.exit(0);
        }
    }
    
    @Override
    public void render(final int xm, final int ym) {
    	this.fill(0, 0, this.width, this.height, 8421504);
        for (int i = 0; i < this.buttons.size(); ++i) {
            final Button button = this.buttons.get(i);
            this.fill(button.x - 1, button.y - 1, button.x + button.w + 1, button.y + button.h + 1, -16777216);
            if (xm >= button.x && ym >= button.y && xm < button.x + button.w && ym < button.y + button.h) {
                this.fill(button.x - 1, button.y - 1, button.x + button.w + 1, button.y + button.h + 1, -6250336);
                this.fill(button.x, button.y, button.x + button.w, button.y + button.h, -8355680);
                this.drawCenteredString(button.msg, button.x + button.w / 2, button.y + (button.h - 8) / 2, 16777120);
            }
            else {
                this.fill(button.x, button.y, button.x + button.w, button.y + button.h, -9408400);
                this.drawCenteredString(button.msg, button.x + button.w / 2, button.y + (button.h - 8) / 2, 14737632);
            }
        }
    }
}
