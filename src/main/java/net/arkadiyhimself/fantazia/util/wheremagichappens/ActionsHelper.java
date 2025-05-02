package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.MeleeBlockHolder;
import net.arkadiyhimself.fantazia.packets.stuff.InterruptPlayerS2C;
import net.arkadiyhimself.fantazia.registries.FTZAttachmentTypes;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.PacketDistributor;

public class ActionsHelper {

    public static boolean preventActions(Player player) {
        if (player == null) return false;

        MeleeBlockHolder meleeBlockHolder = PlayerAbilityHelper.takeHolder(player, MeleeBlockHolder.class);
        if (meleeBlockHolder != null && meleeBlockHolder.isInAnim()) return true;
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isDashing()) return true;
        if (player.getData(FTZAttachmentTypes.MURASAMA_TAUNT_TICKS).value() > 0) return true;

        StunEffectHolder stunEffect = LivingEffectHelper.takeHolder(player, StunEffectHolder.class);
        if (stunEffect != null && stunEffect.stunned()) return true;

        return false;
    }

    public static void interrupt(LivingEntity entity) {
        if (entity instanceof ServerPlayer player) {
            PacketDistributor.sendToPlayer(player, new InterruptPlayerS2C());
            player.stopUsingItem();
            player.stopSleeping();
            player.stopFallFlying();
            PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::stopDash);
            PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, MeleeBlockHolder::interrupt);
        } else if (entity instanceof Mob mob) {
            mob.setTarget(null);
            for (WrappedGoal goal : mob.goalSelector.getAvailableGoals()) goal.stop();
            for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) goal.stop();
        }
    }

    public static boolean cancelMouseMoving(LocalPlayer player) {
        if (player == null) return false;

        MeleeBlockHolder blocking = PlayerAbilityHelper.takeHolder(player, MeleeBlockHolder.class);
        DashHolder dashHolder = PlayerAbilityHelper.takeHolder(player, DashHolder.class);
        if (blocking != null && blocking.isInAnim()) return true;
        if (dashHolder != null && dashHolder.isDashing()) return true;
        if (player.getData(FTZAttachmentTypes.MURASAMA_TAUNT_TICKS).value() > 0) return true;

        StunEffectHolder stun = LivingEffectHelper.takeHolder(player, StunEffectHolder.class);
        if (stun != null && stun.stunned()) return true;

        return false;
    }

    public static boolean infiniteResources(LivingEntity livingEntity) {
        return livingEntity instanceof Player player && player.hasInfiniteMaterials();
    }
}
