package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.ISyncEveryTick;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.ComplexLivingEffectHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.EuphoriaHolder;
import net.arkadiyhimself.fantazia.packets.IPacket;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ApplyEffect;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.util.Mth;
import net.minecraft.world.Difficulty;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

public class StunEffectHolder extends ComplexLivingEffectHolder implements IDamageEventListener, ISyncEveryTick {

    private static final int DURATION = 80;
    private static final int DELAY = 40;
    private int points = 0;
    private int delay = 0;

    public StunEffectHolder(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("stun_effect"), FTZMobEffects.STUN);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("points", points);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        points = compoundTag.getInt("points");
    }

    @Override
    public CompoundTag serializeTick() {
        CompoundTag tag = new CompoundTag();
        tag.putInt("points", points);
        tag.putInt("duration", duration);
        tag.putInt("initialDuration", initialDur);
        return tag;
    }

    @Override
    public void deserializeTick(CompoundTag tag) {
        this.points = tag.getInt("points");
        if (tag.contains("duration")) this.duration = tag.getInt("duration");
        this.initialDur = tag.getInt("initialDuration");
    }

    @Override
    public void serverTick() {
        super.serverTick();

        boolean furious = getEntity().hasEffect(FTZMobEffects.FURY);

        delay = Math.max(0, delay - (furious ? 2 : 1));
        if (delay == 0) points = Math.max(0, points - (furious ? 2 : 1));
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (instance.getEffect().value() != FTZMobEffects.STUN.value()) return;
        if (getEntity() instanceof ServerPlayer serverPlayer) {
            PlayerAbilityHelper.acceptConsumer(serverPlayer, EuphoriaHolder.class, EuphoriaHolder::reset);
            IPacket.animatePlayer(serverPlayer,"stunned");
        }
    }

    @Override
    public void ended(MobEffect mobEffect) {
        super.ended(mobEffect);
        if (mobEffect != FTZMobEffects.STUN.value()) return;
        if (getEntity() instanceof ServerPlayer serverPlayer) IPacket.animatePlayer(serverPlayer,"");;
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        if (FantazicCombat.blocksDamage(getEntity()) || event.getNewDamage() <= 0 || stunned() || !(getEntity().level() instanceof ServerLevel serverLevel)) return;
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();

        if (!source.is(FTZDamageTypes.REMOVAL) && !source.is(FTZDamageTypes.BLEEDING)) delay = Math.max(delay, 16);

        if (serverLevel.getGameRules().getBoolean(FTZGameRules.STUN_FROM_ATTACKS) && meleeHit(source, amount)) return;

        int premature = (int) ((float) points / getMaxPoints() * DURATION);

        Registry<Enchantment> enchantmentRegistry = getEntity().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        if (source.is(DamageTypeTags.IS_EXPLOSION) && serverLevel.getGameRules().getBoolean(FTZGameRules.STUN_FROM_EXPLOSION) && amount > 2f) {
            int dur = (int) Math.max(premature, amount * 5);

            Optional<Holder.Reference<Enchantment>> blast = enchantmentRegistry.getHolder(Enchantments.BLAST_PROTECTION);
            int blastProtect = blast.map(enchantmentReference -> EnchantmentHelper.getEnchantmentLevel(enchantmentReference, getEntity())).orElse(0);
            if (blastProtect > 0) dur /= blastProtect;
            attackStunned(dur);
        } else if (source.is(DamageTypeTags.IS_FALL) && serverLevel.getGameRules().getBoolean(FTZGameRules.STUN_FROM_FALLING)) {
            if (amount > 5f) {
                int dur = (int) Math.max(premature, amount * 16);

                Optional<Holder.Reference<Enchantment>> blast = enchantmentRegistry.getHolder(Enchantments.FEATHER_FALLING);
                int fallProtect = blast.map(enchantmentReference -> getEntity().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(enchantmentReference)).orElse(0);
                if (fallProtect > 0) dur /= fallProtect;
                attackStunned(dur);
            } else {
                int newPoints = (int) ((float) getMaxPoints() * (amount / 5f));
                this.points += newPoints;
                if (points >= getMaxPoints()) attackStunned(DURATION);
            }
        }
    }

    // returns false if damage source was not a melee attack
    private boolean meleeHit(DamageSource source, float amount) {
        boolean attack = source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK);
        boolean parry = source.is(FTZDamageTypes.PARRY);

        if (!attack && !parry) return false;

        int basicPoints;
        int finalPoints;

        double maxPoint = getMaxPoints();

        if (attack) {
            delay = Math.max(delay, DELAY);
            basicPoints = (int) (amount * 25);
            if (basicPoints > 0.8 * maxPoint) {
                // let's have next arguments: x - basic points, max - maxPoint, fin - finalPoints;
                // this is a function where:
                // for x <= (0.8 * max) : fin = x;
                // fir x > (0.8 * max) : fin = 0.8 * max + (x - 0.8 * max) * ln(1 + (x - 0.8 * max) * 2 / max)
                // the graph of such function starts going up linearly, but upon reaching 0.8 of max points, it 'turns' into a ln function
                // initial basic points would have to be more than roughly 120% of max points for the function to reach max points
                // so, if there are 300 points, the basic points will have to be about 360, and the damage will have to be about 14.5
                int basePart = (int) ((float) basicPoints * 0.8);
                int remaining = basicPoints - basePart;
                remaining = (int) (remaining * Math.log(1 + remaining * 2 / maxPoint));
                finalPoints = basePart + remaining;
            } else finalPoints = basicPoints;
        } else {
            delay = Math.max(delay, DELAY * 2);
            basicPoints = (int) (amount * 15.75);
            finalPoints = (int) Math.max(basicPoints, 0.25 * maxPoint);
        }
        if (getEntity().level() instanceof ServerLevel serverLevel) finalPoints = (int) ((float) finalPoints * pointMultiplier(serverLevel));
        if (getEntity().getData(FTZAttachmentTypes.DASHSTONE_MINION)) finalPoints = (int) (0.4f * (float) finalPoints);

        this.points += finalPoints;
        if (points >= maxPoint) {
            attackStunned(DURATION);
            getEntity().playSound(FTZSoundEvents.COMBAT_ATTACK_STUNNED.get());
        }

        return true;
    }

    public boolean stunned() {
        return duration() > 0;
    }

    public int getPoints() {
        return points;
    }

    public boolean hasPoints() {
        return getPoints() > 0;
    }

    public boolean renderBar() {
        return stunned() || hasPoints();
    }

    public int getMaxPoints() {
        return (int) getEntity().getAttributeValue(FTZAttributes.MAX_STUN_POINTS);
    }

    private void attackStunned(int dur) {
        points = 0;
        delay = 0;
        ApplyEffect.makeStunned(getEntity(), dur);
    }

    private float pointMultiplier(ServerLevel serverLevel) {
        Difficulty difficulty = serverLevel.getDifficulty();
        return 0.3f + (float) difficulty.getId() * 0.3f;
    }

    public void healPoints(int amount, boolean ignoreDelay) {
        if (!ignoreDelay && delay > 0 || duration > 0) return;
        this.points = Math.max(0, points - amount);
    }
}
