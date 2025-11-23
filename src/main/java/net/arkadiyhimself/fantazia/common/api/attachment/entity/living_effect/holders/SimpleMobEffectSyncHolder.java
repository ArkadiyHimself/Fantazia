package net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.ILivingEffectHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.networking.IPacket;
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
import org.jetbrains.annotations.UnknownNullability;

import java.util.Map;

public class SimpleMobEffectSyncHolder implements ILivingEffectHolder {

    private final Map<MobEffect, Boolean> effectMap = Maps.newHashMap();
    private final LivingEntity livingEntity;

    private boolean toSync = true;

    public SimpleMobEffectSyncHolder(LivingEntity livingEntity) {
        this.livingEntity = livingEntity;

        addSyncing(FTZMobEffects.ABSOLUTE_BARRIER.value());
        addSyncing(FTZMobEffects.CURSED_MARK.value());
        addSyncing(FTZMobEffects.DEAFENED.value());
        addSyncing(FTZMobEffects.DEFLECT.value());
        addSyncing(FTZMobEffects.DISARM.value());
        addSyncing(FTZMobEffects.DOOMED.value());
        addSyncing(FTZMobEffects.FURY.value());
        addSyncing(FTZMobEffects.WITHERS_BARRIER.value());
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();

        for (Map.Entry<MobEffect, Boolean> entry : effectMap.entrySet()) {
            ResourceLocation location = BuiltInRegistries.MOB_EFFECT.getKey(entry.getKey());
            if (location == null) continue;
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("effect", location.toString());
            entryTag.putBoolean("present", entry.getValue());
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
            boolean present = entry.getBoolean("present");
            effectMap.remove(mobEffect);
            effectMap.put(mobEffect, present);
        }
    }

    @Override
    public @NotNull LivingEntity getEntity() {
        return livingEntity;
    }

    @Override
    public void added(MobEffectInstance instance) {
        MobEffect mobEffect = instance.getEffect().value();
        setEffect(mobEffect,true);
        if (!getEntity().level().isClientSide()) IPacket.effectSync(getEntity(), mobEffect, true);
    }

    @Override
    public void ended(MobEffect effect) {
        setEffect(effect,false);
        if (!getEntity().level().isClientSide()) IPacket.effectSync(getEntity(), effect, false);
    }

    @Override
    public ResourceLocation id() {
        return Fantazia.location("simple_mob_effect_sync");
    }

    @Override
    public CompoundTag serializeInitial() {
        CompoundTag tag = new CompoundTag();

        ListTag listTag = new ListTag();

        for (Map.Entry<MobEffect, Boolean> entry : effectMap.entrySet()) {
            ResourceLocation location = BuiltInRegistries.MOB_EFFECT.getKey(entry.getKey());
            if (location == null) continue;
            CompoundTag entryTag = new CompoundTag();
            entryTag.putString("effect", location.toString());
            entryTag.putBoolean("present", entry.getValue());
            listTag.add(entryTag);
        }

        tag.put("entries", listTag);

        return tag;
    }

    @Override
    public void deserializeInitial(CompoundTag tag) {
        ListTag entries = tag.getList("entries", Tag.TAG_COMPOUND);

        for (int i = 0; i < entries.size(); i++) {
            CompoundTag entry = entries.getCompound(i);
            MobEffect mobEffect = BuiltInRegistries.MOB_EFFECT.get(ResourceLocation.parse(entry.getString("effect")));
            if (mobEffect == null) continue;
            boolean present = entry.getBoolean("present");
            effectMap.remove(mobEffect);
            effectMap.put(mobEffect, present);
        }
    }

    @Override
    public void serverTick() {
        if (toSync) {
            IPacket.simpleMobEffectSyncing(serializeInitial(), getEntity());
            toSync = false;
        }
    }

    public void addSyncing(MobEffect mobEffect) {
        if (!effectMap.containsKey(mobEffect)) effectMap.put(mobEffect, false);
    }

    public void setEffect(MobEffect mobEffect, boolean present) {
        if (effectMap.containsKey(mobEffect)) effectMap.replace(mobEffect, present);
    }

    public boolean hasEffect(MobEffect mobEffect) {
        return effectMap.getOrDefault(mobEffect,false);
    }
}
