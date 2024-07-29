package net.arkadiyhimself.fantazia.advanced.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.registries.FTZAttributes;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.interfaces.IDamageReacting;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.arkadiyhimself.fantazia.util.wheremagichappens.CombatHelper;
import net.minecraft.nbt.CompoundTag;
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
    public int getMaxPoints() {
        return (int) this.getOwner().getAttributeValue(FTZAttributes.MAX_STUN_POINTS);
    }
    private void attackStunned(int dur) {
        points = 0;
        delay = 0;
        getOwner().addEffect(new MobEffectInstance(FTZMobEffects.STUN, dur, 0, false, false, true));
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
        if (CombatHelper.blocksDamage(getOwner()) || event.isCanceled() || event.getAmount() <= 0 || event.getEntity().hurtTime > 0 || stunned()) return;
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
            int blastProt = EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, getOwner());
            if (blastProt > 0) dur /= blastProt;
            attackStunned(dur);
        } else if (source.is(DamageTypeTags.IS_FALL)) {
            int dur = (int) Math.max(premature, amount * 5);
            int fallProt = getOwner().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(Enchantments.FALL_PROTECTION);
            if (fallProt > 0) dur /= fallProt;
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
    public void added(MobEffectInstance instance) {
        super.added(instance);
        ActionsHelper.interrupt(getOwner());
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
}
