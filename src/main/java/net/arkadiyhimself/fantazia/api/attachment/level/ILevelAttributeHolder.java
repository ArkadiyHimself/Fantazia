package net.arkadiyhimself.fantazia.api.attachment.level;

import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.minecraft.world.level.Level;

public interface ILevelAttributeHolder extends IBasicHolder {
    Level getLevel();
}
