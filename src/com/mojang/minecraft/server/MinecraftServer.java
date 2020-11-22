// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.server;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import com.mojang.minecraft.comm.SocketConnection;
import java.util.Map;
import com.mojang.minecraft.comm.SocketServer;
import com.mojang.minecraft.comm.ServerListener;

public class MinecraftServer implements Runnable, ServerListener
{
    private SocketServer socketServer;
    private Map<SocketConnection, Client> clientMap;
    private List<Client> clients;
    
    public MinecraftServer(final byte[] ips, final int port) throws IOException {
        this.clientMap = new HashMap<SocketConnection, Client>();
        this.clients = new ArrayList<Client>();
        this.socketServer = new SocketServer(ips, port, this);
    }
    
    public void clientConnected(final SocketConnection serverConnection) {
        final Client client = new Client(this, serverConnection);
        this.clientMap.put(serverConnection, client);
        this.clients.add(client);
    }
    
    public void disconnect(final Client client) {
        this.clientMap.remove(client.serverConnection);
        this.clients.remove(client);
    }
    
    public void clientException(final SocketConnection serverConnection, final Exception e) {
        final Client client = this.clientMap.get(serverConnection);
        client.handleException(e);
    }
    
    public void run() {
        while (true) {
            this.tick();
            try {
                Thread.sleep(5L);
            }
            catch (InterruptedException ex) {}
        }
    }
    
    private void tick() {
        try {
            this.socketServer.tick();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    public static void main(final String[] args) throws IOException {
        final MinecraftServer server = new MinecraftServer(new byte[] { 127, 0, 0, 1 }, 20801);
        final Thread thread = new Thread(server);
        thread.start();
    }
}
