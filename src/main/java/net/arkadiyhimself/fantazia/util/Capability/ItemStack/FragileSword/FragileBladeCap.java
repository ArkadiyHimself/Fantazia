package net.arkadiyhimself.fantazia.util.Capability.ItemStack.FragileSword;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public class FragileBladeCap extends ItemStackCapability {
    public FragileBladeCap(ItemStack itemStack) { super(itemStack); }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", damage);
        tag.putInt("level", level);
        if (delay == 0) {
            tag.putInt("delay", delay);
        }

        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        damage = nbt.contains("damage") ? nbt.getFloat("damage") : MIN_DMG;
        delay = nbt.contains("delay") ? nbt.getInt("delay") : DELAY_AFTER_HIT;
        level = nbt.contains("level") ? nbt.getInt("level") : 0;
    }
    private final float MIN_DMG = 0;
    private final float MAX_DMG = 20;
    public final int DELAY_AFTER_HIT = 300;
    public final int DELAY_UNLEASH = 1200;
    public float damage = MIN_DMG;
    public int delay = 0;
    public int level = 0;
    public void setLevel() {
        level = switch (getDamageLevel()) {
            case STARTING -> 0;
            case LOW -> 1;
            case MEDIUM -> 2;
            case HIGH -> 3;
            case MAXIMUM -> 4;
        };
    }
    public void tick() {
        delay = Math.max(0, delay - 1);
        if (delay == 0) {
            reset();
        }
    }
    public void onAttack(boolean parry) {
        int bonus = parry ? 2 : 1;
        damage = Math.min(damage + bonus, MAX_DMG);
        if (damage >= MAX_DMG) {
            delay = DELAY_UNLEASH;
        } else {
            delay = DELAY_AFTER_HIT;
        }
        setLevel();
    }
    public void reset() {
        damage = MIN_DMG;
        setLevel();
    }
    public enum DAMAGE_LEVEL {
        STARTING(), LOW(), MEDIUM(), HIGH(), MAXIMUM()
    }
    public DAMAGE_LEVEL getDamageLevel() {
        float perc = bonusDMGpercent();
        if (perc <= 0) {
            return DAMAGE_LEVEL.STARTING;
        } else if (perc > 0 && perc < 0.34) {
            return DAMAGE_LEVEL.LOW;
        } else if (perc >= 0.34 && perc < 0.67) {
            return DAMAGE_LEVEL.MEDIUM;
        } else if (perc >= 0.67 && perc < 1) {
            return DAMAGE_LEVEL.HIGH;
        } else { return DAMAGE_LEVEL.MAXIMUM; }
    }
    private float bonusDMGpercent() {
        return damage / MAX_DMG;
    }
    public ChatFormatting[] getDamageFormatting() {
        return switch (getDamageLevel()) {
            case STARTING -> new ChatFormatting[] {ChatFormatting.GRAY};
            case LOW -> new ChatFormatting[] {ChatFormatting.BOLD};
            case MEDIUM -> new ChatFormatting[] {ChatFormatting.BOLD, ChatFormatting.RED};
            case HIGH -> new ChatFormatting[] {ChatFormatting.BOLD, ChatFormatting.DARK_RED};
            case MAXIMUM -> new ChatFormatting[] {ChatFormatting.BOLD, ChatFormatting.GOLD};
        };
    }
    public SoundEvent getHitSound() {
        return switch (getDamageLevel()) {
            case STARTING -> SoundRegistry.FRAG_SWORD_BEGIN.get();
            case LOW -> SoundRegistry.FRAG_SWORD_LOW.get();
            case MEDIUM -> SoundRegistry.FRAG_SWORD_MEDIUM.get();
            case HIGH -> SoundRegistry.FRAG_SWORD_HIGH.get();
            case MAXIMUM -> SoundRegistry.FRAG_SWORD_MAXIMUM.get();
        };
    }
    public float minDMG() {
        return MIN_DMG;
    }
    public float maxDMG() {
        return MAX_DMG;
    }
}
