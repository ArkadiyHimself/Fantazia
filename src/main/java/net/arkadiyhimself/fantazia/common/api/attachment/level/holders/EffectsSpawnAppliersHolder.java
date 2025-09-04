package net.arkadiyhimself.fantazia.common.api.attachment.level.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.level.LevelAttributeHolder;
import net.arkadiyhimself.fantazia.data.spawn_effect.EffectSpawnApplier;
import net.arkadiyhimself.fantazia.data.spawn_effect.ServerSpawnEffectManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

public class EffectsSpawnAppliersHolder extends LevelAttributeHolder {

    private final List<EffectSpawnApplier> effectSpawnAppliers = ServerSpawnEffectManager.createHolders();

    public EffectsSpawnAppliersHolder(Level level) {
        super(level, Fantazia.location("effects_on_spawn"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        ListTag holders = new ListTag();
        for (EffectSpawnApplier holder : effectSpawnAppliers) holders.add(holder.serialize());
        tag.put("effect_holders", holders);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        effectSpawnAppliers.clear();
        if (!compoundTag.contains("effect_holders")) return;

        ListTag effectHolders = compoundTag.getList("effect_holders", Tag.TAG_COMPOUND);
        for (int i = 0; i < effectHolders.size(); i++) effectSpawnAppliers.add(EffectSpawnApplier.deserialize(effectHolders.getCompound(i)));
        if (effectSpawnAppliers.isEmpty()) effectSpawnAppliers.addAll(ServerSpawnEffectManager.createHolders());
    }

    public void tryApplyEffects(Mob mob) {
        for (EffectSpawnApplier holder : effectSpawnAppliers) if (holder.isAffected(mob)) holder.tryAddEffects(mob);
    }

    public void reset() {
        effectSpawnAppliers.clear();
        effectSpawnAppliers.addAll(ServerSpawnEffectManager.createHolders());
    }
}
