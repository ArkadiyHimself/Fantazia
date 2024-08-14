package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.dynamicattributemodifying.DynamicAttributeModifier;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataManager;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.DAMData;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.util.library.SPHEREBOX;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
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

import java.util.List;
import java.util.Map;
import java.util.function.Function;

public class AuraInstance<T extends Entity, M extends Entity> {
    private final List<T> SUPPOSEDLY_INSIDE = Lists.newArrayList();
    private final List<DynamicAttributeModifier> DAMs = Lists.newArrayList();
    @NotNull
    private final M owner;
    private final Level level;
    private Vec3 center;
    private final BasicAura<T,M> aura;
    private boolean removed = false;
    @SuppressWarnings("unchecked")
    public AuraInstance(M owner, BasicAura<T,M> aura, Level level) {
        this.level = level;
        this.owner = owner;
        this.aura = aura;
        this.center = owner.getPosition(0f);
        if (!level.isClientSide()) LevelCapGetter.get(level).ifPresent(levelCap -> levelCap.addAuraInstance((AuraInstance<Entity, Entity>) this));

        for (Map.Entry<Attribute, AttributeModifier> entry : this.aura.getDynamicAttributeModifiers().entrySet()) {
            Function<LivingEntity, Float> percent = (entity -> {
                double distance = entity.distanceTo(getOwner());
                double rad = aura.getRadius();
                return 1 - (float) ((float) distance / rad);
            });
            DynamicAttributeModifier DAM = new DynamicAttributeModifier(entry.getKey(), entry.getValue(), percent);
            DAMs.add(DAM);
        }
    }
    public BasicAura<T,M> getAura() {
        return aura;
    }
    public Level getLevel() {
        return level;
    }
    public void tick() {
        if (removed) return;
        FTZEvents.onAuraTick(this);
        this.center = owner.position();

        for (T entity : entitiesInside()) if (!SUPPOSEDLY_INSIDE.contains(entity)) enterAura(entity);
        for (T entity : SUPPOSEDLY_INSIDE) if (!entitiesInside().contains(entity)) exitAura(entity);
        SUPPOSEDLY_INSIDE.removeIf(entity -> !entitiesInside().contains(entity));

        SUPPOSEDLY_INSIDE.forEach(entity -> {
            if (aura.canAffect(entity, owner)) {
                aura.entityTick(entity, owner);
                if (entity instanceof LivingEntity livingEntity) removeModifiers(livingEntity);
            } else if (entity instanceof LivingEntity livingEntity) applyModifiers(livingEntity);
        });
        if (aura.ownerCond(owner)) aura.ownerTick(owner);
        if (!level.isClientSide()) blocksInside().forEach(blockPos -> aura.blockTick(blockPos, this));
    }
    public SPHEREBOX getSphericalBox() {
        return new SPHEREBOX(aura.getRadius(), this.center);
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
    public @NotNull M getOwner() {
        return owner;
    }
    public void enterAura(T entity) {
        FTZEvents.onAuraEnter(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("entered"));
        SUPPOSEDLY_INSIDE.add(entity);
        if (!aura.canAffect(entity, getOwner())) return;
        if (!(entity instanceof LivingEntity livingEntity)) return;
        removeModifiers(livingEntity);
    }
    public void exitAura(T entity) {
        FTZEvents.onAuraExit(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("left"));
        if (!(entity instanceof LivingEntity livingEntity)) return;
        applyModifiers(livingEntity);
    }
    public boolean notInside(Entity entity) {
        if (!aura.affectedClass().isInstance(entity)) return true;
        return !SUPPOSEDLY_INSIDE.contains(aura.affectedClass().cast(entity));
    }
    public void discard() {
        if (!level.isClientSide()) LevelCapGetter.get(level).ifPresent(levelCap -> levelCap.removeAuraInstance(this));
        this.removed = true;
        entitiesInside().forEach(this::exitAura);
    }
    public void applyModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Attribute, AttributeModifier> entry : aura.getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null && instance.hasModifier(entry.getValue())) instance.removeModifier(entry.getValue());
        }

        DataManager dataManager = DataGetter.getUnwrap(livingEntity);
        if (dataManager == null) return;
        DAMData damData = dataManager.takeData(DAMData.class);
        if (damData == null) return;
        DAMs.forEach(damData::removeDAM);
    }
    public void removeModifiers(LivingEntity livingEntity) {
        for (Map.Entry<Attribute, AttributeModifier> entry : aura.getAttributeModifiers().entrySet()) {
            AttributeInstance instance = livingEntity.getAttribute(entry.getKey());
            if (instance != null && !instance.hasModifier(entry.getValue())) instance.addTransientModifier(entry.getValue());
        }
        DataManager dataManager = DataGetter.getUnwrap(livingEntity);
        if (dataManager == null) return;
        DAMData damData = dataManager.takeData(DAMData.class);
        if (damData == null) return;
        DAMs.forEach(damData::addDAM);
    }
}
