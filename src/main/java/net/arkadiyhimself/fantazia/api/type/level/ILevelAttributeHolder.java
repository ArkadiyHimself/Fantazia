package net.arkadiyhimself.fantazia.api.type.level;

import net.arkadiyhimself.fantazia.api.type.entity.IBasicHolder;
import net.minecraft.world.level.Level;

public interface ILevelAttributeHolder extends IBasicHolder {
    Level getLevel();
}
