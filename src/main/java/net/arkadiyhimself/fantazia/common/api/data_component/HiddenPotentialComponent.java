package net.arkadiyhimself.fantazia.common.api.data_component;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;

public record HiddenPotentialComponent(float damage, int delay) {

    public static final StreamCodec<ByteBuf, HiddenPotentialComponent> STREAM_CODEC = StreamCodec.composite(ByteBufCodecs.COMPOUND_TAG,
            HiddenPotentialComponent::serialize,
            HiddenPotentialComponent::deserialize
    );

    public static final Codec<HiddenPotentialComponent> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.FLOAT.fieldOf("damage").forGetter(HiddenPotentialComponent::damage),
            Codec.INT.fieldOf("delay").forGetter(HiddenPotentialComponent::delay)
    ).apply(instance, HiddenPotentialComponent::new));

    public static final HiddenPotentialComponent DEFAULT = new HiddenPotentialComponent();

    private static final float MIN = 0;
    private static final float MAX = 20;
    private static final int DELAY_REGULAR = 300;
    private static final int DELAY_UNLEASH = 1200;

    public HiddenPotentialComponent() {
        this(MIN, 0);
    }

    public HiddenPotentialComponent tick() {
        int newDelay = delay - 1;
        if (newDelay <= 0) return DEFAULT;
        else return new HiddenPotentialComponent(this.damage, newDelay);
    }

    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();
        tag.putFloat("damage", damage);
        if (delay == 0) tag.putInt("delay", delay);
        return tag;
    }
    public static HiddenPotentialComponent deserialize(CompoundTag tag) {
        return new HiddenPotentialComponent(tag.getFloat("damage"), tag.getInt("delay"));
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

    public HiddenPotentialComponent onAttack(boolean double_increase, LivingEntity victim) {
        int bonus = double_increase ? 2 : 1;
        DAMAGE old = damageLevel();

        float newDMG = Math.min(damage + bonus, MAX);

        HiddenPotentialComponent copy = new HiddenPotentialComponent(newDMG, newDMG >= MAX ? DELAY_UNLEASH : DELAY_REGULAR);

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

    public static HiddenPotentialComponent reset() {
        return new HiddenPotentialComponent();
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
        return obj instanceof HiddenPotentialComponent holder && holder.damage == this.damage;
    }

    @Override
    public int hashCode() {
        return this.serialize().hashCode();
    }

    public static void playerTookDamage(ServerPlayer player) {
        Inventory inventory = player.getInventory();
        for (int i = 0; i <= inventory.getContainerSize(); i++) {
            ItemStack stack = inventory.getItem(i);
            if (stack.has(FTZDataComponentTypes.HIDDEN_POTENTIAL))
                stack.set(FTZDataComponentTypes.HIDDEN_POTENTIAL, HiddenPotentialComponent.DEFAULT);

        }
    }
}
