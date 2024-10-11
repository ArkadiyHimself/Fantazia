package net.arkadiyhimself.fantazia.api.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.registries.FTZDamageTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;

public record HiddenPotentialHolder(float damage, int delay) {
    public static final StreamCodec<ByteBuf, HiddenPotentialHolder> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG,
            HiddenPotentialHolder::serialize,
            HiddenPotentialHolder::deserialize
    );

    public static final Codec<HiddenPotentialHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damage").forGetter(HiddenPotentialHolder::damage),
            Codec.INT.fieldOf("delay").forGetter(HiddenPotentialHolder::delay)
    ).apply(instance, HiddenPotentialHolder::new));

    public static final HiddenPotentialHolder DEFAULT = new HiddenPotentialHolder();

    private static final float MIN = 0;
    private static final float MAX = 20;
    private static final int DELAY_REGULAR = 300;
    private static final int DELAY_UNLEASH = 1200;

    public HiddenPotentialHolder() {
        this(MIN, 0);
    }

    public HiddenPotentialHolder tick() {
        int newDelay = delay - 1;
        if (newDelay <= 0) return reset();
        else return new HiddenPotentialHolder(this.damage, newDelay);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", damage);
        if (delay == 0) tag.putInt("delay", delay);
        return tag;
    }
    public static HiddenPotentialHolder deserialize(CompoundTag tag) {
        return new HiddenPotentialHolder(tag.getFloat("damage"), tag.getInt("delay"));
    }

    public HiddenPotentialHolder onHit(LivingDamageEvent.Post event) {
        return event.getSource().is(FTZDamageTypes.REMOVAL) ? this : reset();
    }

    public DAMAGE damageLevel() {
        float percent = dmgPercent();
        if (percent <= 0) return DAMAGE.STARTING;
        else if (percent > 0 && percent < 0.34) return DAMAGE.LOW;
        else if (percent >= 0.34 && percent < 0.67) return DAMAGE.MEDIUM;
        else if (percent >= 0.67 && percent < 1) return DAMAGE.HIGH;
        else return DAMAGE.MAXIMUM;
    }

    private float dmgPercent() {
        return damage / MAX;
    }

    public HiddenPotentialHolder onAttack(boolean parry, LivingEntity victim) {


        int bonus = parry ? 2 : 1;
        DAMAGE old = damageLevel();

        float newDMG = Math.min(damage + bonus, MAX);

        HiddenPotentialHolder copy = new HiddenPotentialHolder(newDMG, newDMG >= MAX ? DELAY_UNLEASH : DELAY_REGULAR);

        DAMAGE cur = copy.damageLevel();
        if (old != DAMAGE.MAXIMUM && cur == DAMAGE.MAXIMUM) victim.playSound(FTZSoundEvents.FRAGILE_SWORD_UNLEASHED.get());
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
            case STARTING -> FTZSoundEvents.FRAGILE_SWORD_BEGIN.get();
            case LOW -> FTZSoundEvents.FRAGILE_SWORD_LOW.get();
            case MEDIUM -> FTZSoundEvents.FRAGILE_SWORD_MEDIUM.get();
            case HIGH -> FTZSoundEvents.FRAGILE_SWORD_HIGH.get();
            case MAXIMUM -> FTZSoundEvents.FRAGILE_SWORD_MAXIMUM.get();
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

    public enum DAMAGE {
        STARTING(0), LOW(1), MEDIUM(2), HIGH(3), MAXIMUM(4);
        final int level;
        DAMAGE(int level) {
            this.level = level;
        }
        public int getLevel() {
            return level;
        }
    }

    @Override
    public boolean equals(Object obj) {
        return false;
    }

    @Override
    public int hashCode() {
        return this.serialize().hashCode();
    }
}
