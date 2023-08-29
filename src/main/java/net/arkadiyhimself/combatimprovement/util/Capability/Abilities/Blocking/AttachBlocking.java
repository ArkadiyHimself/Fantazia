package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class AttachBlocking extends CapabilityAttacher {
    private static final Class<Blocking> BLOCKING_CLASS = Blocking.class;
    public static final Capability<Blocking> BLOCKING = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation BLOCKING_RL = new ResourceLocation(CombatImprovement.MODID, "blocking");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static Blocking getUnwrap(Player player) {
        return get(player).orElse(null);
    }
    public static LazyOptional<Blocking> get(Player player) {
        return player.getCapability(BLOCKING);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new Blocking(player), BLOCKING, BLOCKING_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(BLOCKING_CLASS);
        CapabilityAttacher.registerPlayerAttacher(AttachBlocking::attacher, AttachBlocking::get, true);
    }
}
