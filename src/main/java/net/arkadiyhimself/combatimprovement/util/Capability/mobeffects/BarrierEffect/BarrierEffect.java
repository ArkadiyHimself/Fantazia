package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect;

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

public class BarrierEffect extends CapabilityAttacher {
    private static final Class<Barrier> BARRIER_EFFECT_CLASS = Barrier.class;
    public static final Capability<Barrier> BARRIER_EFFECT = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation BARRIER_EEFFECT_RL = new ResourceLocation(CombatImprovement.MODID, "barrier_effect");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static Barrier getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<Barrier> get(LivingEntity entity) {
        return entity.getCapability(BARRIER_EFFECT);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, LivingEntity entity) {
        genericAttachCapability(event, new Barrier(entity), BARRIER_EFFECT, BARRIER_EEFFECT_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(BARRIER_EFFECT_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, BarrierEffect::attacher, BarrierEffect::get, true);
    }
}
