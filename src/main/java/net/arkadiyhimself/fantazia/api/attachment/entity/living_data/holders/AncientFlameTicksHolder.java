package net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class AncientFlameTicksHolder extends LivingDataHolder {
    private int flameTicks = 0;
    public AncientFlameTicksHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("ancient_flame_ticks"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("flameTicks", flameTicks);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        flameTicks = compoundTag.getInt("flameTicks");
    }

    @Override
    public CompoundTag syncSerialize() {
        return serializeNBT(getEntity().registryAccess());
    }

    @Override
    public void syncDeserialize(CompoundTag tag) {
        deserializeNBT(getEntity().registryAccess(), tag);
    }

    @Override
    public void tick() {
        if (flameTicks > 0) flameTicks--;
        DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(getEntity().level());
        if (sources != null && isBurning()) getEntity().hurt(sources.ancientBurning(), 1.5f);
    }

    public void setFlameTicks(int value) {
        if (flameTicks < value) flameTicks = value;
    }

    public boolean isBurning() {
        return flameTicks > 0;
    }
}
