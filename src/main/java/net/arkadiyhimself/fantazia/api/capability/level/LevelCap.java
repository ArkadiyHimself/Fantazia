package net.arkadiyhimself.fantazia.api.capability.level;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.GlobalLevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.data.spawn.EffectSpawnHolder;
import net.arkadiyhimself.fantazia.data.spawn.MobEffectsOnSpawnManager;
import net.arkadiyhimself.fantazia.data.talents.AttributeTalent;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelCap extends GlobalLevelCapability {
    private static final String AURAS_ID = "aura_instances";
    private static final String TALENTS_ID = "talents";
    private static final String MODIFIERS_ID = "modifiers";
    private static final String EFFECT_SPAWN_ID = "effects_on_spawn";
    private final List<AuraInstance<? extends Entity>> auraInstances = Lists.newArrayList();
    private final HashMap<ResourceLocation, AttributeModifier> talentAttributeModifiers = Maps.newHashMap();
    private final HealingSources healingSources;
    private final FTZDamageTypes.DamageSources damageSources;
    private final List<EffectSpawnHolder> effectSpawnHolders = MobEffectsOnSpawnManager.createHolders();
    public LevelCap(Level level) {
        super(level);
        this.healingSources = new HealingSources(level.registryAccess());
        this.damageSources = new FTZDamageTypes.DamageSources(level.registryAccess());
    }
    @Override
    public LevelCapabilityStatusPacket createUpdatePacket() {
        return new SimpleLevelCapabilityStatusPacket(LevelCapGetter.LEVEL_CAP_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        serializeAuras(tag);
        if (savingToDisk) {
            serializeModifiers(tag);
            serializeEffectsOnSpawn(tag);
        }
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        deserializeAuras(nbt);
        if (readingFromDisk) {
            deserializeModifiers(nbt);
            deserializeEffectsOnSpawn(nbt);
        }
    }
    private void serializeAuras(CompoundTag tag) {
        if (auraInstances.isEmpty()) return;

        ListTag instances = new ListTag();
        for (AuraInstance<? extends Entity> auraInstance : auraInstances) instances.add(auraInstance.serialize());

        tag.put(AURAS_ID, instances);
    }
    private void deserializeAuras(CompoundTag tag) {
        auraInstances.clear();

        if (!tag.contains(AURAS_ID)) return;
        ListTag tags = tag.getList(AURAS_ID, Tag.TAG_COMPOUND);

        for (int i = 0; i < tags.size(); i++) auraInstances.add(AuraInstance.deserialize(tags.getCompound(i), level));
    }
    private void serializeModifiers(CompoundTag tag) {
        ListTag talents = new ListTag();
        ListTag modifiers = new ListTag();

        for (Map.Entry<ResourceLocation, AttributeModifier> entry : talentAttributeModifiers.entrySet()) {
            talents.add(StringTag.valueOf(entry.getKey().toString()));
            modifiers.add(entry.getValue().save());
        }

        tag.put(TALENTS_ID, talents);
        tag.put(MODIFIERS_ID, modifiers);
    }
    private void deserializeModifiers(CompoundTag tag) {
        talentAttributeModifiers.clear();
        if (!tag.contains(TALENTS_ID) || !tag.contains(MODIFIERS_ID)) return;

        ListTag talents = tag.getList(TALENTS_ID, Tag.TAG_STRING);
        ListTag modifiers = tag.getList(MODIFIERS_ID, Tag.TAG_COMPOUND);

        if (talents.size() != modifiers.size()) return;

        for (int i = 0; i < talents.size(); i++) talentAttributeModifiers.put(new ResourceLocation(talents.getString(i)), AttributeModifier.load(modifiers.getCompound(i)));
    }
    private void serializeEffectsOnSpawn(CompoundTag tag) {
        ListTag holders = new ListTag();
        for (EffectSpawnHolder holder : effectSpawnHolders) holders.add(holder.serialize());
        tag.put(EFFECT_SPAWN_ID, holders);
    }
    private void deserializeEffectsOnSpawn(CompoundTag tag) {
        effectSpawnHolders.clear();
        if (!tag.contains(EFFECT_SPAWN_ID)) return;

        ListTag effectHolders = tag.getList(EFFECT_SPAWN_ID, Tag.TAG_COMPOUND);
        for (int i = 0; i < effectHolders.size(); i++) effectSpawnHolders.add(EffectSpawnHolder.deserialize(effectHolders.getCompound(i)));
        if (effectSpawnHolders.isEmpty()) effectSpawnHolders.addAll(MobEffectsOnSpawnManager.createHolders());
    }
    public void tick() {
        auraInstances.forEach(AuraInstance::tick);
    }
    public List<AuraInstance<? extends Entity>> getAuraInstances() {
        return auraInstances;
    }
    public void addAuraInstance(AuraInstance<? extends Entity> instance) {
        if (!auraInstances.contains(instance)) auraInstances.add(instance);
        updateTracking();
    }
    public void removeAuraInstance(AuraInstance<?> instance) {
        auraInstances.remove(instance);
        updateTracking();
    }
    public HealingSources healingSources() {
        return healingSources;
    }
    public FTZDamageTypes.DamageSources damageSources() {
        return damageSources;
    }
    public AttributeModifier getOrCreateModifier(AttributeTalent talent) {
        ResourceLocation id = talent.getID();
        talentAttributeModifiers.computeIfAbsent(id, res -> new AttributeModifier(talent.getName(), talent.getAmount(), talent.getOperation()));
        return talentAttributeModifiers.get(id);
    }
    public void tryApplyEffects(LivingEntity spawned) {
        for (EffectSpawnHolder holder : effectSpawnHolders) if (holder.isAffected(spawned)) holder.tryAddEffects(spawned);
    }
}
