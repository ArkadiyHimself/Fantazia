package net.arkadiyhimself.fantazia.registries;

import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.food.FoodProperties;

public interface FTZFoods {
    FoodProperties ARACHNID_EYE = new FoodProperties.Builder().effect(() -> new MobEffectInstance(MobEffects.CONFUSION, 100), 1f).effect(() -> new MobEffectInstance(MobEffects.MOVEMENT_SLOWDOWN, 100, 3), 1f).effect(() -> new MobEffectInstance(FTZMobEffects.DISARM, 100), 1f).alwaysEdible().nutrition(3).saturationModifier(2).build();
    FoodProperties VITALITY_FRUIT = new FoodProperties.Builder().effect(() -> new MobEffectInstance(MobEffects.REGENERATION, 100, 2), 1f).effect(() -> new MobEffectInstance(MobEffects.SATURATION, 100, 2), 1f).nutrition(5).saturationModifier(5f).alwaysEdible().build();
}
