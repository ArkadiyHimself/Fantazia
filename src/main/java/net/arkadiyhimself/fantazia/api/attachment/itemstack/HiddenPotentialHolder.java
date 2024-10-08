package net.arkadiyhimself.fantazia.api.attachment.itemstack;

import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public class HiddenPotentialHolder {
    public static final StreamCodec<ByteBuf, HiddenPotentialHolder> CODEC = StreamCodec.composite(
            ByteBufCodecs.COMPOUND_TAG, HiddenPotentialHolder::serialize,
            (compoundTag) -> {
                HiddenPotentialHolder hiddenPotentialHolder = new HiddenPotentialHolder();
                hiddenPotentialHolder.deserialize(compoundTag);
                return hiddenPotentialHolder;
            }
    );
    public static final HiddenPotentialHolder DEFAULT = new HiddenPotentialHolder();

    private static final float MIN = 0;
    private static final float MAX = 20;
    private static final int DELAY_REGULAR = 300;
    private static final int DELAY_UNLEASH = 1200;
    private float damage = MIN;
    private int delay = 0;

    public HiddenPotentialHolder tick() {
        int newDelay = delay--;
        if (newDelay <= 0) return reset();
        else {
            HiddenPotentialHolder copy = new HiddenPotentialHolder();
            copy.delay = newDelay;
            copy.damage = this.damage;
            return copy;
        }
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", damage);
        if (delay == 0) tag.putInt("delay", delay);
        return tag;
    }
    public void deserialize(CompoundTag tag) {
        damage = tag.contains("damage") ? tag.getFloat("damage") : MIN;
        delay = tag.contains("delay") ? tag.getInt("delay") : DELAY_REGULAR;
    }

    public HiddenPotentialHolder onHit(LivingDamageEvent.Post event) {
        return event.getSource().is(FTZDamageTypes.REMOVAL) ? this : reset();
    }

    public DAMAGE_LEVEL damageLevel() {
        float percent = dmgPercent();
        if (percent <= 0) return DAMAGE_LEVEL.STARTING;
        else if (percent > 0 && percent < 0.34) return DAMAGE_LEVEL.LOW;
        else if (percent >= 0.34 && percent < 0.67) return DAMAGE_LEVEL.MEDIUM;
        else if (percent >= 0.67 && percent < 1) return DAMAGE_LEVEL.HIGH;
        else return DAMAGE_LEVEL.MAXIMUM;
    }

    private float dmgPercent() {
        return damage / MAX;
    }

    public HiddenPotentialHolder onAttack(boolean parry, LivingEntity victim) {
        HiddenPotentialHolder copy = new HiddenPotentialHolder();

        int bonus = parry ? 2 : 1;
        DAMAGE_LEVEL old = damageLevel();
        copy.damage = Math.min(damage + bonus, MAX);
        copy.delay = copy.damage >= MAX ? DELAY_UNLEASH : DELAY_REGULAR;
        DAMAGE_LEVEL cur = damageLevel();
        if (old != DAMAGE_LEVEL.MAXIMUM && cur == DAMAGE_LEVEL.MAXIMUM) victim.playSound(FTZSoundEvents.FRAG_SWORD_UNLEASHED.get());
        victim.playSound(getSound(), 0.35f,1f);

        return copy;
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
            case STARTING -> FTZSoundEvents.FRAG_SWORD_BEGIN.get();
            case LOW -> FTZSoundEvents.FRAG_SWORD_LOW.get();
            case MEDIUM -> FTZSoundEvents.FRAG_SWORD_MEDIUM.get();
            case HIGH -> FTZSoundEvents.FRAG_SWORD_HIGH.get();
            case MAXIMUM -> FTZSoundEvents.FRAG_SWORD_MAXIMUM.get();
        };
    }

    public HiddenPotentialHolder reset() {
        return new HiddenPotentialHolder();
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

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.serialize().hashCode();
    }
}
