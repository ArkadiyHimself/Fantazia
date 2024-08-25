package net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.ITalentListener;
import net.arkadiyhimself.fantazia.api.capability.ITicking;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityHolder;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapHelper;
import net.arkadiyhimself.fantazia.api.fantazicevents.BlockingEvent;
import net.arkadiyhimself.fantazia.data.talents.BasicTalent;
import net.arkadiyhimself.fantazia.events.FTZEvents;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.networking.packets.SwingHandS2C;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
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
import net.minecraftforge.common.ForgeMod;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.eventbus.api.Event;

public class MeleeBlock extends AbilityHolder implements ITicking, ITalentListener, IDamageReacting {
    private static final int BLOCK_ANIM = 20;
    private static final int BLOCK_WINDOW = 13;
    private static final int PARRY_WINDOW = 3;
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
    public MeleeBlock(Player player) {
        super(player);
    }
    @Override
    public String ID() {
        return "melee_block";
    }
    @Override
    public void tick() {
        if (!(getPlayer() instanceof ServerPlayer serverPlayer)) return;
        anim = Math.max(0, anim - 1);
        block_ticks = Math.max(0, block_ticks - 1);
        parry_ticks = Math.max(0, parry_ticks - 1);
        parry_delay = Math.max(0, parry_delay - 1);
        blockedTime = Math.max(0, blockedTime - 1);
        block_cd = Math.max(0, block_cd - 1);
        if (parried && parry_delay == 0) {
            NetworkHandler.sendToPlayer(new SwingHandS2C(InteractionHand.MAIN_HAND), serverPlayer);
            Vec3 horLook = serverPlayer.getLookAngle().subtract(0, serverPlayer.getLookAngle().y(), 0);
            AABB aabb = serverPlayer.getBoundingBox().move(horLook.normalize().scale(2f)).inflate(1.5,0.25,1.5);

            FTZDamageTypes.DamageSources sources = LevelCapHelper.getDamageSources(serverPlayer.level());

            if (sources != null) {
                for (LivingEntity livingentity : serverPlayer.level().getEntitiesOfClass(LivingEntity.class, aabb, livingEntity -> livingEntity.isAlive() && livingEntity != serverPlayer && livingEntity != lastAttacker)) {
                    float damageBonus = EnchantmentHelper.getDamageBonus(getPlayer().getMainHandItem(), livingentity.getMobType());
                    livingentity.hurt(sources.parry(getPlayer()), dmgParry + damageBonus);
                }
                AttributeInstance instance = getPlayer().getAttribute(ForgeMod.ENTITY_REACH.get());
                double reach = instance == null ? 3 : instance.getValue();
                if (getPlayer().distanceTo(lastAttacker) <= reach) {
                    float damageBonus = EnchantmentHelper.getDamageBonus(getPlayer().getMainHandItem(), lastAttacker.getMobType());
                    lastAttacker.hurt(sources.parry(getPlayer()), dmgParry + damageBonus);
                }
                if (Fantazia.DEVELOPER_MODE) getPlayer().sendSystemMessage(Component.translatable(String.valueOf(dmgParry)));
            }

            parried = false;
            block_cd = 0;
        }
        if (expiring && block_ticks == 0) {
            FTZEvents.onBlockingExpired(getPlayer(), getPlayer().getMainHandItem());
            expiring = false;
        }
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
    public CompoundTag serialize(boolean toDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putInt("anim", this.anim);
        tag.putInt("block_cd", this.block_cd);
        tag.putInt("block_ticks", this.block_ticks);
        tag.putInt("parry_ticks", this.parry_ticks);
        tag.putBoolean("unlocked", this.unlocked);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag, boolean fromDisk) {
        this.anim = tag.contains("anim") ? tag.getInt("anim") : 0;
        this.block_cd = tag.contains("block_cd") ? tag.getInt("block_cd") : 0;
        this.block_ticks = tag.contains("block_ticks") ? tag.getInt("block_ticks") : 0;
        this.parry_ticks = tag.contains("parry_ticks") ? tag.getInt("parry_ticks") : 0;
        this.unlocked = tag.contains("unlocked") && tag.getBoolean("unlocked");
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
    @Override
    public void onHit(LivingAttackEvent event) {
        boolean facing = AbilityHelper.facesAttack(getPlayer(), event.getSource());
        if (facing && (parry_delay > 0 || blockedTime > 0)) event.setCanceled(true);

        if (!event.getSource().is(DamageTypes.MOB_ATTACK) && !event.getSource().is(DamageTypes.PLAYER_ATTACK) || !facing || !(event.getSource().getEntity() instanceof LivingEntity attacker) || block_ticks <= 0) return;
        boolean cancel = true;
        lastAttacker = attacker;
        dmgTaken = event.getAmount();

        BlockingEvent.ParryDecision decision = FTZEvents.onParryDecision(getPlayer(), getPlayer().getMainHandItem(), event.getAmount(), attacker);
        if (decision.getResult() == Event.Result.ALLOW || parry_ticks > 0) {
            // parrying
            AttributeInstance attackDamage = getPlayer().getAttribute(Attributes.ATTACK_DAMAGE);
            float DMG = attackDamage == null ? 8f : (float) attackDamage.getValue() * 2;
            if (parryAttack(DMG)) getPlayer().getMainHandItem().hurtAndBreak(1, attacker, (living -> living.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
            else cancel = false;
        // regular blocking
        } else if (blockAttack()) getPlayer().getMainHandItem().hurtAndBreak(2, attacker, (living -> living.broadcastBreakEvent(EquipmentSlot.MAINHAND)));
        else cancel = false;

        event.setCanceled(cancel);
    }
    public boolean isInAnim() {
        return (anim > 0 || parry_delay > 0 || blockedTime > 0);
    }
    public boolean parryAttack(float amount) {
        BlockingEvent.Parry parryEvent = FTZEvents.onParry(getPlayer(), getPlayer().getMainHandItem(), dmgTaken, lastAttacker, amount);
        if (parryEvent.isCanceled()) return false;

        dmgParry = parryEvent.getParryDamage();
        parry_delay = PARRY_DELAY;
        parried = true;
        block_cd = 0;
        block_ticks = 0;
        anim = 0;
        expiring = false;

        if (lastAttacker != null) EffectHelper.makeDisarmed(lastAttacker, 160);

        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.BLOCKED.get(), SoundSource.PLAYERS);
        NetworkHandler.sendToPlayer(new PlayAnimationS2C("parry"), getPlayer());

        return true;
    }
    public boolean blockAttack() {
        if (!FTZEvents.onBlock(getPlayer(), getPlayer().getMainHandItem(), dmgTaken, lastAttacker)) return false;

        getPlayer().getMainHandItem().hurtAndBreak(2, getPlayer(), (durability) -> durability.broadcastBreakEvent(EquipmentSlot.MAINHAND));
        NetworkHandler.sendToPlayer(new PlayAnimationS2C("block"), getPlayer());

        blockedTime = BLOCK_TIME;
        block_ticks = 0;
        anim = 0;
        expiring = false;

        if (lastAttacker != null) EffectHelper.microStun(lastAttacker);
        getPlayer().level().playSound(null, getPlayer().blockPosition(), FTZSoundEvents.BLOCKED.get(), SoundSource.PLAYERS);

        return true;
    }
    public void startBlocking() {
        if (!unlocked || block_cd > 0 || !FTZEvents.onBlockingStart(getPlayer(), getPlayer().getMainHandItem())) return;
        block_cd = BLOCK_CD;
        block_ticks = BLOCK_WINDOW;
        parry_ticks = PARRY_WINDOW;
        anim = BLOCK_ANIM;
        expiring = true;
        NetworkHandler.sendToPlayer(new PlayAnimationS2C("blocking"), getPlayer());
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
}
