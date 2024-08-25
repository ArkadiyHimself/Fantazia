package net.arkadiyhimself.fantazia.api.capability.level;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.GlobalLevelCapability;
import dev._100media.capabilitysyncer.network.LevelCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleLevelCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.healing.HealingSources;
import net.arkadiyhimself.fantazia.data.talents.AttributeTalent;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.simple.SimpleChannel;
import org.apache.commons.compress.utils.Lists;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LevelCap extends GlobalLevelCapability {
    private final List<AuraInstance<? extends Entity>> AURA_INSTANCES = Lists.newArrayList();
    private final HashMap<ResourceLocation, AttributeModifier> TALENT_MODIFIERS = Maps.newHashMap();
    private final HealingSources healingSources;
    private final FTZDamageTypes.DamageSources damageSources;
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
        serializeModifiers(tag);
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        deserializeAuras(nbt);
        deserializeModifiers(nbt);
    }
    private void serializeAuras(CompoundTag tag) {
        if (AURA_INSTANCES.isEmpty()) return;

        ListTag instances = new ListTag();
        for (AuraInstance<? extends Entity> auraInstance : AURA_INSTANCES) instances.add(auraInstance.serialize());

        tag.put("aura_instances", instances);
    }
    private void deserializeAuras(CompoundTag tag) {
        AURA_INSTANCES.clear();

        if (!tag.contains("aura_instances")) return;
        ListTag tags = tag.getList("aura_instances", Tag.TAG_COMPOUND);

        for (int i = 0; i < tags.size(); i++) AURA_INSTANCES.add(AuraInstance.deserialize(tags.getCompound(i), level));
    }
    private void serializeModifiers(CompoundTag tag) {
        ListTag talents = new ListTag();
        ListTag modifiers = new ListTag();

        for (Map.Entry<ResourceLocation, AttributeModifier> entry : TALENT_MODIFIERS.entrySet()) {
            talents.add(StringTag.valueOf(entry.getKey().toString()));
            modifiers.add(entry.getValue().save());
        }

        tag.put("talents", talents);
        tag.put("modifiers", modifiers);
    }
    private void deserializeModifiers(CompoundTag tag) {
        TALENT_MODIFIERS.clear();
        if (!tag.contains("talents") || !tag.contains("modifiers")) return;

        ListTag talents = tag.getList("talents", Tag.TAG_STRING);
        ListTag modifiers = tag.getList("modifiers", Tag.TAG_COMPOUND);

        if (talents.size() != modifiers.size()) return;

        for (int i = 0; i < talents.size(); i++) TALENT_MODIFIERS.put(new ResourceLocation(talents.getString(i)), AttributeModifier.load(modifiers.getCompound(i)));
    }
    public void tick() {
        AURA_INSTANCES.forEach(AuraInstance::tick);
    }
    public List<AuraInstance<? extends Entity>> getAuraInstances() {
        return AURA_INSTANCES;
    }
    public void addAuraInstance(AuraInstance<? extends Entity> instance) {
        if (!AURA_INSTANCES.contains(instance)) AURA_INSTANCES.add(instance);
        updateTracking();
    }
    public void removeAuraInstance(AuraInstance<?> instance) {
        AURA_INSTANCES.remove(instance);
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
        if (!TALENT_MODIFIERS.containsKey(id)) TALENT_MODIFIERS.put(id, new AttributeModifier(talent.getName(), talent.getAmount(), talent.getOperation()));
        return TALENT_MODIFIERS.get(id);
    }
}
