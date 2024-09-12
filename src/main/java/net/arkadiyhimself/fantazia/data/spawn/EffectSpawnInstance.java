package net.arkadiyhimself.fantazia.data.spawn;

import net.arkadiyhimself.fantazia.util.library.pseudorandom.PSERANInstance;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.NotNull;


public class EffectSpawnInstance {
    private final @NotNull MobEffect mobEffect;
    private final PSERANInstance pseranInstance;
    private final int level;
    private final boolean hidden;
    public EffectSpawnInstance(@NotNull MobEffect mobEffect, PSERANInstance pseranInstance, int level, boolean hidden) {
        this.mobEffect = mobEffect;
        this.pseranInstance = pseranInstance;
        this.level = level;
        this.hidden = hidden;
    }
    public void tryAddEffects(LivingEntity livingEntity) {
        if (!pseranInstance.performAttempt()) return;
        livingEntity.addEffect(new MobEffectInstance(mobEffect, -1, level, true, hidden, true));
    }
    public CompoundTag serialize() {
        CompoundTag tag = new CompoundTag();

        ResourceLocation effectID = ForgeRegistries.MOB_EFFECTS.getKey(mobEffect);
        if (effectID == null) throw new IllegalStateException("MobEffect's id has not been found");
        tag.putString("effect", effectID.toString());

        tag.put("random", pseranInstance.serialize());
        tag.putInt("level", level);
        tag.putBoolean("hidden", hidden);

        return tag;
    }
    public static EffectSpawnInstance deserialize(CompoundTag tag) {
        ResourceLocation effectID = new ResourceLocation(tag.getString("effect"));
        MobEffect mobEffect = ForgeRegistries.MOB_EFFECTS.getValue(effectID);
        if (mobEffect == null) throw new IllegalStateException("MobEffect has not been found: " + effectID);

        PSERANInstance pseranInstance = PSERANInstance.deserialize(tag.getCompound("random"));
        int level = tag.getInt("level");
        boolean hidden = tag.getBoolean("hidden");

        return new EffectSpawnInstance(mobEffect, pseranInstance, level, hidden);
    }
    public static class Builder {
        private final MobEffect mobEffect;
        private final double chance;
        private final int level;
        private final boolean hidden;
        public Builder(MobEffect mobEffect, double chance, int level, boolean hidden) {
            this.mobEffect = mobEffect;
            this.chance = chance;
            this.level = level;
            this.hidden = hidden;
        }
        public EffectSpawnInstance build() {
            return new EffectSpawnInstance(mobEffect, new PSERANInstance(chance), level, hidden);
        }
    }
}
