package net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.api.AttributeRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.util.Interfaces.IDamageReacting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class StunEffect extends EffectHolder implements IDamageReacting {
    private static final int DURATION = 80;
    private static final int DELAY = 40;
    private int points = 0;
    private int delay = 0;
    private int color = 0;
    private boolean shift = false;
    public StunEffect(LivingEntity owner) {
        super(owner, MobEffectRegistry.STUN.get());
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
        return (int) this.getOwner().getAttributeValue(AttributeRegistry.MAX_STUN_POINTS.get());
    }
    private void attackStunned(int dur) {
        points = 0;
        delay = 0;
        getOwner().addEffect(new MobEffectInstance(MobEffectRegistry.STUN.get(), dur, 0, false, false, true));
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
    public void onHit(LivingAttackEvent event) {
        if (WhereMagicHappens.Abilities.blocksDamage(getOwner()) || event.isCanceled() || event.getAmount() <= 0 || event.getEntity().hurtTime > 0 || stunned()) return;
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
                getOwner().playSound(SoundRegistry.ATTACK_STUNNED.get());
            }
        } else if (source.is(DamageTypeTags.IS_EXPLOSION)) {
            int dur = (int) Math.max(premature, amount * 5 / Math.max(1, EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, getOwner())));
            attackStunned(dur);
        } else if (source.is(DamageTypeTags.IS_FALL)) {
            int dur = (int) Math.max(premature, amount * 5 / Math.max(1, getOwner().getItemBySlot(EquipmentSlot.FEET).getEnchantmentLevel(Enchantments.FALL_PROTECTION)));
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
