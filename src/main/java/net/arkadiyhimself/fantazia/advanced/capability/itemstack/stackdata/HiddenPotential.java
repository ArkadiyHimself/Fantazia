package net.arkadiyhimself.fantazia.advanced.capability.itemstack.stackdata;

import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataHolder;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.interfaces.IDamageReacting;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class HiddenPotential extends StackDataHolder implements ITicking, IDamageReacting {
    private static final String ID = "hidden_potential:";
    public enum DAMAGE_LEVEL {
        STARTING(0), LOW(1), MEDIUM(2), HIGH(3), MAXIMUM(4);
        final int level;
        DAMAGE_LEVEL(int level) {
            this.level = level;
        }
        public int getLevel() {
            return level;
        }
    }
    private static final float MIN = 0;
    private static final float MAX = 20;
    private static final int DELAY_REGULAR = 300;
    private final int DELAY_UNLEASH = 1200;
    private float damage = MIN;
    private int delay = 0;
    public HiddenPotential(ItemStack stack) {
        super(stack);
    }

    @Override
    public void tick() {
        if (delay > 0) delay--;
        else reset();
    }
    @Override
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat(ID + "damage", damage);
        if (delay == 0) tag.putInt(ID + "delay", delay);
        return tag;
    }

    @Override
    public void deserialize(CompoundTag tag) {
        super.deserialize(tag);
        damage = tag.contains(ID + "damage") ? tag.getFloat(ID + "damage") : MIN;
        delay = tag.contains(ID + "delay") ? tag.getInt(ID + "delay") : DELAY_REGULAR;
    }

    @Override
    public void onHit(LivingHurtEvent event) {
        reset();
    }
    public DAMAGE_LEVEL damageLevel() {
        float perc = dmgPercent();
        if (perc <= 0) return DAMAGE_LEVEL.STARTING;
        else if (perc > 0 && perc < 0.34) return DAMAGE_LEVEL.LOW;
        else if (perc >= 0.34 && perc < 0.67) return DAMAGE_LEVEL.MEDIUM;
        else if (perc >= 0.67 && perc < 1) return DAMAGE_LEVEL.HIGH;
        else return DAMAGE_LEVEL.MAXIMUM;
    }
    private float dmgPercent() {
        return damage / MAX;
    }
    public float onAttack(boolean parry, LivingEntity victim) {
        int bonus = parry ? 2 : 1;
        DAMAGE_LEVEL old = damageLevel();
        damage = Math.min(damage + bonus, MAX);
        delay = damage >= MAX ? DELAY_UNLEASH : DELAY_REGULAR;
        DAMAGE_LEVEL cur = damageLevel();
        if (old != HiddenPotential.DAMAGE_LEVEL.MAXIMUM && cur == HiddenPotential.DAMAGE_LEVEL.MAXIMUM) victim.playSound(FTZSoundEvents.FRAG_SWORD_UNLEASHED);
        victim.playSound(getSound());

        return damage;
    }
    public ChatFormatting[] getFormatting() {
        return switch (damageLevel()) {
            case STARTING -> new ChatFormatting[] {ChatFormatting.GRAY};
            case LOW -> new ChatFormatting[]{};
            case MEDIUM -> new ChatFormatting[] {ChatFormatting.RED};
            case HIGH -> new ChatFormatting[] {ChatFormatting.DARK_RED};
            case MAXIMUM -> new ChatFormatting[] {ChatFormatting.GOLD};
        };
    }
    public SoundEvent getSound() {
        return switch (damageLevel()) {
            case STARTING -> FTZSoundEvents.FRAG_SWORD_BEGIN;
            case LOW -> FTZSoundEvents.FRAG_SWORD_LOW;
            case MEDIUM -> FTZSoundEvents.FRAG_SWORD_MEDIUM;
            case HIGH -> FTZSoundEvents.FRAG_SWORD_HIGH;
            case MAXIMUM -> FTZSoundEvents.FRAG_SWORD_MAXIMUM;
        };
    }
    public void reset() {
        damage = MIN;
    }
    public float minDMG() {
        return MIN;
    }
    public float maxDMG() {
        return MAX;
    }
    public float getDamage() {
        return damage;
    }
}
