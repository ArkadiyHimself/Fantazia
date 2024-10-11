package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
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
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.UnknownNullability;

import java.util.Optional;

public class StunEffect extends LivingEffectHolder implements IDamageEventListener {

    private static final int DURATION = 80;
    private static final int DELAY = 40;
    private int points = 0;
    private int delay = 0;
    private int color = 0;
    private boolean shift = false;

    public StunEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("stun_effect"), FTZMobEffects.STUN);
    }

    @Override
    public @UnknownNullability CompoundTag serializeNBT(HolderLookup.@NotNull Provider provider) {
        CompoundTag tag = super.serializeNBT(provider);
        tag.putInt("points", points);
        tag.putInt("color", color);
        return tag;
    }

    @Override
    public void deserializeNBT(HolderLookup.@NotNull Provider provider, @NotNull CompoundTag compoundTag) {
        super.deserializeNBT(provider, compoundTag);
        points = compoundTag.getInt("points");
        color = compoundTag.getInt("color");
    }

    @Override
    public void tick() {
        super.tick();
        colorTick();

        boolean furious = getEntity().hasEffect(FTZMobEffects.FURY);

        delay = Math.max(0, delay - (furious ? 2 : 1));
        if (delay == 0) points = Math.max(0, points - (furious ? 2 : 1));
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C("stunned"));
    }
    @Override
    public void ended() {
        super.ended();
        if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlayAnimationS2C(""));
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        if (FantazicCombat.blocksDamage(getEntity()) || event.getNewDamage() <= 0 || event.getEntity().hurtTime > 0 || stunned()) return;
        DamageSource source = event.getSource();
        float amount = event.getNewDamage();

        if (meleeHit(source, amount)) return;

        int premature = (int) ((float) points / getMaxPoints() * DURATION);

        Registry<Enchantment> enchantmentRegistry = getEntity().registryAccess().registryOrThrow(Registries.ENCHANTMENT);

        if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            int dur = (int) Math.max(premature, amount * 5);

            Optional<Holder.Reference<Enchantment>> blast = enchantmentRegistry.getHolder(Enchantments.BLAST_PROTECTION);
            int blastProtect = blast.map(enchantmentReference -> EnchantmentHelper.getEnchantmentLevel(enchantmentReference, getEntity())).orElse(0);
            if (blastProtect > 0) dur /= blastProtect;
            attackStunned(dur);
        } else if (source.is(DamageTypeTags.IS_FALL)) {
            int dur = (int) Math.max(premature, amount * 5);

            Optional<Holder.Reference<Enchantment>> blast = enchantmentRegistry.getHolder(Enchantments.FEATHER_FALLING);
            int fallProtect = blast.map(enchantmentReference -> getEntity().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(enchantmentReference)).orElse(0);
            if (fallProtect > 0) dur /= fallProtect;
            attackStunned(dur);
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
            finalPoints = (int) Mth.clamp(basicPoints,maxPoint * 0.025f, maxPoint * 0.85f);
        } else {
            delay = Math.max(delay, DELAY * 2);
            basicPoints = (int) (amount * 15.75);
            finalPoints = (int) Math.max(basicPoints, maxPoint * 0.25f);
        }
        if (getEntity().level() instanceof ServerLevel serverLevel) finalPoints = (int) ((float) finalPoints * pointMultiplier(serverLevel));

        points += finalPoints;
        if (points < getMaxPoints()) return true;

        attackStunned(DURATION);
        getEntity().playSound(FTZSoundEvents.COMBAT_ATTACK_STUNNED.get());

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
        LivingEffectHelper.makeStunned(getEntity(), dur);
    }

    public int getColor() {
        return color;
    }

    private void colorTick() {
        if (shift) color += 15;
        else color -= 15;
        if (color <= 160)  shift = true;
        if (color >= 255)  shift = false;
    }

    private float pointMultiplier(ServerLevel serverLevel) {
        Difficulty difficulty = serverLevel.getDifficulty();
        return 0.3f + (float) difficulty.getId() * 0.3f;
    }
}
