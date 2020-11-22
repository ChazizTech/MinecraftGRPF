// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.comm;

public interface ServerListener
{
    void clientConnected(final SocketConnection p0);
    
    void clientException(final SocketConnection p0, final Exception p1);
}
