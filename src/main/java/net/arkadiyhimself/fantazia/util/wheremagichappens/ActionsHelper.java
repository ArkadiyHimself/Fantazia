package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.abilities.MeleeBlock;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectManager;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.KickOutOfGuiS2CPacket;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.warden.Warden;
import net.minecraft.world.entity.player.Player;

public class ActionsHelper {
    public static boolean preventActions(Player player) {
        if (player == null) return false;

        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager != null) {
            MeleeBlock meleeBlock = abilityManager.takeAbility(MeleeBlock.class);
            if (meleeBlock != null && meleeBlock.isInAnim()) return true;
            Dash dash = abilityManager.takeAbility(Dash.class);
            if (dash != null && dash.isDashing()) return true;
        }

        EffectManager effectManager = EffectGetter.getUnwrap(player);
        if (effectManager != null) {
            StunEffect stunEffect = effectManager.takeEffect(StunEffect.class);
            if (stunEffect != null && stunEffect.stunned()) return true;
        }

        return false;
    }

    public static void interrupt(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            NetworkHandler.sendToPlayer(new KickOutOfGuiS2CPacket(), player);
            player.stopUsingItem();
            player.stopSleeping();
            player.stopFallFlying();
        } else if (entity instanceof Mob mob) {
            mob.setTarget(null);
            if (mob instanceof Warden warden) {
                warden.getBrain().eraseMemory(MemoryModuleType.ATTACK_TARGET);
                warden.getBrain().eraseMemory(MemoryModuleType.LOOK_TARGET);
                warden.getBrain().clearMemories();
            }
        }
    }

    public static boolean cancelMouseMoving(LocalPlayer player) {
        if (player == null) return false;

        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager != null) {
            MeleeBlock blocking = abilityManager.takeAbility(MeleeBlock.class);
            Dash dash = abilityManager.takeAbility(Dash.class);
            if (blocking != null && blocking.isInAnim()) return true;
            if (dash != null && dash.isDashing()) return true;

        }

        EffectManager effectManager = EffectGetter.getUnwrap(player);
        if (effectManager != null) {
            StunEffect stun = effectManager.takeEffect(StunEffect.class);
            if (stun != null && stun.stunned()) return true;
        }

        return false;
    }
}
