package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.CurrentAndInitialValue;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.ILivingEffectHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class MobEffectDurationSyncHolder implements ILivingEffectHolder, ISyncEveryTick {

    private final Map<MobEffect, CurrentAndInitialValue> effectMap = Maps.newHashMap();
    private final LivingEntity livingEntity;

    public MobEffectDurationSyncHolder(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;

        addSyncing(FTZMobEffects.FROZEN.value());
        addSyncing(FTZMobEffects.FURY.value());
        addSyncing(FTZMobEffects.BARRIER.value());
        addSyncing(FTZMobEffects.LAYERED_BARRIER.value());
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingEntity;
    }

    @Override
    public void added(MobEffectInstance instance) {
        int duration = instance.getDuration();
        CurrentAndInitialValue holder = effectMap.get(instance.getEffect().value());
        if (holder != null) {
            holder.setInitialValue(duration);
            holder.setValue(duration);
        }
    }

    @Override
    public void ended(MobEffect effect) {
        CurrentAndInitialValue holder = effectMap.get(effect);
        if (holder != null) holder.setValue(0);
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.location("mob_effect_duration_sync");
    }

    @Override
    public CompoundTag serializeInitial() {
        return new CompoundTag();
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {}

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();

        for (Map.Entry<MobEffect, CurrentAndInitialValue> entry : effectMap.entrySet()) {
            ResourceLocation location = BuiltInRegistries.MOB_EFFECT.getKey(entry.getKey());
            if (location == null) continue;
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("effect", location.toString());
            entryTag.putInt("duration", entry.getValue().value());
            entryTag.putInt("initialDuration", entry.getValue().initialValue());

            listTag.add(entryTag);
        }

        tag.put("entries", listTag);

        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag tag) {
        ListTag entries = tag.getList("entries", Tag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(entry.getString("effect")));
            if (mobEffect == null) continue;
            int dur = entry.getInt("duration");
            int initDur = entry.getInt("initialDuration");

            CurrentAndInitialValue holder = new CurrentAndInitialValue();
            holder.setValue(dur);
            holder.setInitialValue(initDur);
            effectMap.put(mobEffect, holder);
        }
    }

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();

        for (Map.Entry<MobEffect, CurrentAndInitialValue> entry : effectMap.entrySet()) {
            ResourceLocation location = BuiltInRegistries.MOB_EFFECT.getKey(entry.getKey());
            if (location == null) continue;
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("effect", location.toString());
            entryTag.putInt("duration", entry.getValue().value());
            entryTag.putInt("initialDuration", entry.getValue().initialValue());

            listTag.add(entryTag);
        }

        tag.put("entries", listTag);

        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        ListTag entries = tag.getList("entries", Tag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(entry.getString("effect")));
            if (mobEffect == null) continue;
            int dur = entry.getInt("duration");
            int initDur = entry.getInt("initialDuration");

            CurrentAndInitialValue holder = new CurrentAndInitialValue();
            holder.setValue(dur);
            holder.setInitialValue(initDur);
            effectMap.put(mobEffect, holder);
        }
    }

    @Override
    public void serverTick() {
        for (MobEffectInstance instance : getEntity().getActiveEffects()) {
            CurrentAndInitialValue holder = effectMap.get(instance.getEffect().value());
            if (holder != null) holder.setValue(instance.getDuration());
        }
    }

    public void addSyncing(MobEffect mobEffect) {
        if (!effectMap.containsKey(mobEffect)) effectMap.put(mobEffect, new CurrentAndInitialValue());
    }

    public @Nullable CurrentAndInitialValue getDuration(MobEffect mobEffect) {
        return effectMap.get(mobEffect);
    }
}
