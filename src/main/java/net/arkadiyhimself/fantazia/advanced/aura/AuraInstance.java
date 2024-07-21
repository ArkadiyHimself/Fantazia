package net.arkadiyhimself.fantazia.advanced.aura;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.events.custom.NewEvents;
import net.arkadiyhimself.fantazia.util.library.SPHEREBOX;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.advanced.capability.level.LevelCapGetter;
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
    public final List<T> supposedlyAffected = Lists.newArrayList();
    private boolean removed = false;
    public int partialTick = 20;
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
        NewEvents.onAuraTick(this);
        partialTick--;
        if (partialTick <= 1) partialTick = 20;
        this.center = owner.position();
        entitiesInside().forEach(entity -> {
            if (!supposedlyAffected.contains(entity)) enterAura(entity);
        });
        supposedlyAffected.forEach(entity -> {
            if (!entitiesInside().contains(entity)) exitAura(entity);
        });
        supposedlyAffected.removeIf(entity -> !entitiesInside().contains(entity));
        if (aura.ownerConditions.test(owner)) {
            aura.onTickOwner.accept(owner);
        }
        if (!level.isClientSide()) {
            blocksInside().forEach(blockPos -> aura.onTickBlock.accept(blockPos, this));
        }
    }
    public SPHEREBOX getSphericalBox() {
        return new SPHEREBOX(aura.getRadius(), this.center);
    }
    @SuppressWarnings("unchecked")
    public List<T> entitiesInside() {
        Class<T> type = aura.getAffectedType();
        return (List<T>) getSphericalBox().entitiesInside(level).stream().filter(type::isInstance).filter(entity -> entity != owner).toList();
    }
    public List<BlockPos> blocksInside() {
        return getSphericalBox().blocksInside(level);
    }
    public M getOwner() {
        return owner;
    }
    public void enterAura(T entity) {
        NewEvents.onAuraEnter(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("entered"));
        supposedlyAffected.add(entity);
        if (entity instanceof LivingEntity livingEntity) {
            if (!aura.canAffect(entity, getOwner())) return;
            aura.attributeModifiers.forEach((attribute, attributeModifier) -> {
                AttributeInstance instance = livingEntity.getAttribute(attribute);
                if (instance != null && !instance.hasModifier(attributeModifier)) {
                    instance.addTransientModifier(attributeModifier);
                }
            });
        }
    }
    public void exitAura(T entity) {
        NewEvents.onAuraExit(this, entity);
        if (getOwner() instanceof Player player && Fantazia.DEVELOPER_MODE) player.sendSystemMessage(Component.translatable("left"));
        if (entity instanceof LivingEntity livingEntity) {
            aura.attributeModifiers.forEach((attribute, attributeModifier) -> {
                AttributeInstance instance = livingEntity.getAttribute(attribute);
                if (instance != null && instance.hasModifier(attributeModifier))
                    instance.removeModifier(attributeModifier);
            });
        }
    }
    public void discard() {
        if (!level.isClientSide()) LevelCapGetter.get(level).ifPresent(levelCap -> levelCap.removeAuraInstance(this));
        this.removed = true;
        entitiesInside().forEach(this::exitAura);
    }
    public boolean isInside(Entity entity) {
        return supposedlyAffected.contains(entity);
    }
}
