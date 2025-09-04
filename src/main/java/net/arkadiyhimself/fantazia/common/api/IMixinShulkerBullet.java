package net.arkadiyhimself.fantazia.common.api;

import net.minecraft.world.entity.Entity;

public interface IMixinShulkerBullet {

    void setTarget(Entity entity);
}
