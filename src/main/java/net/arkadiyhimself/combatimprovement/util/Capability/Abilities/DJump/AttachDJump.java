package net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump;

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

public class AttachDJump extends CapabilityAttacher {
    private static final Class<DJump> DJUMP_CLASS = DJump.class;
    public static final Capability<DJump> DJUMP = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation DJUMP_RL = new ResourceLocation(CombatImprovement.MODID, "djump");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static DJump getUnwrap(Player player) {
        return get(player).orElse(null);
    }
    public static LazyOptional<DJump> get(Player player) {
        return player.getCapability(DJUMP);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new DJump(player), DJUMP, DJUMP_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(DJUMP_CLASS);
        CapabilityAttacher.registerPlayerAttacher(AttachDJump::attacher, AttachDJump::get, true);
    }
}
