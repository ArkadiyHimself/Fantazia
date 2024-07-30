package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class AbilityGetter extends CapabilityAttacher {
    private static final Class<AbilityManager> ABILITY_CLASS = AbilityManager.class;
    public static final Capability<AbilityManager> ABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation ABILITY_RL = Fantazia.res("ability");
    @SuppressWarnings("ConstantConditions")
    @Nullable
    public static AbilityManager getUnwrap(LivingEntity entity) {
        if (!(entity instanceof Player)) return null;
        if (entity == null) return null;
        return get(entity).orElse(null);
    }
    public static LazyOptional<AbilityManager> get(LivingEntity entity) {
        return entity.getCapability(ABILITY);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new AbilityManager(player), ABILITY, ABILITY_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(ABILITY_CLASS);
        CapabilityAttacher.registerEntityAttacher(Player.class, AbilityGetter::attacher, AbilityGetter::get, true);
    }
}
