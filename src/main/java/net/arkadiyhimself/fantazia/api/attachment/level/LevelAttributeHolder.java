package net.arkadiyhimself.fantazia.api.attachment.level;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;


public abstract class LevelAttributeHolder implements ILevelAttributeHolder {
    private final Level level;
    private final ResourceLocation id;

    public LevelAttributeHolder(Level level, ResourceLocation id) {
        this.id = id;
        this.level = level;
    }

    @Override
    public Level getLevel() {
        return level;
    }

    @Override
    public ResourceLocation id() {
        return id;
    }

    @Override
    public CompoundTag serializeInitial() {
        return new CompoundTag();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {

    }
}
