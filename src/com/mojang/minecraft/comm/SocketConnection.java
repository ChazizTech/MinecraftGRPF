// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.comm;

import java.io.IOException;
import java.net.UnknownHostException;
import java.net.SocketAddress;
import java.net.InetSocketAddress;
import java.io.BufferedOutputStream;
import java.io.BufferedInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;

public class SocketConnection
{
    public static final int BUFFER_SIZE = 131068;
    private boolean connected;
    private SocketChannel socketChannel;
    public ByteBuffer readBuffer;
    public ByteBuffer writeBuffer;
    protected long lastRead;
    private ConnectionListener connectionListener;
    private int bytesRead;
    private int totalBytesWritten;
    private Socket socket;
    private BufferedInputStream in;
    private BufferedOutputStream out;
    
    public SocketConnection(final String ip, final int port) throws UnknownHostException, IOException {
        this.readBuffer = ByteBuffer.allocate(131068);
        this.writeBuffer = ByteBuffer.allocate(131068);
        (this.socketChannel = SocketChannel.open()).connect(new InetSocketAddress(ip, port));
        this.socketChannel.configureBlocking(false);
        this.lastRead = System.currentTimeMillis();
        this.connected = true;
        this.readBuffer.clear();
        this.writeBuffer.clear();
    }
    
    public String getIp() {
        return this.socket.getInetAddress().toString();
    }
    
    public SocketConnection(final SocketChannel socketChannel) throws IOException {
        this.readBuffer = ByteBuffer.allocate(131068);
        this.writeBuffer = ByteBuffer.allocate(131068);
        (this.socketChannel = socketChannel).configureBlocking(false);
        this.lastRead = System.currentTimeMillis();
        this.socket = socketChannel.socket();
        this.connected = true;
        this.readBuffer.clear();
        this.writeBuffer.clear();
    }
    
    public ByteBuffer getBuffer() {
        return this.writeBuffer;
    }
    
    public void setConnectionListener(final ConnectionListener connectionListener) {
        this.connectionListener = connectionListener;
    }
    
    public boolean isConnected() {
        return this.connected;
    }
    
    public void disconnect() {
        this.connected = false;
        try {
            if (this.in != null) {
                this.in.close();
            }
            this.in = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.out != null) {
                this.out.close();
            }
            this.out = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (this.socket != null) {
                this.socket.close();
            }
            this.socket = null;
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public void tick() throws IOException {
        this.writeBuffer.flip();
        this.socketChannel.write(this.writeBuffer);
        this.writeBuffer.compact();
        this.readBuffer.compact();
        this.socketChannel.read(this.readBuffer);
        this.readBuffer.flip();
        if (this.readBuffer.remaining() > 0) {
            this.connectionListener.command(this.readBuffer.get(0), this.readBuffer.remaining(), this.readBuffer);
        }
    }
    
    public int getSentBytes() {
        return this.totalBytesWritten;
    }
    
    public int getReadBytes() {
        return this.bytesRead;
    }
    
    public void clearSentBytes() {
        this.totalBytesWritten = 0;
    }
    
    public void clearReadBytes() {
        this.bytesRead = 0;
    }
}
