package net.arkadiyhimself.fantazia.api.capability.entity.effect;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class EffectGetter extends CapabilityAttacher {
    public static <T extends EffectHolder> @Nullable T takeEffectHolder(LivingEntity livingEntity, Class<T> tClass) {
        EffectManager effectManager = getUnwrap(livingEntity);
        if (effectManager == null) return null;
        return effectManager.takeEffect(tClass);
    }
    public static <T extends EffectHolder> void effectConsumer(LivingEntity livingEntity, Class<T> tClass, NonNullConsumer<T> consumer) {
        EffectManager effectManager = getUnwrap(livingEntity);
        if (effectManager == null) return;
        effectManager.getEffect(tClass).ifPresent(consumer);
    }
    private static final Class<EffectManager> EFFECT_CLASS = EffectManager.class;
    public static final Capability<EffectManager> EFFECT = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation EFFECT_RL = Fantazia.res("effect");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static EffectManager getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<EffectManager> get(LivingEntity entity) {
        return entity.getCapability(EFFECT);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, LivingEntity livingEntity) {
        genericAttachCapability(event, new EffectManager(livingEntity), EFFECT, EFFECT_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(EFFECT_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, EffectGetter::attacher, EffectGetter::get, true);
    }
}
