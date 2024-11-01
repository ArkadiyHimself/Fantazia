package net.arkadiyhimself.fantazia.api.attachment.level.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.data.spawn.EffectSpawnHolder;
import net.arkadiyhimself.fantazia.data.spawn.EffectsOnSpawnManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class EffectsOnSpawnHolder extends LevelAttributeHolder {

    private final List<EffectSpawnHolder> effectSpawnHolders = EffectsOnSpawnManager.createHolders();

    public EffectsOnSpawnHolder(Level level) {
        super(level, Fantazia.res("effects_on_spawn"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag holders = new ListTag();
        for (EffectSpawnHolder holder : effectSpawnHolders) holders.add(holder.serialize());
        tag.put("effect_holders", holders);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        effectSpawnHolders.clear();
        if (!compoundTag.contains("effect_holders")) return;

        ListTag effectHolders = compoundTag.getList("effect_holders", Tag.TAG_COMPOUND);
        for (int i = 0; i < effectHolders.size(); i++) effectSpawnHolders.add(EffectSpawnHolder.deserialize(effectHolders.getCompound(i)));
        if (effectSpawnHolders.isEmpty()) effectSpawnHolders.addAll(EffectsOnSpawnManager.createHolders());
    }

    public void tryApplyEffects(LivingEntity spawned) {
        for (EffectSpawnHolder holder : effectSpawnHolders) if (holder.isAffected(spawned)) holder.tryAddEffects(spawned);
    }
}
