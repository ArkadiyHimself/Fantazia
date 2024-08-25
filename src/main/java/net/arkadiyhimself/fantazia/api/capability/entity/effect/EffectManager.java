package net.arkadiyhimself.fantazia.api.capability.entity.effect;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.IHealReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.*;
import net.arkadiyhimself.fantazia.api.fantazicevents.VanillaEventsExtension;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.function.Function;

public class EffectManager extends LivingEntityCapability {
    private final HashMap<MobEffect, EffectHolder> EFFECTS = Maps.newHashMap();
    public EffectManager(LivingEntity entity) {
        super(entity);
        EffectProvider.provide(this);
    }
    @Override
    public EntityCapabilityStatusPacket createUpdatePacket() {
        return new SimpleEntityCapabilityStatusPacket(this.livingEntity.getId(), EffectGetter.EFFECT_RL, this);
    }
    @Override
    public SimpleChannel getNetworkChannel() {
        return NetworkHandler.INSTANCE;
    }
    @Override
    public CompoundTag serializeNBT(boolean toDisk) {
        CompoundTag tag = new CompoundTag();

        for (EffectHolder effectHolder : EFFECTS.values()) if (effectHolder.ID() != null) tag.put(effectHolder.ID(), effectHolder.serialize(toDisk));

        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag tab, boolean fromDisk) {
        for (EffectHolder effectHolder : EFFECTS.values()) if (tab.contains(effectHolder.ID())) effectHolder.deserialize(tab.getCompound(effectHolder.ID()), fromDisk);
    }
    public void tick() {
        EFFECTS.values().forEach(EffectHolder::tick);
        updateTracking();
    }
    public void grantEffect(Function<LivingEntity, EffectHolder> effect) {
        EffectHolder effectHolder = effect.apply(livingEntity);
        MobEffect mobEffect = effectHolder.getEffect();
        if (!FTZMobEffects.Application.isApplicable(livingEntity.getType(), mobEffect)) return;
        if (hasEffect(effectHolder.getEffect())) return;
        EFFECTS.put(effectHolder.getEffect(), effectHolder);
    }
    public <T extends EffectHolder> LazyOptional<T> getEffect(Class<T> tClass) {
        T ability = takeEffect(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    public void effectAdded(MobEffectInstance instance) {
        for (EffectHolder effectHolder : EFFECTS.values()) if (effectHolder.getEffect() == instance.getEffect()) effectHolder.added(instance);
    }
    public void effectEnded(MobEffectInstance instance) {
        for (EffectHolder effectHolder : EFFECTS.values()) if (effectHolder.getEffect() == instance.getEffect()) effectHolder.ended();
    }
    public void onHit(LivingAttackEvent event) {
        EFFECTS.values().stream().filter(IDamageReacting.class::isInstance).forEach(effectHolder -> ((IDamageReacting) effectHolder).onHit(event));
    }
    public void onHit(LivingHurtEvent event) {
        EFFECTS.values().stream().filter(IDamageReacting.class::isInstance).forEach(effectHolder -> ((IDamageReacting) effectHolder).onHit(event));
    }
    public void onHit(LivingDamageEvent event) {
        EFFECTS.values().stream().filter(IDamageReacting.class::isInstance).forEach(effectHolder -> ((IDamageReacting) effectHolder).onHit(event));
    }
    public void onHeal(VanillaEventsExtension.AdvancedHealEvent event) {
        EFFECTS.values().stream().filter(IHealReacting.class::isInstance).forEach(effectHolder -> ((IHealReacting) effectHolder).onHeal(event));
    }
    public void respawm() {
        EFFECTS.values().forEach(EffectHolder::respawn);
    }
    @Nullable
    public <T extends EffectHolder> T takeEffect(Class<T> tClass) {
        for (EffectHolder effectHolder : EFFECTS.values()) if (tClass == effectHolder.getClass()) return tClass.cast(effectHolder);
        return null;
    }
    public boolean hasEffect(MobEffect mobEffect) {
        for (EffectHolder effectHolder : EFFECTS.values()) if (effectHolder.getEffect() == mobEffect) return true;
        return false;
    }
    public <T extends EffectHolder> boolean hasEffect(Class<T> tClass) {
        for (EffectHolder effectHolder : EFFECTS.values()) if (tClass == effectHolder.getClass()) return true;
        return false;
    }
    private static class EffectProvider {
        private static void provide(EffectManager effectManager) {
            effectManager.grantEffect(StunEffect::new);
            effectManager.grantEffect(BarrierEffect::new);
            effectManager.grantEffect(LayeredBarrierEffect::new);
            effectManager.grantEffect(AbsoluteBarrierEffect::new);
            effectManager.grantEffect(FuryEffect::new);
            effectManager.grantEffect(FrozenEffect::new);
            effectManager.grantEffect(DisarmEffect::new);
            effectManager.grantEffect(DeafenedEffect::new);
            effectManager.grantEffect(DoomedEffect::new);
            effectManager.grantEffect(HaemorrhageEffect::new);
        }
    }
}
