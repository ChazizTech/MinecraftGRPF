// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.gui;

import java.io.File;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;

import com.mojang.minecraft.gui.Font;
import com.mojang.minecraft.Minecraft;
import com.mojang.minecraft.level.LevelIO;


public class PauseScreen extends Screen
{
    private List<Button> buttons;
    public Font font;
    
    public PauseScreen() {
        this.buttons = new ArrayList<Button>();
    }
    
    @Override
    public void init() {
    	attemptSaveLevel();
    	System.out.println("Level saved? [PAUSE MENU]");
        this.buttons.add(new Button(0, this.width / 2 - 100, this.height / 4 + 32, 200, 20, "Generate new level"));
        //this.buttons.add(new Button(1, this.width / 2 - 100, this.height / 4 + 96, 200, 20, "Save level.."));
        //this.buttons.add(new Button(2, this.width / 2 - 100, this.height / 4 + 128, 200, 20, "Load level.."));
        this.buttons.add(new Button(3, this.width / 2 - 100, this.height / 4 + 64, 97, 20, "Back to game"));
        this.buttons.add(new Button(4, this.width / 2 - 0, this.height / 4 + 64, 100, 20, "Exit"));
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
        if (button.id == 1) {
        	attemptSaveLevel();
        	this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
            System.out.println("Level saved? [PAUSE MENU BUTTON]");
        }
        if (button.id == 3) {
            this.minecraft.setScreen(null);
            this.minecraft.grabMouse();
        }
        if (button.id == 4) {
        	System.exit(0);
        }
    }
    
    public void attemptSaveLevel() {
        try {
        	Minecraft.levelIo.save(Minecraft.level, new FileOutputStream(new File("level.dat")));
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    @Override
    public void render(final int xm, final int ym) {
        this.fillGradient(0, 0, this.width, this.height, 537199872, -1607454624);
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
