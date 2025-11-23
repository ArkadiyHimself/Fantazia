package net.arkadiyhimself.fantazia.util.wheremagichappens;

import net.arkadiyhimself.fantazia.client.ClientEvents;
import net.arkadiyhimself.fantazia.client.screen.TalentScreen;
import net.arkadiyhimself.fantazia.common.advanced.spell.IChanneled;
import net.arkadiyhimself.fantazia.common.api.FTZKeyMappings;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.StunEffectHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.DoubleJumpHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.MeleeBlockHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.common.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.common.item.casters.SpellCasterItem;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.networking.stuff.KeyInputC2S;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.Brain;
import net.minecraft.world.entity.ai.goal.WrappedGoal;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.item.Item;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Optional;

public class ActionsHelper {

    public static boolean spellCast1 = false;
    public static boolean spellCast2 = false;
    public static boolean spellCast3 = false;
    public static boolean jumpPressed = false;

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
            IPacket.interruptPlayer(player);
            player.stopUsingItem();
            player.stopSleeping();
            player.stopFallFlying();
            PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::stopDash);
            PlayerAbilityHelper.acceptConsumer(player, MeleeBlockHolder.class, MeleeBlockHolder::interrupt);
        } else if (entity instanceof Mob mob) {
            mob.setTarget(null);
            for (WrappedGoal goal : mob.goalSelector.getAvailableGoals()) goal.stop();
            for (WrappedGoal goal : mob.targetSelector.getAvailableGoals()) goal.stop();
            Brain<?> brain = mob.getBrain();
            brain.eraseMemory(MemoryModuleType.ATTACK_TARGET);
            brain.setActiveActivityIfPossible(Activity.IDLE);

            if (entity instanceof Creeper creeper) creeper.setSwellDir(-3);
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

    public static boolean doSpellInputTick(int index) {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return false;
        Optional<SlotResult> result = FantazicUtil.findCurio(player, FTZSlots.ACTIVECASTER, index);
        if (result.isEmpty()) return false;
        Item item = result.get().stack().getItem();

        if (!(item instanceof SpellCasterItem spellCasterItem) || !(spellCasterItem.getSpell().value() instanceof IChanneled spell)) return true;
        if (PlayerAbilityHelper.onRecharge(spellCasterItem.getSpell(), player) || !PlayerAbilityHelper.enoughMana(player, spellCasterItem.getSpell().value().manacost())) return false;

        int castTime = spell.castTime();
        ClientEvents.castCurioIndex = index;
        ClientEvents.requiredCast = castTime;
        if (++ClientEvents.currentCast >= castTime) {
            ClientEvents.currentCast = 0;
            return true;
        } else return false;
    }

    public static void handleKeyInputsOnTick() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (FTZKeyMappings.DASH.isDown()) PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::maybeCancelDash);

        if (player == null || ActionsHelper.preventActions(player) || player.isSpectator()) return;

        if (FTZKeyMappings.SWORD_ABILITY.consumeClick()) IPacket.keyInput(KeyInputC2S.INPUT.WEAPON_ABILITY, 1);
        if (FTZKeyMappings.SPELLCAST1.isDown()) {
            spellCast1 = true;
            if (ActionsHelper.doSpellInputTick(0))
                IPacket.keyInput(KeyInputC2S.INPUT.SPELLCAST1, 1);
        } else {
            if (spellCast1) {
                spellCast1 = false;
                ClientEvents.currentCast = 0;
                ClientEvents.castCurioIndex = -1;
            }
        }

        if (FTZKeyMappings.SPELLCAST2.isDown()) {
            spellCast2 = true;
            if (ActionsHelper.doSpellInputTick(1))
                IPacket.keyInput(KeyInputC2S.INPUT.SPELLCAST2, 1);
        } else {
            if (spellCast2) {
                spellCast2 = false;
                ClientEvents.currentCast = 0;
                ClientEvents.castCurioIndex = -1;
            }
        }

        if (FTZKeyMappings.SPELLCAST3.isDown()) {
            spellCast3 = true;
            if (ActionsHelper.doSpellInputTick(2))
                IPacket.keyInput(KeyInputC2S.INPUT.SPELLCAST3, 1);
        } else {
            if (spellCast3) {
                spellCast3 = false;
                ClientEvents.currentCast = 0;
                ClientEvents.castCurioIndex = -1;
            }
        }

        TalentsHolder talentsHolder = PlayerAbilityHelper.takeHolder(player, TalentsHolder.class);
        if (FTZKeyMappings.TALENTS.isDown() && talentsHolder != null) Minecraft.getInstance().setScreen(new TalentScreen(talentsHolder));

        if (Minecraft.getInstance().options.keyJump.isDown()) {
            jumpPressed = true;
            PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::tryToJumpClient);
        } else {
            if (jumpPressed) {
                PlayerAbilityHelper.acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::buttonRelease);
                jumpPressed = false;
            }
        }

        if (FTZKeyMappings.DASH.isDown()) PlayerAbilityHelper.acceptConsumer(player, DashHolder.class, DashHolder::beginDash);
    }
}
