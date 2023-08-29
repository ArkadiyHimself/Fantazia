package net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword;

import dev._100media.capabilitysyncer.core.ItemStackCapability;
import net.arkadiyhimself.combatimprovement.Registries.Sounds.SoundRegistry;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ItemStack;

public class FragileBladeCap extends ItemStackCapability {
    public FragileBladeCap(ItemStack itemStack) { super(itemStack); }

    @Override
    public CompoundTag serializeNBT(boolean savingToDisk) {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", this.damage);
        if (this.delay == 0) {
            tag.putInt("delay", this.delay);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt, boolean readingFromDisk) {
        this.damage = nbt.contains("damage") ? nbt.getFloat("damage") : MIN_DMG;
        this.delay = nbt.contains("delay") ? nbt.getInt("delay") : DELAY_AFTER_HIT;
    }
    public final float MIN_DMG = 0;
    public final float MAX_DMG = 16;
    public final int DELAY_AFTER_HIT = 300;
    public float damage = MIN_DMG;
    public int delay = 0;
    public void tick() {
        delay = Math.max(0, delay - 1);
        if (delay == 0) {
            reset();
        }
    }
    public void onAttack() {
        delay = DELAY_AFTER_HIT;
        damage = Math.min(damage + 1, MAX_DMG);
    }
    public void reset() {
        damage = MIN_DMG;
    }
    public enum DAMAGE_LEVEL {
        STARTING(), LOW(), MEDIUM(), HIGH(), MAXIMUM()
    }
    public DAMAGE_LEVEL getDamageLevel() {
        if (damage <= MIN_DMG) {
            return DAMAGE_LEVEL.STARTING;
        } else if (damage > MIN_DMG && damage < 8) {
            return DAMAGE_LEVEL.LOW;
        } else if (damage >= 8 && damage < 12) {
            return DAMAGE_LEVEL.MEDIUM;
        } else if (damage >= 12 && damage < MAX_DMG) {
            return DAMAGE_LEVEL.HIGH;
        } else { return DAMAGE_LEVEL.MAXIMUM; }
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
        return switch (this.getDamageLevel()) {
            case STARTING -> SoundRegistry.FRAG_SWORD_BEGIN.get();
            case LOW -> SoundRegistry.FRAG_SWORD_LOW.get();
            case MEDIUM -> SoundRegistry.FRAG_SWORD_MEDIUM.get();
            case HIGH -> SoundRegistry.FRAG_SWORD_HIGH.get();
            case MAXIMUM -> SoundRegistry.FRAG_SWORD_MAXIMUM.get();
        };
    }
}
