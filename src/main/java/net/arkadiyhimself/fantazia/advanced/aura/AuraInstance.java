package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.util.library.SPHEREBOX;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import org.apache.commons.compress.utils.Lists;

import java.util.List;

public class AuraInstance<T extends Entity, M extends Entity> {
    private final M owner;
    private final Level level;
    private Vec3 center;
    private final BasicAura<T,M> aura;
    private final List<T> SUPPOSEDLY_INSIDE = Lists.newArrayList();
    private boolean removed = false;
    public AuraInstance(M owner, BasicAura<T,M> aura, Level level) {
        this.level = level;
        this.owner = owner;
        this.aura = aura;
        this.center = owner.getPosition(0f);
        if (!level.isClientSide()) LevelCapGetter.get(level).ifPresent(levelCap -> levelCap.addAuraInstance((AuraInstance<Entity, Entity>) this));
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
            if (aura.primary(entity, owner) && aura.secondary(entity, owner)) aura.entityTick(entity, owner);
        });
        if (aura.ownerCond(owner)) aura.ownerTick(owner);
        if (!level.isClientSide()) blocksInside().forEach(blockPos -> aura.blockTick(blockPos, this));
    }
    public SPHEREBOX getSphericalBox() {
        return new SPHEREBOX(aura.getRadius(), this.center);
    }
    public List<T> entitiesInside() {
        Class<T> type = aura.getAffectedType();
        List<Entity> entities = getSphericalBox().entitiesInside(level);
        List<T> inside = Lists.newArrayList();
        for (Entity entity : entities) if (type.isInstance(entity)) inside.add(type.cast(entity));
        return inside;
    }
    public List<BlockPos> blocksInside() {
        return getSphericalBox().blocksInside(level);
    }
    public M getOwner() {
        return owner;
    }
    public void enterAura(T entity) {
        FTZEvents.onAuraEnter(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("entered"));
        SUPPOSEDLY_INSIDE.add(entity);
        if (entity instanceof LivingEntity livingEntity) {
            if (!aura.canAffect(entity, getOwner())) return;
            aura.getAttributeModifiers().forEach((attribute, attributeModifier) -> {
                AttributeInstance instance = livingEntity.getAttribute(attribute);
                if (instance != null && !instance.hasModifier(attributeModifier)) {
                    instance.addTransientModifier(attributeModifier);
                }
            });
        }
    }
    public void exitAura(T entity) {
        FTZEvents.onAuraExit(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("left"));
        if (entity instanceof LivingEntity livingEntity) {
            aura.getAttributeModifiers().forEach((attribute, attributeModifier) -> {
                AttributeInstance instance = livingEntity.getAttribute(attribute);
                if (instance != null && instance.hasModifier(attributeModifier))
                    instance.removeModifier(attributeModifier);
            });
        }
    }
    public boolean isInside(Entity entity) {
        return SUPPOSEDLY_INSIDE.contains(entity);
    }
    public void discard() {
        if (!level.isClientSide()) LevelCapGetter.get(level).ifPresent(levelCap -> levelCap.removeAuraInstance(this));
        this.removed = true;
        entitiesInside().forEach(this::exitAura);
    }
}
