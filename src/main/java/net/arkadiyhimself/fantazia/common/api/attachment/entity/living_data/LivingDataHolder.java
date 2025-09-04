package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data;

import net.arkadiyhimself.fantazia.common.api.attachment.IBasicHolder;
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
    public CompoundTag serializeInitial() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }
}
