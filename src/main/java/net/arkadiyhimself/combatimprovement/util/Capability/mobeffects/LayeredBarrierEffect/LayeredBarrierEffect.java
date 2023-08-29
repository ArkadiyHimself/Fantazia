package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class LayeredBarrierEffect extends CapabilityAttacher {
    private static final Class<LayeredBarrier> LAYERED_BARRIER_EFFECT_CLASS = LayeredBarrier.class;
    public static final Capability<LayeredBarrier> LAYERED_BARRIER_EFFECT = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation LAYERED_BARRIER_EEFFECT_RL = new ResourceLocation(CombatImprovement.MODID, "layered_barrier_effect");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static LayeredBarrier getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<LayeredBarrier> get(LivingEntity entity) {
        return entity.getCapability(LAYERED_BARRIER_EFFECT);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, LivingEntity entity) {
        genericAttachCapability(event, new LayeredBarrier(entity), LAYERED_BARRIER_EFFECT, LAYERED_BARRIER_EEFFECT_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(LAYERED_BARRIER_EFFECT_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, LayeredBarrierEffect::attacher, LayeredBarrierEffect::get, true);
    }
}
