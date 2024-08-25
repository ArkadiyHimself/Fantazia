package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ClientValues;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.MeleeBlock;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.KickOutOfGuiS2C;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;

public class ActionsHelper {
    public static boolean preventActions(Player player) {
        if (player == null) return false;

        MeleeBlock meleeBlock = AbilityGetter.takeAbilityHolder(player, MeleeBlock.class);
        if (meleeBlock != null && meleeBlock.isInAnim()) return true;
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        if (dash != null && dash.isDashing()) return true;
        ClientValues clientValues = AbilityGetter.takeAbilityHolder(player, ClientValues.class);
        if (clientValues != null && clientValues.isTaunting()) return true;

        StunEffect stunEffect = EffectGetter.takeEffectHolder(player, StunEffect.class);
        if (stunEffect != null && stunEffect.stunned()) return true;

        return false;
    }

    public static void interrupt(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            NetworkHandler.sendToPlayer(new KickOutOfGuiS2C(), player);
            player.stopUsingItem();
            player.stopSleeping();
            player.stopFallFlying();
            AbilityGetter.abilityConsumer(player, Dash.class, Dash::stopDash);
            AbilityGetter.abilityConsumer(player, MeleeBlock.class, MeleeBlock::interrupt);
        } else if (entity instanceof Mob mob) mob.setTarget(null);
    }

    public static boolean cancelMouseMoving(LocalPlayer player) {
        if (player == null) return false;

        MeleeBlock blocking = AbilityGetter.takeAbilityHolder(player, MeleeBlock.class);
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        ClientValues clientValues = AbilityGetter.takeAbilityHolder(player, ClientValues.class);
        if (blocking != null && blocking.isInAnim()) return true;
        if (dash != null && dash.isDashing()) return true;
        if (clientValues != null && clientValues.isTaunting()) return true;

        StunEffect stun = EffectGetter.takeEffectHolder(player, StunEffect.class);
        if (stun != null && stun.stunned()) return true;

        return false;
    }
}
