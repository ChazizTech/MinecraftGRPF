// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.comm;

import java.nio.channels.SocketChannel;
import java.io.IOException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.List;
import java.nio.channels.ServerSocketChannel;

public class SocketServer
{
    private ServerSocketChannel ssc;
    private ServerListener serverListener;
    private List<SocketConnection> connections;
    
    public SocketServer(final byte[] ips, final int port, final ServerListener serverListener) throws IOException {
        this.connections = new LinkedList<SocketConnection>();
        this.serverListener = serverListener;
        final InetAddress hostip = InetAddress.getByAddress(ips);
        this.ssc = ServerSocketChannel.open();
        this.ssc.socket().bind(new InetSocketAddress(hostip, port));
        this.ssc.configureBlocking(false);
    }
    
    public void tick() throws IOException {
        SocketChannel socketChannel;
        while ((socketChannel = this.ssc.accept()) != null) {
            try {
                socketChannel.configureBlocking(false);
                final SocketConnection socketConnection = new SocketConnection(socketChannel);
                this.connections.add(socketConnection);
                this.serverListener.clientConnected(socketConnection);
            }
            catch (IOException e) {
                socketChannel.close();
                throw e;
            }
        }
        for (int i = 0; i < this.connections.size(); ++i) {
            final SocketConnection socketConnection2 = this.connections.get(i);
            if (!socketConnection2.isConnected()) {
                socketConnection2.disconnect();
                this.connections.remove(i--);
            }
            else {
                try {
                    socketConnection2.tick();
                }
                catch (Exception e2) {
                    socketConnection2.disconnect();
                    this.serverListener.clientException(socketConnection2, e2);
                }
            }
        }
    }
}
