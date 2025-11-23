package net.arkadiyhimself.fantazia.common.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.FantazicHooks;
import net.arkadiyhimself.fantazia.common.advanced.dynamic_attribute_modifier.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.common.api.AttributeTemplate;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_data.holders.DAMHolder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.networking.attachment_syncing.IAttachmentSync;
import net.arkadiyhimself.fantazia.util.library.SphereBox;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;
import oshi.util.tuples.Pair;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

public class AuraInstance {

    private final List<Entity> supposedlyInside = Lists.newArrayList();
    private final List<Entity> actuallyAffected = Lists.newArrayList();
    private final List<DynamicAttributeModifier> proximityAttributeModifiers = Lists.newArrayList();
    private final Entity owner;
    private final Level level;
    private Vec3 center;
    private final Holder<Aura> aura;
    private boolean removed = false;

    private final int amplifier;

    public AuraInstance(@NotNull Entity owner, Holder<Aura> aura, int amplifier) {
        this.level = owner.level();
        this.owner = owner;
        this.aura = aura;
        this.center = owner.getPosition(0f);
        this.amplifier = amplifier;

        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : this.aura.value().getProximityAttributeModifiers().entrySet()) {
            Function<LivingEntity, Float> floatFunction = (entity -> 1 - (entity.distanceTo(getOwner()) / getActualRange()));
            proximityAttributeModifiers.add(new DynamicAttributeModifier(entry.getKey(), entry.getValue().create(amplifier), floatFunction));
        }
    }

    public AuraInstance(@NotNull Entity owner, Holder<Aura> aura) {
        this(owner, aura, 0);
    }

    public Holder<Aura> getAura() {
        return aura;
    }

    public Level getLevel() {
        return level;
    }

    public void tick() {
        if (owner == null || owner.isRemoved()) {
            discard();
            return;
        }

        FantazicHooks.onAuraTick(this);
        this.center = owner.position();

        for (Entity entity : entitiesInside()) if (!supposedlyInside.contains(entity)) physicallyEnter(entity);
        supposedlyInside.removeIf(entity -> {
            boolean inside = entitiesInside().contains(entity);
            if (!inside) physicallyExit(entity);
            return !inside;
        });

        if (aura.value().ownerCond(owner)) aura.value().instanceTick(this);
        if (!level.isClientSide()) blocksInside().forEach(blockPos -> aura.value().blockTick(blockPos, this));
    }

    public SphereBox getSphericalBox() {
        return new SphereBox(getActualRange(), this.center);
    }

    public List<Entity> entitiesInside() {
        return getSphericalBox().entitiesInside(level);
    }

    public List<BlockPos> blocksInside() {
        return getSphericalBox().blocksInside(level);
    }

    public @NotNull Entity getOwner() {
        return owner;
    }

    // just the fact of being inside aura instance
    public void physicallyEnter(Entity entity) {
        FantazicHooks.onAuraEnter(this, entity);
        if (getOwner() instanceof ServerPlayer && Fantazia.DEVELOPER_MODE) Fantazia.LOGGER.info("Entered aura: {}", entity);
        supposedlyInside.add(entity);
    }

    public void physicallyExit(Entity entity) {
        FantazicHooks.onAuraExit(this, entity);
        if (getOwner() instanceof ServerPlayer && Fantazia.DEVELOPER_MODE) Fantazia.LOGGER.info("Exited aura: {}", entity);
    }

    // being actually affected by the aura instance
    public boolean affectedByAura(Entity entity) {
        if (actuallyAffected.contains(entity)) return false;
        actuallyAffected.add(entity);
        if (entity instanceof LivingEntity livingEntity) applyModifiers(livingEntity);
        return true;
    }

    public boolean unaffectedByAura(Entity entity) {
        if (!actuallyAffected.contains(entity)) return false;
        actuallyAffected.remove(entity);
        if (entity instanceof LivingEntity livingEntity) removeModifiers(livingEntity);
        return true;
    }

    public boolean isInside(Entity entity) {
        return getSphericalBox().entitiesInside(level).contains(entity);
    }

    public void discard() {
        this.removed = true;
        supposedlyInside.stream().toList().forEach(this::physicallyExit);
        actuallyAffected.stream().toList().forEach(this::unaffectedByAura);
        if (getLevel() instanceof ServerLevel serverLevel) IAttachmentSync.updateAuraInstances(serverLevel);
    }

    public void applyModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : aura.value().getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance == null) continue;
            AttributeModifier modifier = entry.getValue().create(amplifier);
            instance.removeModifier(modifier);
            instance.addTransientModifier(modifier);
        }

        DAMHolder damHolder = LivingDataHelper.takeHolder(livingEntity, DAMHolder.class);
        if (damHolder == null) return;
        proximityAttributeModifiers.forEach(damHolder::addOrReplaceDAM);
        for (Map.Entry<Holder<Attribute>, Pair<AttributeTemplate, Function<LivingEntity, Float>>> entry : aura.value().getDynamicAttributeModifiers().entrySet()) {
            Pair<AttributeTemplate, Function<LivingEntity, Float>> pair = entry.getValue();
            damHolder.addOrReplaceDAM(new DynamicAttributeModifier(entry.getKey(), pair.getA().create(amplifier), pair.getB()));
        }
    }

    public void removeModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Holder<Attribute>, AttributeTemplate> entry : aura.value().getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null) instance.removeModifier(entry.getValue().id());
        }

        DAMHolder damHolder = LivingDataHelper.takeHolder(livingEntity, DAMHolder.class);
        if (damHolder == null) return;
        proximityAttributeModifiers.forEach(damHolder::removeDAM);
        for (Map.Entry<Holder<Attribute>, Pair<AttributeTemplate, Function<LivingEntity, Float>>> entry : aura.value().getDynamicAttributeModifiers().entrySet()) {
            damHolder.removeDAM(entry.getValue().getA().id());
        }
    }

    private float getActualRange() {
        float initial = aura.value().getRadius();
        if (!(owner instanceof LivingEntity livingEntity)) return initial;

        AttributeInstance addition = livingEntity.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
        return addition == null ? initial : initial + (float) addition.getValue();
    }

    public boolean matchesFilter(Entity entity) {
        return aura.value().matchesFilter(entity, owner);
    }

    public boolean primaryFilter(Entity entity) {
        return aura.value().primary(entity, owner);
    }

    public double distanceToCenter(Entity entity) {
        return entity.distanceTo(owner);
    }

    public void tickOnEntity(Entity entity) {
        this.aura.value().affectedTick(entity, this);
    }

    public void applyMobEffects(LivingEntity livingEntity) {
        if (!level.isClientSide()) for (Map.Entry<Holder<MobEffect>, Integer> entry : aura.value().getMobEffects().entrySet()) {
            livingEntity.addEffect(new MobEffectInstance(entry.getKey(), 2, entry.getValue()));
        }
    }

    public int getAmplifier() {
        return amplifier;
    }

    public boolean removed() {
        return this.removed;
    }

    public AuraInstance copy(Entity newOwner) {
        return new AuraInstance(newOwner, aura, amplifier);
    }

    public String getDescriptionId() {
        return aura.value().getDescriptionId();
    }

    @Override
    public String toString() {
        String desc = getDescriptionId();
        if (amplifier > 0) desc += " x " + (this.amplifier + 1);
        desc += ", owner: " + (owner == null ? "null" : owner.getName());
        return desc;
    }

    public CompoundTag serializeSync() {
        CompoundTag tag = new CompoundTag();
        tag.putString("aura", aura.value().getId().toString());
        tag.putInt("owner", this.owner.getId());
        tag.putBoolean("removed", this.removed);
        tag.putInt("amplifier", this.amplifier);
        return tag;
    }

    public CompoundTag serializeSave() {
        CompoundTag tag = new CompoundTag();
        tag.putString("aura", aura.value().getId().toString());
        tag.putBoolean("removed", this.removed);
        tag.putInt("amplifier", this.amplifier);
        return tag;
    }

    public static AuraInstance deserializeSync(CompoundTag tag, ClientLevel level) {
        ResourceLocation auraID = ResourceLocation.parse(tag.getString("aura"));
        int ownerID = tag.getInt("owner");

        Aura aura = FantazicRegistries.AURAS.get(auraID);
        Entity owner = level.getEntity(ownerID);

        if (aura == null || owner == null) return null;

        int amplifier = tag.getInt("amplifier");

        AuraInstance instance = new AuraInstance(owner, FantazicRegistries.AURAS.wrapAsHolder(aura), amplifier);
        instance.removed = tag.getBoolean("removed");

        return instance;
    }

    public static Function<Entity, AuraInstance> deserializeSave(CompoundTag tag) {
        ResourceLocation auraID = ResourceLocation.parse(tag.getString("aura"));

        Optional<Holder.Reference<Aura>> aura = FantazicRegistries.AURAS.getHolder(auraID);

        if (aura.isEmpty()) return entity -> null;

        int amplifier = tag.getInt("amplifier");
        boolean removed = tag.getBoolean("removed");

        return entity -> {
            AuraInstance instance = new AuraInstance(entity, aura.get(), amplifier);
            instance.removed = removed;
            return instance;
        };
    }
}
