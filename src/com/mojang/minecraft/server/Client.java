// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.server;

import java.nio.ByteBuffer;
import com.mojang.minecraft.comm.SocketConnection;
import com.mojang.minecraft.comm.ConnectionListener;

public class Client implements ConnectionListener
{
    public final SocketConnection serverConnection;
    private final MinecraftServer server;
    
    public Client(final MinecraftServer server, final SocketConnection serverConnection) {
        this.server = server;
        (this.serverConnection = serverConnection).setConnectionListener(this);
        System.out.println("Connection");
    }
    
    public void command(final byte cmd, final int remaining, final ByteBuffer in) {
    }
    
    public void handleException(final Exception e) {
        this.disconnect();
    }
    
    public void disconnect() {
        this.server.disconnect(this);
        System.out.println("Disconnection");
    }
}
