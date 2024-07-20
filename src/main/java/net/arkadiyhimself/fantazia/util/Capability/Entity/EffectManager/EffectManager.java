package net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager;

import com.google.common.collect.Maps;
import dev._100media.capabilitysyncer.core.LivingEntityCapability;
import dev._100media.capabilitysyncer.network.EntityCapabilityStatusPacket;
import dev._100media.capabilitysyncer.network.SimpleEntityCapabilityStatusPacket;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.AbsoluteBarrierEffect;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.BarrierEffect;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.StunEffect;
import net.arkadiyhimself.fantazia.util.Interfaces.IDamageReacting;
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
    public void tick() {
        EFFECTS.values().forEach(EffectHolder::tick);
        updateTracking();
    }
    public void grantEffect(Function<LivingEntity, EffectHolder> effect) {
        EffectHolder effectHolder = effect.apply(livingEntity);
        if (hasEffect(effectHolder.getEffect())) return;
        EFFECTS.put(effectHolder.getEffect(), effectHolder);
    }
    public <T extends EffectHolder> LazyOptional<T> getEffect(Class<T> tClass) {
        T ability = takeEffect(tClass);
        return ability == null ? LazyOptional.empty() : LazyOptional.of(() -> ability);
    }
    public void effectAdded(MobEffectInstance instance) {
        EFFECTS.values().forEach(effectHolder -> {
            if (effectHolder.getEffect() == instance.getEffect()) effectHolder.added(instance);
        });
    }
    public void effectEnded(MobEffectInstance instance) {
        EFFECTS.values().forEach(effectHolder -> {
            if (effectHolder.getEffect() == instance.getEffect()) effectHolder.ended();
        });
    }
    public void onHit(LivingAttackEvent event) {
        EFFECTS.values().forEach(effectHolder -> {
            if (effectHolder instanceof IDamageReacting damageReacting) damageReacting.onHit(event);
        });
    }
    public void onHit(LivingHurtEvent event) {
        EFFECTS.values().forEach(effectHolder -> {
            if (effectHolder instanceof IDamageReacting damageReacting) damageReacting.onHit(event);
        });
    }
    public void onHit(LivingDamageEvent event) {
        EFFECTS.values().forEach(effectHolder -> {
            if (effectHolder instanceof IDamageReacting damageReacting) damageReacting.onHit(event);
        });
    }
    public void respawm() {
        EFFECTS.values().forEach(EffectHolder::respawn);
    }
    @Nullable
    public <T extends EffectHolder> T takeEffect(Class<T> tClass) {
        T object = null;
        for (EffectHolder effectHolder : EFFECTS.values()) {
            if (tClass == effectHolder.getClass()) {
                object = tClass.cast(effectHolder);
                break;
            }
        }
        return object;
    }
    public boolean hasEffect(MobEffect mobEffect) {
        for (EffectHolder effectHolder : EFFECTS.values()) {
            if (effectHolder.getEffect() == mobEffect) return true;
        }
        return false;
    }
    public <T extends EffectHolder> boolean hasEffect(Class<T> tClass) {
        for (EffectHolder effectHolder : EFFECTS.values()) {
            if (tClass == effectHolder.getClass()) return true;
        }
        return false;
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
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        EFFECTS.values().forEach(inbTsaver -> tag.merge(inbTsaver.serialize()));
        return tag;
    }
    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        EFFECTS.values().forEach(inbTsaver -> inbTsaver.deserialize(nbt));
    }
    private static class EffectProvider {
        private static void provide(EffectManager effectManager) {
            effectManager.grantEffect(StunEffect::new);
            effectManager.grantEffect(BarrierEffect::new);
            effectManager.grantEffect(LayeredBarrierEffect::new);
            effectManager.grantEffect(AbsoluteBarrierEffect::new);
        }
    }
}
