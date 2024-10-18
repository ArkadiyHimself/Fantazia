package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.advanced.spell.types.TargetedSpell;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.DAMHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesGetter;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.AurasInstancesHolder;
import net.arkadiyhimself.fantazia.events.FTZHooks;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.util.library.SphereBox;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AuraInstance<T extends Entity> {

    private final List<T> supposedlyInside = Lists.newArrayList();
    private final List<DynamicAttributeModifier> dynamicAttributeModifiers = Lists.newArrayList();
    @NotNull
    private final Entity owner;
    private final Level level;
    private Vec3 center;
    private final BasicAura<T> aura;
    private boolean removed = false;

    public AuraInstance(@NotNull Entity owner, BasicAura<T> aura) {
        this.level = owner.level();
        this.owner = owner;
        this.aura = aura;
        this.center = owner.getPosition(0f);

        if (level instanceof ServerLevel) LevelAttributesGetter.acceptConsumer(level, AurasInstancesHolder.class, aurasInstancesHolder -> aurasInstancesHolder.addAuraInstance(this));

        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : this.aura.getDynamicAttributeModifiers().entrySet()) {
            Function<LivingEntity, Float> floatFunction = (entity -> 1 - (entity.distanceTo(getOwner()) / getActualRange()));
            dynamicAttributeModifiers.add(new DynamicAttributeModifier(entry.getKey(), entry.getValue(), floatFunction));
        }
    }

    public BasicAura<T> getAura() {
        return aura;
    }

    public Level getLevel() {
        return level;
    }

    public void tick() {
        if (removed) return;
        FTZHooks.onAuraTick(this);
        this.center = owner.position();

        for (T entity : entitiesInside()) if (!supposedlyInside.contains(entity)) enterAura(entity);
        for (T entity : supposedlyInside) if (!entitiesInside().contains(entity)) exitAura(entity);
        supposedlyInside.removeIf(entity -> !entitiesInside().contains(entity));

        supposedlyInside.forEach(entity -> {
            if (aura.canAffect(entity, owner)) {
                aura.affectedTick(entity, owner);
                if (entity instanceof LivingEntity livingEntity) applyModifiers(livingEntity);
            } else if (entity instanceof LivingEntity livingEntity) removeModifiers(livingEntity);
        });

        if (aura.ownerCond(owner)) aura.ownerTick(owner);
        if (!level.isClientSide()) blocksInside().forEach(blockPos -> aura.blockTick(blockPos, this));
    }

    public SphereBox getSphericalBox() {
        return new SphereBox(getActualRange(), this.center);
    }

    public List<T> entitiesInside() {
        Class<T> type = aura.affectedClass();
        List<Entity> entities = getSphericalBox().entitiesInside(level);
        List<T> inside = Lists.newArrayList();
        for (Entity entity : entities) if (type.isInstance(entity)) inside.add(type.cast(entity));
        return inside;
    }

    public List<BlockPos> blocksInside() {
        return getSphericalBox().blocksInside(level);
    }

    public @NotNull Entity getOwner() {
        return owner;
    }

    public void enterAura(T entity) {
        FTZHooks.onAuraEnter(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.literal("entered"));
        supposedlyInside.add(entity);
        if (!aura.canAffect(entity, getOwner())) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        applyModifiers(livingEntity);
    }

    public void exitAura(T entity) {
        FTZHooks.onAuraExit(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.literal("left"));
        if (!(entity instanceof LivingEntity livingEntity)) return;
        removeModifiers(livingEntity);
    }

    public boolean notInside(Entity entity) {
        if (!aura.affectedClass().isInstance(entity) && !Fantazia.DEVELOPER_MODE) return true;
        return !supposedlyInside.contains(aura.affectedClass().cast(entity));
    }

    public void discard() {
        LevelAttributesGetter.acceptConsumer(level, AurasInstancesHolder.class, aurasInstancesHolder -> aurasInstancesHolder.removeAuraInstance(this));
        this.removed = true;
        entitiesInside().forEach(this::exitAura);
    }

    public void removeModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : aura.getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null && instance.hasModifier(entry.getValue().id())) instance.removeModifier(entry.getValue());
        }

        DAMHolder damHolder = LivingDataGetter.takeHolder(livingEntity, DAMHolder.class);
        if (damHolder == null) return;
        dynamicAttributeModifiers.forEach(damHolder::removeDAM);
    }

    public void applyModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Holder<Attribute>, AttributeModifier> entry : aura.getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null && !instance.hasModifier(entry.getValue().id())) instance.addTransientModifier(entry.getValue());
        }

        DAMHolder damHolder = LivingDataGetter.takeHolder(livingEntity, DAMHolder.class);
        if (damHolder == null) return;
        dynamicAttributeModifiers.forEach(damHolder::addDAM);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putString("aura", this.aura.getID().toString());
        tag.putInt("owner", this.owner.getId());
        return tag;
    }

    public static AuraInstance<? extends Entity> deserialize(CompoundTag tag, Level level) {
        ResourceLocation auraID = ResourceLocation.parse(tag.getString("aura"));
        int ownerID = tag.getInt("owner");

        BasicAura<?> aura = FantazicRegistry.AURAS.get(auraID);
        Entity owner = level.getEntity(ownerID);

        if (aura == null) throw new IllegalStateException("Could not resolve aura: " + auraID);
        else if (owner == null) throw new IllegalStateException("Could not resolve owner");

        return new AuraInstance<>(owner, aura);
    }

    private float getActualRange() {
        float initial = aura.getRadius();
        if (!(owner instanceof LivingEntity livingEntity)) return initial;

        AttributeInstance addition = livingEntity.getAttribute(FTZAttributes.AURA_RANGE_ADDITION);
        return addition == null ? initial : initial + (float) addition.getValue();
    }
}
