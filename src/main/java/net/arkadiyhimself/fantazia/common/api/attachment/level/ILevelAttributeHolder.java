package net.arkadiyhimself.fantazia.common.api.attachment.level;

import net.arkadiyhimself.fantazia.common.api.attachment.IBasicHolder;
import net.minecraft.world.level.Level;

public interface ILevelAttributeHolder extends IBasicHolder {
    Level getLevel();
}
