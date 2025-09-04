package net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.common.advanced.spell.SpellInstance;
import net.arkadiyhimself.fantazia.common.advanced.spell.types.AbstractSpell;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.*;
import net.arkadiyhimself.fantazia.common.FantazicHooks;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.custom.Spells;
import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.BlockTags;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.ClipBlockStateContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;
import java.util.function.Consumer;

public class PlayerAbilityHelper {

    private PlayerAbilityHelper() {}

    public static <T extends IPlayerAbility> @Nullable T takeHolder(@Nullable Player player, Class<T> tClass) {
        return player == null ? null : player.getData(FTZAttachmentTypes.ABILITY_MANAGER).actualHolder(tClass);
    }

    public static <T extends IPlayerAbility> void acceptConsumer(@Nullable Player player, Class<T> tClass, Consumer<T> consumer) {
        if (player == null) return;
        player.getData(FTZAttachmentTypes.ABILITY_MANAGER).optionalHolder(tClass).ifPresent(consumer);
    }

    public static Vec3 calculateViewVector(float pXRot, float pYRot) {
        float f = pXRot * ((float)Math.PI / 180F);
        float f1 = -pYRot * ((float)Math.PI / 180F);
        float f2 = Mth.cos(f1);
        float f3 = Mth.sin(f1);
        float f4 = Mth.cos(f);
        float f5 = Mth.sin(f);
        return new Vec3(f3 * f4, -f5, f2 * f4);
    }

    public static Vec3 dashDeltaMovement(Vec3 lookAngle, double speed, boolean hor) {
        Vec3 velocity = lookAngle.normalize().scale(speed);
        if (velocity.y() == 0 && hor) velocity = velocity.add(0,0.001,0);
        return velocity;
    }

    public static Vec3 dashDeltaMovement(LivingEntity entity, double velocity, boolean horizontal) {
        return dashDeltaMovement(calculateViewVector(horizontal ? 0 : entity.getXRot(), entity.getYRot()), velocity, horizontal);
    }

    public static boolean doubleJump(Player player) {
        StaminaHolder staminaHolder = PlayerAbilityHelper.takeHolder(player, StaminaHolder.class);
        if (staminaHolder != null && !staminaHolder.wasteStamina(1.75f, true)) return false;

        if (FantazicHooks.onDoubleJump(player)) return false;

        jumpInAir(player);
        return true;
    }

    public static void jumpInAir(LivingEntity livingEntity) {
        Vec3 vec3 = livingEntity.getDeltaMovement();
        AttributeInstance jump = livingEntity.getAttribute(Attributes.JUMP_STRENGTH);
        double strength = jump == null ? 0 : jump.getValue();
        livingEntity.setDeltaMovement(vec3.x, 0.175 + livingEntity.getJumpBoostPower() + strength * 1.1, vec3.z);
        livingEntity.fallDistance = -2f;
        livingEntity.hurtMarked = true;
    }

    public static boolean accelerateFlying(Player player) {
        StaminaHolder staminaHolder = PlayerAbilityHelper.takeHolder(player, StaminaHolder.class);
        if (staminaHolder != null && !staminaHolder.wasteStamina(3f, true)) return false;

        if (FantazicHooks.onDoubleJump(player)) return false;

        Vec3 vec31 = player.getLookAngle();
        Vec3 vec32 = player.getDeltaMovement();

        player.setDeltaMovement(vec32.add(vec31.x * 0.1D + (vec31.x * 2.5D - vec32.x) * 0.5D, vec31.y * 0.1D + (vec31.y * 2.5D - vec32.y) * 0.5D, vec31.z * 0.1D + (vec31.z * 2.5D - vec32.z) * 0.5D));
        player.hurtMarked = true;
        return true;
    }

    public static boolean facesAttack(LivingEntity blocker, Vec3 position) {
        Vec3 vec3 = blocker.getViewVector(1.0F);
        Vec3 vec31 = position.vectorTo(blocker.position()).normalize();
        vec31 = new Vec3(vec31.x, 0.0D, vec31.z);
        return vec31.dot(vec3) < 0.0D;
    }

    public static boolean isDashing(Player player) {
        DashHolder holder = takeHolder(player, DashHolder.class);
        return holder != null && holder.isDashing();
    }

    public static boolean shouldListen(ServerLevel pLevel, BlockPos pPos, GameEvent.Context pContext, LivingEntity entity) {
        if (pContext.sourceEntity() != null && pContext.sourceEntity().isCrouching() || pContext.sourceEntity() == entity) return false;
        return !entity.isDeadOrDying() && pLevel.getWorldBorder().isWithinBounds(pPos) && !entity.hasEffect(FTZMobEffects.DEAFENED);
    }

    public static boolean isOccluded(Level pLevel, Vec3 pFrom, Vec3 pTo) {
        Vec3 vec3 = new Vec3(Mth.floor(pFrom.x) + 0.5D, Mth.floor(pFrom.y) + 0.5D,  Mth.floor(pFrom.z) + 0.5D);
        Vec3 vec31 = new Vec3(Mth.floor(pTo.x) + 0.5D,  Mth.floor(pTo.y) + 0.5D,  Mth.floor(pTo.z) + 0.5D);

        for (Direction direction : Direction.values()) {
            Vec3 vec32 = vec3.relative(direction, 1.0E-5F);
            if (pLevel.isBlockInLine(new ClipBlockStateContext(vec32, vec31, blockState -> blockState.is(BlockTags.OCCLUDES_VIBRATION_SIGNALS))).getType() != HitResult.Type.BLOCK) return false;
        }
        return true;
    }

    public static void tryListen(ServerLevel pLevel, GameEvent.Context pContext, Vec3 pPos, ServerPlayer player) {
        if (pContext.sourceEntity() == null || !(pContext.sourceEntity() instanceof LivingEntity livingEntity)) return;
        Vec3 vec3 = player.getPosition(1f);
        if (PlayerAbilityHelper.shouldListen(pLevel, BlockPos.containing(pPos), pContext, player) && !PlayerAbilityHelper.isOccluded(pLevel, pPos, vec3)) {
            VibrationListenerHolder vibrationListenerHolder = PlayerAbilityHelper.takeHolder(player, VibrationListenerHolder.class);
            if (vibrationListenerHolder == null || !vibrationListenerHolder.listen()) return;
            vibrationListenerHolder.madeSound(livingEntity);
            reduceRecharge(player, Spells.SONIC_BOOM,25);
        }
    }

    public boolean tryMeleeBlock(Player player) {
        MeleeBlockHolder holder = takeHolder(player, MeleeBlockHolder.class);
        if (holder == null) return false;

        return holder.blockAttack();
    }

    public static void awardWisdom(ServerPlayer player, ResourceLocation category, ResourceLocation instance) {
        TalentsHolder progressHolder = takeHolder(player, TalentsHolder.class);
        if (progressHolder == null) return;
        progressHolder.tryAwardWisdom(category, instance);
    }

    public static void awardWisdom(ServerPlayer player, int amount) {
        TalentsHolder progressHolder = takeHolder(player, TalentsHolder.class);
        if (progressHolder == null) return;
        progressHolder.grantWisdom(amount);
    }

    public static void wasteMana(Player player, float amount) {
        acceptConsumer(player, ManaHolder.class, manaHolder -> manaHolder.wasteMana(amount));
    }

    public static boolean enoughMana(Player player, float amount) {
        ManaHolder manaHolder = takeHolder(player, ManaHolder.class);
        return manaHolder != null && manaHolder.getMana() >= amount;
    }

    public static @Nullable SpellInstance spellInstance(Player player, Holder<AbstractSpell> holder) {
        SpellInstancesHolder spellInstancesHolder = takeHolder(player, SpellInstancesHolder.class);
        return spellInstancesHolder == null ? null : spellInstancesHolder.getOrCreate(holder);
    }

    public static void spellInstance(Player player, Holder<AbstractSpell> holder, Consumer<SpellInstance> consumer) {
        acceptConsumer(player, SpellInstancesHolder.class, spellInstancesHolder -> consumer.accept(spellInstancesHolder.getOrCreate(holder)));
    }

    public static void reduceRecharge(Player player, Holder<AbstractSpell> holder, int value) {
        spellInstance(player, holder, spellInstance -> spellInstance.reduceRecharge(value));
    }

    public static boolean onRecharge(Holder<AbstractSpell> spell, Player player) {
        SpellInstance instance = spellInstance(player, spell);
        return instance != null && instance.recharge() > 0;
    }

    public static void pogo(Player player) {
        acceptConsumer(player, DoubleJumpHolder.class, DoubleJumpHolder::pogo);
        acceptConsumer(player, DashHolder.class, DashHolder::pogo);
        if (player instanceof ServerPlayer serverPlayer) IPacket.pogoPlayer(serverPlayer);
    }

    public static boolean canDoMeleeBlock(Player player) {
        MeleeBlockHolder holder = takeHolder(player, MeleeBlockHolder.class);
        if (holder == null) return false;
        return holder.isUnlocked() && player.getMainHandItem().is(FTZItemTags.MELEE_BLOCK);
    }
}
