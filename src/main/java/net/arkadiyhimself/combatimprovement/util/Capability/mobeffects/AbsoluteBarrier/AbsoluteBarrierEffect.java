package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.AbsoluteBarrier;

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

public class AbsoluteBarrierEffect extends CapabilityAttacher {
    private static final Class<AbsoluteBarrier> ABSOLUTE_BARRIER_EFFECT_CLASS = AbsoluteBarrier.class;
    public static final Capability<AbsoluteBarrier> ABSOLUTE_BARRIER_EFFECT = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation ABSOLUTE_BARRIER_EEFFECT_RL = new ResourceLocation(CombatImprovement.MODID, "absolute_barrier_effect");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static AbsoluteBarrier getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<AbsoluteBarrier> get(LivingEntity entity) {
        return entity.getCapability(ABSOLUTE_BARRIER_EFFECT);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, LivingEntity entity) {
        genericAttachCapability(event, new AbsoluteBarrier(entity), ABSOLUTE_BARRIER_EFFECT, ABSOLUTE_BARRIER_EEFFECT_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(ABSOLUTE_BARRIER_EFFECT_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, AbsoluteBarrierEffect::attacher, AbsoluteBarrierEffect::get, true);
    }
}
