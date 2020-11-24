// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft;

import com.mojang.minecraft.renderer.Tesselator;
import com.mojang.minecraft.renderer.Frustum;
import com.mojang.minecraft.phys.AABB;
import com.mojang.minecraft.level.tile.Tile;
import com.mojang.minecraft.gui.PauseScreen;
import com.mojang.minecraft.gui.MenuScreen;
import com.mojang.minecraft.level.Chunk;
import java.awt.Component;
import javax.swing.JOptionPane;
import java.io.OutputStream;
import java.io.FileOutputStream;
import org.lwjgl.util.glu.GLU;
import java.io.IOException;
import com.mojang.minecraft.character.Zombie;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import org.lwjgl.opengl.GL11;
import org.lwjgl.input.Mouse;
import org.lwjgl.input.Keyboard;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.Display;
import org.lwjgl.BufferUtils;
import java.nio.IntBuffer;
import com.mojang.minecraft.level.levelgen.LevelGen;
import com.mojang.minecraft.level.LevelIO;
import com.mojang.minecraft.gui.Screen;
import com.mojang.minecraft.gui.Font;
import com.mojang.minecraft.renderer.Textures;
import org.lwjgl.input.Cursor;
import java.awt.Canvas;
import java.util.ArrayList;
import com.mojang.minecraft.particle.ParticleEngine;
import com.mojang.minecraft.level.LevelRenderer;
import com.mojang.minecraft.level.Level;
import java.nio.FloatBuffer;
import com.mojang.minecraft.level.LevelLoaderListener;

public class Minecraft implements Runnable, LevelLoaderListener
{
    public static final String VERSION_STRING = "0.1.0";
    private boolean fullscreen;
    public int width;
    public int height;
    private FloatBuffer fogColor0;
    private FloatBuffer fogColor1;
    private Timer timer;
    public static Level level;
    private LevelRenderer levelRenderer;
    private Player player;
    private int paintTexture;
    private ParticleEngine particleEngine;
    public User user;
    private ArrayList<Entity> entities;
    private Canvas parent;
    public boolean appletMode;
    public volatile boolean pause;
    private Cursor emptyCursor;
    private int yMouseAxis;
    public Textures textures;
    public Font font;
    private int editMode;
    private Screen screen;
    public static LevelIO levelIo;
    private LevelGen levelGen;
    private volatile boolean running;
    private String fpsString;
    private String cordString;
    private boolean mouseGrabbed;
    private IntBuffer viewportBuffer;
    private IntBuffer selectBuffer;
    private HitResult hitResult;
    FloatBuffer lb;
    private String title;
    
    public Minecraft(final Canvas parent, final int width, final int height, final boolean fullscreen) {
        this.fullscreen = false;
        this.fogColor0 = BufferUtils.createFloatBuffer(4);
        this.fogColor1 = BufferUtils.createFloatBuffer(4);
        this.timer = new Timer(20.0f);
        this.paintTexture = 1;
        this.user = new User("noname");
        this.entities = new ArrayList<Entity>();
        this.appletMode = false;
        this.pause = false;
        this.yMouseAxis = 1;
        this.editMode = 0;
        this.screen = null;
        Minecraft.levelIo = new LevelIO(this);
        this.levelGen = new LevelGen(this);
        this.running = false;
        this.fpsString = "";
        this.mouseGrabbed = false;
        this.viewportBuffer = BufferUtils.createIntBuffer(16);
        this.selectBuffer = BufferUtils.createIntBuffer(2000);
        this.hitResult = null;
        this.lb = BufferUtils.createFloatBuffer(16);
        this.title = "";
        this.parent = parent;
        this.width = width;
        this.height = height;
        this.fullscreen = fullscreen;
        this.textures = new Textures();
    }
    
    public void init() throws LWJGLException, IOException {
        final int col1 = 920330;
        final float fr = 0.5f;
        final float fg = 0.8f;
        final float fb = 1.0f;
        this.fogColor0.put(new float[] { fr, fg, fb, 1.0f });
        this.fogColor0.flip();
        this.fogColor1.put(new float[] { (col1 >> 16 & 0xFF) / 255.0f, (col1 >> 8 & 0xFF) / 255.0f, (col1 & 0xFF) / 255.0f, 1.0f });
        this.fogColor1.flip();
        if (this.parent != null) {
            Display.setParent(this.parent);
        }
        else if (this.fullscreen) {
            Display.setFullscreen(true);
            this.width = Display.getDisplayMode().getWidth();
            this.height = Display.getDisplayMode().getHeight();
        }
        else {
            Display.setDisplayMode(new DisplayMode(this.width, this.height));
        }
        Display.setTitle("MinecraftGRPF 0.1.0b");
        try {
            Display.create();
        }
        catch (LWJGLException e) {
            e.printStackTrace();
            try {
                Thread.sleep(1000L);
            }
            catch (InterruptedException ex) {}
            Display.create();
        }
        Keyboard.create();
        Mouse.create();
        this.checkGlError("Pre startup");
        GL11.glEnable(3553);
        GL11.glShadeModel(7425);
        GL11.glClearColor(fr, fg, fb, 0.0f);
        GL11.glClearDepth(1.0);
        GL11.glEnable(2929);
        GL11.glDepthFunc(515);
        GL11.glEnable(3008);
        GL11.glAlphaFunc(516, 0.0f);
        GL11.glCullFace(1029);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glMatrixMode(5888);
        this.checkGlError("Startup");
        this.font = new Font("/default.gif", this.textures);
        final IntBuffer imgData = BufferUtils.createIntBuffer(256);
        imgData.clear().limit(256);
        GL11.glViewport(0, 0, this.width, this.height);
        this.setScreen(new MenuScreen());
        Minecraft.level = new Level();
        boolean success = false;
        try {
            success = Minecraft.levelIo.load(Minecraft.level, new FileInputStream(new File("level.dat")));
            if (!success) {
                success = Minecraft.levelIo.loadLegacy(Minecraft.level, new FileInputStream(new File("level.dat")));
            }
        }
        catch (Exception e3) {
            success = false;
        }
        if (!success) {
            this.levelGen.generateLevel(Minecraft.level, this.user.name, 256, 256, 64);
        }
        this.levelRenderer = new LevelRenderer(Minecraft.level, this.textures);
        this.player = new Player(Minecraft.level);
        this.particleEngine = new ParticleEngine(Minecraft.level, this.textures);
        for (int i = 0; i < 10; ++i) {
            final Zombie zombie = new Zombie(Minecraft.level, this.textures, 128.0f, 0.0f, 128.0f);
            zombie.resetPos();
            this.entities.add(zombie);
        }
        if (this.appletMode) {
            try {
                this.emptyCursor = new Cursor(16, 16, 0, 0, 1, imgData, (IntBuffer)null);
            }
            catch (LWJGLException e2) {
                e2.printStackTrace();
            }
        }
        this.checkGlError("Post startup");
    }
    
    public void setScreen(final Screen screen) {
        this.screen = screen;
        if (screen != null) {
            final int screenWidth = this.width * 240 / this.height;
            final int screenHeight = this.height * 240 / this.height;
            screen.init(this, screenWidth, screenHeight);
        }
    }
    
    private void checkGlError(final String string) {
        final int errorCode = GL11.glGetError();
        if (errorCode != 0) {
            final String errorString = GLU.gluErrorString(errorCode);
            System.out.println("########## GL ERROR ##########");
            System.out.println("@ " + string);
            System.out.println(String.valueOf(errorCode) + ": " + errorString);
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
    
    public void destroy() {
        this.attemptSaveLevel();
        Mouse.destroy();
        Keyboard.destroy();
        Display.destroy();
    }

    public void stop() {
        this.running = false;
    }
    
    public void grabMouse() {
        if (this.mouseGrabbed) {
            return;
        }
        this.mouseGrabbed = true;
        if (this.appletMode) {
            try {
                Mouse.setNativeCursor(this.emptyCursor);
                Mouse.setCursorPosition(this.width / 2, this.height / 2);
            }
            catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
        else {
            Mouse.setGrabbed(true);
        }
        this.setScreen(null);
    }
    
    public void releaseMouse() {
        if (!this.mouseGrabbed) {
            return;
        }
        this.player.releaseAllKeys();
        this.mouseGrabbed = false;
        if (this.appletMode) {
            try {
                Mouse.setNativeCursor((Cursor)null);
            }
            catch (LWJGLException e) {
                e.printStackTrace();
            }
        }
        else {
            Mouse.setGrabbed(false);
        }
        this.setScreen(new PauseScreen());
    }
    
    private void handleMouseClick() {
        if (this.editMode == 0) {
            if (this.hitResult != null) {
                final Tile oldTile = Tile.tiles[Minecraft.level.getTile(this.hitResult.x, this.hitResult.y, this.hitResult.z)];
                final boolean changed = Minecraft.level.setTile(this.hitResult.x, this.hitResult.y, this.hitResult.z, 0);
                if (oldTile != null && changed) {
                    oldTile.destroy(Minecraft.level, this.hitResult.x, this.hitResult.y, this.hitResult.z, this.particleEngine);
                }
            }
        }
        else if (this.hitResult != null) {
            int x = this.hitResult.x;
            int y = this.hitResult.y;
            int z = this.hitResult.z;
            if (this.hitResult.f == 0) {
                --y;
            }
            if (this.hitResult.f == 1) {
                ++y;
            }
            if (this.hitResult.f == 2) {
                --z;
            }
            if (this.hitResult.f == 3) {
                ++z;
            }
            if (this.hitResult.f == 4) {
                --x;
            }
            if (this.hitResult.f == 5) {
                ++x;
            }
            final AABB aabb = Tile.tiles[this.paintTexture].getAABB(x, y, z);
            if (aabb == null || this.isFree(aabb)) {
                Minecraft.level.setTile(x, y, z, this.paintTexture);
            }
        }
    }
    
    public void tick() {
        if (this.screen == null) {
            while (Mouse.next()) {
                if (!this.mouseGrabbed && Mouse.getEventButtonState()) {
                    this.grabMouse();
                }
                else {
                    if (Mouse.getEventButton() == 0 && Mouse.getEventButtonState()) {
                        this.handleMouseClick();
                    }
                    if (Mouse.getEventButton() != 1 || !Mouse.getEventButtonState()) {
                        continue;
                    }
                    this.editMode = (this.editMode + 1) % 2;
                }
            }
            while (Keyboard.next()) {
                this.player.setKey(Keyboard.getEventKey(), Keyboard.getEventKeyState());
                if (Keyboard.getEventKeyState()) {
                    if (Keyboard.getEventKey() == 1) {
                        this.releaseMouse();
                    }															//refer to tile.java to find tile ID.
                    if (Keyboard.getEventKey() == 28) {
                        this.attemptSaveLevel();
                    }
                    if (Keyboard.getEventKey() == 19) {
                        this.player.resetPos();
                    }
                    if (Keyboard.getEventKey() == 2) {
                        this.paintTexture = 1; //rock (stone)
                    }
                    if (Keyboard.getEventKey() == 3) {
                        this.paintTexture = 3; //dirt
                    }
                    if (Keyboard.getEventKey() == 4) {
                        this.paintTexture = 4; //stoneBricks
                    }
                    if (Keyboard.getEventKey() == 5) {
                        this.paintTexture = 5; //wood
                    }
                    if (Keyboard.getEventKey() == 7) {
                        this.paintTexture = 6; //non-functional bush
                    }
                    if (Keyboard.getEventKey() == 8) {
                        this.paintTexture = 12;  //white cloth
                    }
                  
                    if (Keyboard.getEventKey() == Keyboard.KEY_PERIOD) {
                    	if (this.paintTexture != 16)
                            this.paintTexture += 1;
                    }
                    
                    if (Keyboard.getEventKey() == Keyboard.KEY_COMMA) {
                    	if (this.paintTexture != 1)
                        	this.paintTexture -= 1;
                    }
                    if (Keyboard.getEventKey() == 21) {
                        this.yMouseAxis *= -1;
                    }
                    if (Keyboard.getEventKey() == 34) {
                        this.entities.add(new Zombie(Minecraft.level, this.textures, this.player.x, this.player.y, this.player.z));
                    }
                    if (Keyboard.getEventKey() != 33) {
                        continue;
                    }
                    this.levelRenderer.toggleDrawDistance();
                }
            }
        }

        if (this.screen != null) {
            this.screen.updateEvents();
            if (this.screen != null) {
                this.screen.tick();
            }
        }
        Minecraft.level.tick();
        this.particleEngine.tick();
        for (int i = 0; i < this.entities.size(); ++i) {
            this.entities.get(i).tick();
            if (this.entities.get(i).removed) {
                this.entities.remove(i--);
            }
        }
        this.player.tick();
    }
    
    private boolean isFree(final AABB aabb) {
        if (this.player.bb.intersects(aabb)) {
            return false;
        }
        for (int i = 0; i < this.entities.size(); ++i) {
            if (this.entities.get(i).bb.intersects(aabb)) {
                return false;
            }
        }
        return true;
    }
    
    private void moveCameraToPlayer(final float a) {
        GL11.glTranslatef(0.0f, 0.0f, -0.3f);
        GL11.glRotatef(this.player.xRot, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(this.player.yRot, 0.0f, 1.0f, 0.0f);
        final float x = this.player.xo + (this.player.x - this.player.xo) * a;
        final float y = this.player.yo + (this.player.y - this.player.yo) * a;
        final float z = this.player.zo + (this.player.z - this.player.zo) * a;
        GL11.glTranslatef(-x, -y, -z);
    }
    
    private void setupCamera(final float a) {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GLU.gluPerspective(70.0f, this.width / (float)this.height, 0.05f, 1024.0f);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.moveCameraToPlayer(a);
    }
    
    private void setupPickCamera(final float a, final int x, final int y) {
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        this.viewportBuffer.clear();
        GL11.glGetInteger(2978, this.viewportBuffer);
        this.viewportBuffer.flip();
        this.viewportBuffer.limit(16);
        GLU.gluPickMatrix((float)x, (float)y, 5.0f, 5.0f, this.viewportBuffer);
        GLU.gluPerspective(70.0f, this.width / (float)this.height, 0.05f, 1024.0f);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        this.moveCameraToPlayer(a);
    }
    
    private void pick(final float a) {
        this.selectBuffer.clear();
        GL11.glSelectBuffer(this.selectBuffer);
        GL11.glRenderMode(7170);
        this.setupPickCamera(a, this.width / 2, this.height / 2);
        this.levelRenderer.pick(this.player, Frustum.getFrustum());
        final int hits = GL11.glRenderMode(7168);
        this.selectBuffer.flip();
        this.selectBuffer.limit(this.selectBuffer.capacity());
        final int[] names = new int[10];
        HitResult bestResult = null;
        for (int i = 0; i < hits; ++i) {
            final int nameCount = this.selectBuffer.get();
            this.selectBuffer.get();
            this.selectBuffer.get();
            for (int j = 0; j < nameCount; ++j) {
                names[j] = this.selectBuffer.get();
            }
            this.hitResult = new HitResult(names[0], names[1], names[2], names[3], names[4]);
            if (bestResult == null || this.hitResult.isCloserThan(this.player, bestResult, this.editMode)) {
                bestResult = this.hitResult;
            }
        }
        this.hitResult = bestResult;
    }
    
    public void render(final float a) {
        if (!Display.isActive()) {
            this.releaseMouse();
        }
        GL11.glViewport(0, 0, this.width, this.height);
        if (this.mouseGrabbed) {
            float xo = 0.0f;
            float yo = 0.0f;
            xo = (float)Mouse.getDX();
            yo = (float)Mouse.getDY();
            if (this.appletMode) {
                Display.processMessages();
                Mouse.poll();
                xo = (float)(Mouse.getX() - this.width / 2);
                yo = (float)(Mouse.getY() - this.height / 2);
                Mouse.setCursorPosition(this.width / 2, this.height / 2);
            }
            this.player.turn(xo, yo * this.yMouseAxis);
        }
        this.checkGlError("Set viewport");
        this.pick(a);
        this.checkGlError("Picked");
        GL11.glClear(16640);
        this.setupCamera(a);
        this.checkGlError("Set up camera");
        GL11.glEnable(2884);
        final Frustum frustum = Frustum.getFrustum();
        this.levelRenderer.cull(frustum);
        this.levelRenderer.updateDirtyChunks(this.player);
        this.checkGlError("Update chunks");
        this.setupFog(0);
        GL11.glEnable(2912);
        this.levelRenderer.render(this.player, 0);
        this.checkGlError("Rendered level");
        for (int i = 0; i < this.entities.size(); ++i) {
            final Entity entity = this.entities.get(i);
            if (entity.isLit() && frustum.isVisible(entity.bb)) {
                this.entities.get(i).render(a);
            }
        }
        this.checkGlError("Rendered entities");
        this.particleEngine.render(this.player, a, 0);
        this.checkGlError("Rendered particles");
        this.setupFog(1);
        this.levelRenderer.render(this.player, 1);
        for (int i = 0; i < this.entities.size(); ++i) {
            final Entity zombie = this.entities.get(i);
            if (!zombie.isLit() && frustum.isVisible(zombie.bb)) {
                this.entities.get(i).render(a);
            }
        }
        this.particleEngine.render(this.player, a, 1);
        this.levelRenderer.renderSurroundingGround();
        if (this.hitResult != null) {
            GL11.glDisable(2896);
            GL11.glDisable(3008);
            this.levelRenderer.renderHit(this.player, this.hitResult, this.editMode, this.paintTexture);
            this.levelRenderer.renderHitOutline(this.player, this.hitResult, this.editMode, this.paintTexture);
            GL11.glEnable(3008);
            GL11.glEnable(2896);
        }
        GL11.glBlendFunc(770, 771);
        this.setupFog(0);
        this.levelRenderer.renderSurroundingWater();
        GL11.glEnable(3042);
        GL11.glColorMask(false, false, false, false);
        this.levelRenderer.render(this.player, 2);
        GL11.glColorMask(true, true, true, true);
        this.levelRenderer.render(this.player, 2);
        GL11.glDisable(3042);
        GL11.glDisable(2896);
        GL11.glDisable(3553);
        GL11.glDisable(2912);
        if (this.hitResult != null) {
            GL11.glDepthFunc(513);
            GL11.glDisable(3008);
            this.levelRenderer.renderHit(this.player, this.hitResult, this.editMode, this.paintTexture);
            this.levelRenderer.renderHitOutline(this.player, this.hitResult, this.editMode, this.paintTexture);
            GL11.glEnable(3008);
            GL11.glDepthFunc(515);
        }
        this.drawGui(a);
        this.checkGlError("Rendered gui");
        Display.update();
    }
    
    public void run() {
        this.running = true;
        try {
            this.init();
        }
        catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(null, e.toString(), "Failed to start MinecraftGRPF", 0);
            return;
        }
        long lastTime = System.currentTimeMillis();
        int frames = 0;
        try {
            while (this.running) {
                if (this.pause) {
                    Thread.sleep(100L);
                }
                else {
                    if (this.parent == null && Display.isCloseRequested()) {
                        this.stop();
                    }
                    this.timer.advanceTime();
                    for (int i = 0; i < this.timer.ticks; ++i) {
                        this.tick();
                    }
                    this.checkGlError("Pre render");
                    this.render(this.timer.a);
                    this.checkGlError("Post render");
                    ++frames;
                    while (System.currentTimeMillis() >= lastTime + 1000L) {
                        this.fpsString = String.valueOf(frames) + " fps, " + Chunk.updates + " chunk updates";
                        Chunk.updates = 0;
                        lastTime += 1000L;
                        frames = 0;
                    }
                }
            }
        }
        catch (Exception e2) {
            e2.printStackTrace();
            return;
        }
        finally {
            this.destroy();
        }
        this.destroy();
    }
    
    
    private void drawGui(final float a) {
        final int screenWidth = this.width * 240 / this.height;
        final int screenHeight = this.height * 240 / this.height;
        final int xMouse = Mouse.getX() * screenWidth / this.width;
        final int yMouse = screenHeight - Mouse.getY() * screenHeight / this.height - 1;
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, (double)screenWidth, (double)screenHeight, 0.0, 100.0, 300.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -200.0f);
        this.checkGlError("GUI: Init");
        GL11.glPushMatrix();
        GL11.glTranslatef((float)(screenWidth - 16), 16.0f, -50.0f);
        final Tesselator t = Tesselator.instance;
        GL11.glScalef(16.0f, 16.0f, 16.0f);
        GL11.glRotatef(-30.0f, 1.0f, 0.0f, 0.0f);
        GL11.glRotatef(45.0f, 0.0f, 1.0f, 0.0f);
        GL11.glTranslatef(-1.5f, 0.5f, 0.5f);
        GL11.glScalef(-1.0f, -1.0f, -1.0f);
        final int id = this.textures.loadTexture("/terrain.png", 9728);
        GL11.glBindTexture(3553, id);
        GL11.glEnable(3553);
        t.begin();
        Tile.tiles[this.paintTexture].render(t, Minecraft.level, 0, -2, 0, 0);
        t.end();
        GL11.glDisable(3553);
        GL11.glPopMatrix();
        this.checkGlError("GUI: Draw selected");
        this.font.drawShadow("MinecraftGRPF 0.1.0b", 2, 2, 16777215);
        this.font.drawShadow(this.fpsString, 2, 12, 16777215);
        this.font.drawShadow("Test version", 2, 22, 16777215);
        this.font.drawShadow("this.cordString", 2, 42, 16777215);
        this.checkGlError("GUI: Draw text");
        final int wc = screenWidth / 2;
        final int hc = screenHeight / 2;
        GL11.glColor4f(1.0f, 1.0f, 1.0f, 1.0f);
        t.begin();
        t.vertex((float)(wc + 1), (float)(hc - 4), 0.0f);
        t.vertex((float)(wc - 0), (float)(hc - 4), 0.0f);
        t.vertex((float)(wc - 0), (float)(hc + 5), 0.0f);
        t.vertex((float)(wc + 1), (float)(hc + 5), 0.0f);
        t.vertex((float)(wc + 5), (float)(hc - 0), 0.0f);
        t.vertex((float)(wc - 4), (float)(hc - 0), 0.0f);
        t.vertex((float)(wc - 4), (float)(hc + 1), 0.0f);
        t.vertex((float)(wc + 5), (float)(hc + 1), 0.0f);
        t.end();
        this.checkGlError("GUI: Draw crosshair");
        if (this.screen != null) {
            this.screen.render(xMouse, yMouse);
        }
    }
    
    private void setupFog(final int i) {
        final Tile currentTile = Tile.tiles[Minecraft.level.getTile((int)this.player.x, (int)(this.player.y + 0.12f), (int)this.player.z)];
        if (currentTile != null && currentTile.getLiquidType() == 1) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.1f);
            GL11.glFog(2918, this.getBuffer(0.02f, 0.02f, 0.2f, 1.0f));
            GL11.glLightModel(2899, this.getBuffer(0.3f, 0.3f, 0.7f, 1.0f));
        }
        else if (currentTile != null && currentTile.getLiquidType() == 2) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 2.0f);
            GL11.glFog(2918, this.getBuffer(0.6f, 0.1f, 0.0f, 1.0f));
            GL11.glLightModel(2899, this.getBuffer(0.4f, 0.3f, 0.3f, 1.0f));
        }
        else if (i == 0) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.001f);
            GL11.glFog(2918, this.fogColor0);
            GL11.glLightModel(2899, this.getBuffer(1.0f, 1.0f, 1.0f, 1.0f));
        }
        else if (i == 1) {
            GL11.glFogi(2917, 2048);
            GL11.glFogf(2914, 0.01f);
            GL11.glFog(2918, this.fogColor1);
            final float br = 0.6f;
            GL11.glLightModel(2899, this.getBuffer(br, br, br, 1.0f));
        }
        GL11.glEnable(2903);
        GL11.glColorMaterial(1028, 4608);
        GL11.glEnable(2896);
    }
    
    private FloatBuffer getBuffer(final float a, final float b, final float c, final float d) {
        this.lb.clear();
        this.lb.put(a).put(b).put(c).put(d);
        this.lb.flip();
        return this.lb;
    }
    
    public static void checkError() {
        final int e = GL11.glGetError();
        if (e != 0) {
            throw new IllegalStateException(GLU.gluErrorString(e));
        }
    }
    
    public void beginLevelLoading(final String title) {
        this.title = title;
        final int screenWidth = this.width * 240 / this.height;
        final int screenHeight = this.height * 240 / this.height;
        GL11.glClear(256);
        GL11.glMatrixMode(5889);
        GL11.glLoadIdentity();
        GL11.glOrtho(0.0, (double)screenWidth, (double)screenHeight, 0.0, 100.0, 300.0);
        GL11.glMatrixMode(5888);
        GL11.glLoadIdentity();
        GL11.glTranslatef(0.0f, 0.0f, -200.0f);
    }
    
    public void levelLoadUpdate(final String status) {
        final int screenWidth = this.width * 240 / this.height;
        final int screenHeight = this.height * 240 / this.height;
        GL11.glClear(16640);
        final Tesselator t = Tesselator.instance;
        GL11.glEnable(3553);
        final int id = this.textures.loadTexture("/wood.png", 9728);
        GL11.glBindTexture(3553, id);
        t.begin();
        t.color(8421504);
        final float s = 32.0f;
        t.vertexUV(0.0f, (float)screenHeight, 0.0f, 0.0f, screenHeight / s);
        t.vertexUV((float)screenWidth, (float)screenHeight, 0.0f, screenWidth / s, screenHeight / s);
        t.vertexUV((float)screenWidth, 0.0f, 0.0f, screenWidth / s, 0.0f);
        t.vertexUV(0.0f, 0.0f, 0.0f, 0.0f, 0.0f);
        t.end();
        GL11.glEnable(3553);
        this.font.drawShadow(this.title, (screenWidth - this.font.width(this.title)) / 2, screenHeight / 2 - 4 - 8, 16777215);
        this.font.drawShadow(status, (screenWidth - this.font.width(status)) / 2, screenHeight / 2 - 4 + 4, 16777215);
        Display.update();
        try {
            Thread.sleep(200L);
        }
        catch (Exception ex) {}
    }
    
    
    //Level generation used by Pause Screen.
    public void generateNewLevel() {
        this.levelGen.generateLevel(Minecraft.level, this.user.name, 256, 256, 64);
        this.player.resetPos();
        for (int i = 0; i < this.entities.size(); ++i) {
            this.entities.remove(i--);
        }
    }
    
    public static void main(final String[] args) throws LWJGLException {
        boolean fullScreen = false;
        for (int i = 0; i < args.length; ++i) {
            if (args[i].equalsIgnoreCase("-fullscreen")) {
                fullScreen = true;
            }
        }
        final Minecraft minecraft = new Minecraft(null, 854, 480, fullScreen);
        new Thread(minecraft).start();
    }
}
