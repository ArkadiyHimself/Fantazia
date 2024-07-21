package net.arkadiyhimself.fantazia.advanced.cleansing;

import net.minecraft.network.chat.Component;

public enum Cleanse {
    BASIC(net.minecraft.network.chat.Component.translatable("fantazia.cleanse.basic"),0),
    MEDIUM(net.minecraft.network.chat.Component.translatable("fantazia.cleanse.medium"),1),
    POWERFUL(net.minecraft.network.chat.Component.translatable("fantazia.cleanse.powerful"),2),
    ABSOLUTE(net.minecraft.network.chat.Component.translatable("fantazia.cleanse.absolute"),3);
    private final Component name;
    private final int strength;
    Cleanse(Component name, int strength) {
        this.name = name;
        this.strength = strength;
    }
    public Component getName() {
        return name;
    }
    public int getStrength() {
        return strength;
    }
    public boolean isStrongEnough(Cleanse cleanse) {
        return this.strength >= cleanse.strength;
    }
}
