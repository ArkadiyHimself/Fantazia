package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHolder;
import net.arkadiyhimself.fantazia.api.attachment.level.LevelAttributesHelper;
import net.arkadiyhimself.fantazia.api.attachment.level.holders.DamageSourcesHolder;
import net.arkadiyhimself.fantazia.api.custom_events.BlockingEvent;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.type.entity.ITalentListener;
import net.arkadiyhimself.fantazia.data.talent.types.BasicTalent;
import net.arkadiyhimself.fantazia.events.FTZHooks;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.networking.packets.stuff.SwingHandS2C;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.neoforged.neoforge.event.entity.living.LivingIncomingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

public class MeleeBlockHolder extends PlayerAbilityHolder implements ITalentListener, IDamageEventListener {
    private static final int BLOCK_ANIM = 20;
    private static final int BLOCK_WINDOW = 13;
    private static final int PARRY_WINDOW = 4;
    private static final int PARRY_DELAY = 8;
    private static final int BLOCK_TIME = 12;
    private static final int BLOCK_CD = 25;
    private LivingEntity lastAttacker = null;
    private boolean unlocked = false;
    private int anim;
    private int block_ticks;
    private int parry_ticks;
    private int parry_delay;
    private int blockedTime;
    private int block_cd;
    private boolean parried = false;
    private boolean expiring = false;
    private float dmgTaken;
    private float dmgParry;
    public MeleeBlockHolder(Player player) {
        super(player, Fantazia.res("melee_block"));
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("anim", this.anim);
        tag.putInt("block_cd", this.block_cd);
        tag.putInt("block_ticks", this.block_ticks);
        tag.putInt("parry_ticks", this.parry_ticks);
        tag.putBoolean("unlocked", this.unlocked);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        this.anim = compoundTag.contains("anim") ? compoundTag.getInt("anim") : 0;
        this.block_cd = compoundTag.contains("block_cd") ? compoundTag.getInt("block_cd") : 0;
        this.block_ticks = compoundTag.contains("block_ticks") ? compoundTag.getInt("block_ticks") : 0;
        this.parry_ticks = compoundTag.contains("parry_ticks") ? compoundTag.getInt("parry_ticks") : 0;
        this.unlocked = compoundTag.contains("unlocked") && compoundTag.getBoolean("unlocked");
    }

    @Override
    public void respawn() {
        anim = 0;
        block_ticks = 0;
        parry_ticks = 0;
        parry_delay = 0;
        blockedTime = 0;
        block_cd = 0;
        parried = false;
        expiring = false;
    }

    @Override
    public void tick() {
        if (!(getPlayer() instanceof ServerPlayer serverPlayer) || !(getPlayer().level() instanceof ServerLevel serverLevel)) return;
        anim = Math.max(0, anim - 1);
        block_ticks = Math.max(0, block_ticks - 1);
        parry_ticks = Math.max(0, parry_ticks - 1);
        parry_delay = Math.max(0, parry_delay - 1);
        blockedTime = Math.max(0, blockedTime - 1);
        block_cd = Math.max(0, block_cd - 1);

        if (parried && parry_delay == 0) {
            PacketDistributor.sendToPlayer(serverPlayer, new SwingHandS2C(InteractionHand.MAIN_HAND));
            Vec3 horLook = serverPlayer.getLookAngle().subtract(0, serverPlayer.getLookAngle().y(), 0);
            AABB aabb = serverPlayer.getBoundingBox().move(horLook.normalize().scale(2f)).inflate(1.5,0.25,1.5);

            DamageSourcesHolder sources = LevelAttributesHelper.getDamageSources(serverPlayer.level());

            if (sources != null) {
                for (LivingEntity livingentity : serverPlayer.level().getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> livingEntity.isAlive() && livingEntity != serverPlayer && livingEntity != lastAttacker)) {
                    float damageBonus = EnchantmentHelper.modifyDamage(serverLevel, serverPlayer.getMainHandItem(), livingentity, sources.parry(serverPlayer), dmgParry);
                    livingentity.hurt(sources.parry(serverPlayer), damageBonus);
                }
                AttributeInstance instance = serverPlayer.getAttribute(Attributes.ENTITY_INTERACTION_RANGE);
                double reach = instance == null ? 3 : instance.getValue();
                if (serverPlayer.distanceTo(lastAttacker) <= reach) {
                    float damageBonus = EnchantmentHelper.modifyDamage(serverLevel, serverPlayer.getMainHandItem(), lastAttacker, sources.parry(serverPlayer), dmgParry);
                    lastAttacker.hurt(sources.parry(serverPlayer), damageBonus);
                }
                if (Fantazia.DEVELOPER_MODE) serverPlayer.sendSystemMessage(Component.translatable(String.valueOf(dmgParry)));
            }

            parried = false;
            block_cd = 0;
        }
        if (expiring && block_ticks == 0) {
            FTZHooks.onBlockingExpired(serverPlayer, serverPlayer.getMainHandItem());
            expiring = false;
        }
    }

    @Override
    public void onHit(LivingIncomingDamageEvent event) {
        if (!(event.getEntity() instanceof Player player)) return;
        boolean facing = PlayerAbilityHelper.facesAttack(player, event.getSource());
        if (facing && (parry_delay > 0 || blockedTime > 0)) event.setCanceled(true);

        if (!event.getSource().is(DamageTypes.MOB_ATTACK) && !event.getSource().is(DamageTypes.PLAYER_ATTACK) || !facing || !(event.getSource().getEntity() instanceof LivingEntity attacker) || block_ticks <= 0) return;
        boolean cancel = true;
        lastAttacker = attacker;
        dmgTaken = event.getAmount();

        BlockingEvent.ParryDecision decision = FTZHooks.onParryDecision(player, player.getMainHandItem(), event.getAmount(), attacker);
        if (decision.getResult() == BlockingEvent.ParryDecision.Result.DO_PARRY || (parry_ticks > 0 && decision.getResult() != BlockingEvent.ParryDecision.Result.DO_NOT_PARRY)) {
            // parrying
            AttributeInstance attackDamage = player.getAttribute(Attributes.ATTACK_DAMAGE);
            float DMG = attackDamage == null ? 8f : (float) attackDamage.getValue() * 2;
            if (parryAttack(DMG)) player.getMainHandItem().hurtAndBreak(1, attacker, EquipmentSlot.MAINHAND);
            else cancel = false;
            // regular blocking
        } else if (blockAttack()) player.getMainHandItem().hurtAndBreak(2, attacker, EquipmentSlot.MAINHAND);
        else cancel = false;

        event.setCanceled(cancel);
    }
    @Override
    public void onTalentUnlock(BasicTalent talent) {
        ResourceLocation location = talent.getID();
        if (Fantazia.res("melee_block").equals(location)) unlocked = true;
    }
    @Override
    public void onTalentRevoke(BasicTalent talent) {
        ResourceLocation location = talent.getID();
        if (Fantazia.res("melee_block").equals(location)) unlocked = false;
    }

    public boolean isInAnim() {
        return (anim > 0 || parry_delay > 0 || blockedTime > 0);
    }
    public boolean parryAttack(float amount) {
        BlockingEvent.Parry parryEvent = FTZHooks.onParry(getPlayer(), getPlayer().getMainHandItem(), dmgTaken, lastAttacker, amount);
        if (parryEvent.isCanceled()) return false;

        dmgParry = parryEvent.getParryDamage();
        parry_delay = PARRY_DELAY;
        parried = true;
        block_cd = 0;
        block_ticks = 0;
        anim = 0;
        expiring = false;

        if (lastAttacker != null) LivingEffectHelper.makeDisarmed(lastAttacker, 160);

        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.COMBAT_MELEE_BLOCK.get(), SoundSource.PLAYERS);
        if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C("parry"));

        return true;
    }
    public boolean blockAttack() {
        if (!FTZHooks.onBlock(getPlayer(), getPlayer().getMainHandItem(), dmgTaken, lastAttacker)) return false;

        getPlayer().getMainHandItem().hurtAndBreak(2, getPlayer(), EquipmentSlot.MAINHAND);
        if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C("block"));

        blockedTime = BLOCK_TIME;
        block_ticks = 0;
        anim = 0;
        expiring = false;

        if (lastAttacker != null) LivingEffectHelper.microStun(lastAttacker);
        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.COMBAT_MELEE_BLOCK.get(), SoundSource.PLAYERS);

        return true;
    }
    public void startBlocking() {
        if (!unlocked || block_cd > 0 || !FTZHooks.onBlockingStart(getPlayer(), getPlayer().getMainHandItem())) return;
        block_cd = BLOCK_CD;
        block_ticks = BLOCK_WINDOW;
        parry_ticks = getPlayer().level() instanceof ServerLevel serverLevel ? getParryWindow(serverLevel) : PARRY_WINDOW;
        anim = BLOCK_ANIM;
        expiring = true;
        if (getPlayer() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C("blocking"));
    }
    public void interrupt() {
        anim = 0;
        block_ticks = 0;
        parry_ticks = 0;
        parry_delay = 0;
        blockedTime = 0;
        parried = false;
        expiring = false;
    }

    public int getParryWindow(ServerLevel serverLevel) {
        return 6 - serverLevel.getDifficulty().getId();
    }
}
