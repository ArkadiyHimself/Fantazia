package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.DAMHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.AurasInstancesHolder;
import net.arkadiyhimself.fantazia.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.events.FantazicHooks;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.util.library.SphereBox;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AuraInstance {

    private final List<Entity> supposedlyInside = Lists.newArrayList();
    private final List<DynamicAttributeModifier> dynamicAttributeModifiers = Lists.newArrayList();
    private final Entity owner;
    private final Level level;
    private Vec3 center;
    private final Holder<BasicAura> aura;
    private boolean removed = false;

    public AuraInstance(@NotNull Entity owner, Holder<BasicAura> aura) {
        this.level = owner.level();
        this.owner = owner;
        this.aura = aura;
        this.center = owner.getPosition(0f);

        if (level instanceof ServerLevel) LevelAttributesHelper.acceptConsumer(level, AurasInstancesHolder.class, aurasInstancesHolder -> aurasInstancesHolder.addAuraInstance(this));

        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : this.aura.value().getDynamicAttributeModifiers().entrySet()) {
            Function<LivingEntity, Float> floatFunction = (entity -> 1 - (entity.distanceTo(getOwner()) / getActualRange()));
            dynamicAttributeModifiers.add(new DynamicAttributeModifier(entry.getKey(), entry.getValue(), floatFunction));
        }
    }

    public Holder<BasicAura> getAura() {
        return aura;
    }

    public Level getLevel() {
        return level;
    }

    public void tick() {
        if (owner == null || !owner.isAlive()) removed = true;
        if (removed) return;

        FantazicHooks.onAuraTick(this);
        this.center = owner.position();

        for (Entity entity : entitiesInside()) if (!supposedlyInside.contains(entity)) enterAura(entity);
        for (Entity entity : supposedlyInside) if (!entitiesInside().contains(entity)) exitAura(entity);
        supposedlyInside.removeIf(entity -> !entitiesInside().contains(entity));

        supposedlyInside.forEach(entity -> {
            if (aura.value().affects(entity, owner)) {
                aura.value().affectedTick(entity, owner);
                if (entity instanceof LivingEntity livingEntity) applyModifiers(livingEntity);
            } else if (entity instanceof LivingEntity livingEntity) removeModifiers(livingEntity);
        });

        if (aura.value().ownerCond(owner)) aura.value().ownerTick(owner);
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

    public void enterAura(Entity entity) {
        FantazicHooks.onAuraEnter(this, entity);
        if (getOwner() instanceof ServerPlayer player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.literal("entered"));
        supposedlyInside.add(entity);
        if (!aura.value().affects(entity, getOwner())) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        applyModifiers(livingEntity);
    }

    public void exitAura(Entity entity) {
        FantazicHooks.onAuraExit(this, entity);
        if (getOwner() instanceof ServerPlayer player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.literal("left"));
        if (!(entity instanceof LivingEntity livingEntity)) return;
        removeModifiers(livingEntity);
    }

    public boolean isInside(Entity entity) {
        return getSphericalBox().entitiesInside(level).contains(entity);
    }

    public void discard() {
        this.removed = true;
        entitiesInside().forEach(this::exitAura);
        IPacket.levelUpdate(getLevel());
    }

    public void removeModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : aura.value().getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null && instance.hasModifier(entry.getValue().id())) instance.removeModifier(entry.getValue());
        }

        DAMHolder damHolder = LivingDataHelper.takeHolder(livingEntity, DAMHolder.class);
        if (damHolder == null) return;
        dynamicAttributeModifiers.forEach(damHolder::removeDAM);
    }

    public void applyModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : aura.value().getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null && !instance.hasModifier(entry.getValue().id())) instance.addTransientModifier(entry.getValue());
        }

        DAMHolder damHolder = LivingDataHelper.takeHolder(livingEntity, DAMHolder.class);
        if (damHolder == null) return;
        dynamicAttributeModifiers.forEach(damHolder::addDAM);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("aura", BasicAura.getID(aura).toString());
        tag.putInt("owner", this.owner.getId());
        tag.putBoolean("removed", this.removed);
        return tag;
    }

    public static AuraInstance deserialize(CompoundTag tag, Level level) {
        ResourceLocation auraID = ResourceLocation.parse(tag.getString("aura"));
        int ownerID = tag.getInt("owner");

        BasicAura aura = FantazicRegistries.AURAS.get(auraID);
        Entity owner = level.getEntity(ownerID);

        if (aura == null || owner == null) return null;

        AuraInstance instance = new AuraInstance(owner, FantazicRegistries.AURAS.wrapAsHolder(aura));
        instance.removed = tag.getBoolean("removed");

        return instance;
    }

    private float getActualRange() {
        float initial = aura.value().getRadius();
        if (!(owner instanceof LivingEntity livingEntity)) return initial;

        AttributeInstance addition = livingEntity.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
        return addition == null ? initial : initial + (float) addition.getValue();
    }

    public boolean removed() {
        return this.removed;
    }
}
