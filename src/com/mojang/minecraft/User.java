// 
// Decompiled by Procyon v0.5.36
// 

package com.mojang.minecraft;

public class User
{
    public static String name;
    
    public User(final String name) {
        User.name = name;
        System.out.println("Username is " + name);
    }
}
