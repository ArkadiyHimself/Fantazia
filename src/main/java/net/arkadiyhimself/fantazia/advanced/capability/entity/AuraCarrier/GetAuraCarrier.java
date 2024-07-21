package net.arkadiyhimself.fantazia.advanced.capability.entity.AuraCarrier;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.decoration.ArmorStand;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class GetAuraCarrier extends CapabilityAttacher {
    private static final Class<AuraCarrier> AURA_CARRIER_CLASS = AuraCarrier.class;
    public static final Capability<AuraCarrier> AURA_CARRIER = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation AURA_CARRIER_RL = Fantazia.res("aura_carrier");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static AuraCarrier getUnwrap(ArmorStand armorStand) {
        return get(armorStand).orElse(null);
    }
    public static LazyOptional<AuraCarrier> get(ArmorStand armorStand) {
        return armorStand.getCapability(AURA_CARRIER);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, ArmorStand armorStand) {
        genericAttachCapability(event, new AuraCarrier(armorStand), AURA_CARRIER, AURA_CARRIER_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(AURA_CARRIER_CLASS);
        CapabilityAttacher.registerEntityAttacher(ArmorStand.class, GetAuraCarrier::attacher, GetAuraCarrier::get, true);
    }
}
