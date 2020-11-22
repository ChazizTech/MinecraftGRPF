// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft.comm;

import java.nio.ByteBuffer;

public interface ConnectionListener
{
    void handleException(final Exception p0);
    
    void command(final byte p0, final int p1, final ByteBuffer p2);
}
