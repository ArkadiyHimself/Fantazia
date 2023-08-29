package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash;

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

public class AttachDash extends CapabilityAttacher {
    private static final Class<Dash> DASH_CLASS = Dash.class;
    public static final Capability<Dash> DASH = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation DASH_RL = new ResourceLocation(CombatImprovement.MODID, "dash");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static Dash getUnwrap(Player player) {
        return get(player).orElse(null);
    }
    public static LazyOptional<Dash> get(Player player) {
        return player.getCapability(DASH);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new Dash(player), DASH, DASH_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(DASH_CLASS);
        CapabilityAttacher.registerPlayerAttacher(AttachDash::attacher, AttachDash::get, true);
    }
}
