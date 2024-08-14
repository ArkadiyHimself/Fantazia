package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHelper;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlayAnimationS2C;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicCombat;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class StunEffect extends EffectHolder implements IDamageReacting {
    private static final int DURATION = 80;
    private static final int DELAY = 40;
    private int points = 0;
    private int delay = 0;
    private int color = 0;
    private boolean shift = false;
    @SuppressWarnings("ConstantConditions")
    public StunEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.STUN);
    }
    public boolean stunned() {
        return getDur() > 0;
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
    @SuppressWarnings("ConstantConditions")
    public int getMaxPoints() {
        return (int) this.getOwner().getAttributeValue(FTZAttributes.MAX_STUN_POINTS);
    }
    @SuppressWarnings("ConstantConditions")
    private void attackStunned(int dur) {
        points = 0;
        delay = 0;
        EffectHelper.makeStunned(getOwner(), dur);
    }
    public int getColor() {
        return color;
    }

    private void colorTick() {
        if (shift) color += 15;
        else  color -= 15;
        if (color <= 160)  shift = true;
        if (color >= 255)  shift = false;
    }
    @Override
    public void onHit(LivingHurtEvent event) {
        if (FantazicCombat.blocksDamage(getOwner()) || event.isCanceled() || event.getAmount() <= 0 || event.getEntity().hurtTime > 0 || stunned()) return;
        DamageSource source = event.getSource();
        float amount = event.getAmount();
        int premature = (int) ((float) points / getMaxPoints() * DURATION);

        if (source.is(DamageTypes.MOB_ATTACK) || source.is(DamageTypes.PLAYER_ATTACK)) {
            delay = DELAY;
            int newPoints = (int) (amount / getOwner().getMaxHealth() * getMaxPoints()) * 2;
            int minPoints = (int) (getMaxPoints() * 0.05f);
            newPoints = Math.max(minPoints, newPoints);
            points += ((int) (Math.min(getMaxPoints() * 0.7f, newPoints)));
            if (points >= getMaxPoints()) {
                attackStunned(DURATION);
                getOwner().playSound(FTZSoundEvents.ATTACK_STUNNED);
            }
        } else if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            int dur = (int) Math.max(premature, amount * 5);
            int blastProtect = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, getOwner());
            if (blastProtect > 0) dur /= blastProtect;
            attackStunned(dur);
        } else if (source.is(DamageTypeTags.IS_FALL)) {
            int dur = (int) Math.max(premature, amount * 5);
            int fallProtect = getOwner().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(Enchantments.FALL_PROTECTION);
            if (fallProtect > 0) dur /= fallProtect;
            attackStunned(dur);
        }
    }
    @Override
    public void tick() {
        super.tick();
        if (delay > 0) delay--;
        else if (points > 0) points--;
        colorTick();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = super.serialize();
        tag.putInt(ID + "points", points);
        tag.putInt(ID + "color", color);
        return tag;
    }
    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        points = tag.contains(ID + "points") ? tag.getInt(ID + "points") : 0;
        color = tag.contains(ID + "color") ? tag.getInt(ID + "color") : 0;
    }
    @Override
    public void respawn() {
        super.respawn();
        points = 0;
        delay = 0;
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlayAnimationS2C("stunned"), serverPlayer);
    }
    @Override
    public void ended() {
        super.ended();
        if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlayAnimationS2C(""), serverPlayer);
    }
}
