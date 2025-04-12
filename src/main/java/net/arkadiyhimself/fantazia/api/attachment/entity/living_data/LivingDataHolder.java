package net.arkadiyhimself.fantazia.api.attachment.entity.living_data;

import net.arkadiyhimself.fantazia.api.attachment.IBasicHolder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;

public abstract class LivingDataHolder implements IBasicHolder {
    private final @NotNull LivingEntity livingEntity;
    private final @NotNull ResourceLocation id;
    public LivingDataHolder(@NotNull LivingEntity livingEntity, @NotNull ResourceLocation id) {
        this.livingEntity = livingEntity;
        this.id = id;
    }
    @Override
    public final @NotNull ResourceLocation id() {
        return this.id;
    }
    public @NotNull LivingEntity getEntity() {
        return livingEntity;
    }

    @Override
    public CompoundTag syncSerialize() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }
}
