package net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect;

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

public class StunEffect extends CapabilityAttacher {
    private static final Class<Stun> STUN_EFFECT_CLASS = Stun.class;
    public static final Capability<Stun> STUN_EFFECT = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation STUN_EEFFECT_RL = new ResourceLocation(CombatImprovement.MODID, "stun_effect");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static Stun getUnwrap(LivingEntity entity) {
        return get(entity).orElse(null);
    }
    public static LazyOptional<Stun> get(LivingEntity entity) {
        return entity.getCapability(STUN_EFFECT);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, LivingEntity entity) {
        genericAttachCapability(event, new Stun(entity), STUN_EFFECT, STUN_EEFFECT_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(STUN_EFFECT_CLASS);
        CapabilityAttacher.registerEntityAttacher(LivingEntity.class, StunEffect::attacher, StunEffect::get, true);
    }
}
