package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;


public interface IDimensionChangeListener {

    void onChangeDimension(ResourceKey<Level> form, ResourceKey<Level> to);
}
