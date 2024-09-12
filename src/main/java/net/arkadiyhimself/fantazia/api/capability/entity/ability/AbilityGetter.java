package net.arkadiyhimself.fantazia.api.capability.entity.ability;

import dev._100media.capabilitysyncer.core.CapabilityAttacher;
import net.arkadiyhimself.fantazia.Fantazia;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.common.util.NonNullConsumer;
import net.minecraftforge.event.AttachCapabilitiesEvent;

import javax.annotation.Nullable;

public class AbilityGetter extends CapabilityAttacher {
    public static <T extends AbilityHolder> @Nullable T takeAbilityHolder(Player player, Class<T> tClass) {
        AbilityManager abilityManager = getUnwrap(player);
        if (abilityManager == null) return null;
        return abilityManager.takeAbility(tClass);
    }
    public static <T extends AbilityHolder> void abilityConsumer(Player player, Class<T> tClass, NonNullConsumer<T> consumer) {
        AbilityManager abilityManager = getUnwrap(player);
        if (abilityManager == null) return;
        abilityManager.getAbility(tClass).ifPresent(consumer);
    }
    private static final Class<AbilityManager> ABILITY_CLASS = AbilityManager.class;
    public static final Capability<AbilityManager> ABILITY = getCapability(new CapabilityToken<>() {});
    public static final ResourceLocation ABILITY_RL = Fantazia.res("ability");
    @Nullable
    @SuppressWarnings("ConstantConditions")
    public static AbilityManager getUnwrap(Player player) {
        return get(player).orElse(null);
    }
    public static LazyOptional<AbilityManager> get(Player player) {
        return player.getCapability(ABILITY);
    }
    private static void attacher(AttachCapabilitiesEvent<Entity> event, Player player) {
        genericAttachCapability(event, new AbilityManager(player), ABILITY, ABILITY_RL);
    }
    public static void register() {
        CapabilityAttacher.registerCapability(ABILITY_CLASS);
        CapabilityAttacher.registerEntityAttacher(Player.class, AbilityGetter::attacher, AbilityGetter::get, true);
    }
}
