package net.arkadiyhimself.fantazia.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.projectile.Arrow;
import net.minecraft.world.item.enchantment.EnchantedItemInUse;
import net.minecraft.world.item.enchantment.LevelBasedValue;
import net.minecraft.world.item.enchantment.effects.ApplyMobEffect;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Optional;

@Mixin(ApplyMobEffect.class)
public class MixinApplyMobEffect {

    @Shadow @Final private HolderSet<MobEffect> toApply;

    @Shadow @Final private LevelBasedValue minDuration;

    @Shadow @Final private LevelBasedValue maxDuration;

    @Shadow @Final private LevelBasedValue minAmplifier;

    @Shadow @Final private LevelBasedValue maxAmplifier;

    @Inject(at = @At("HEAD"), method = "apply")
    private void apply(ServerLevel level, int enchantmentLevel, EnchantedItemInUse item, Entity entity, Vec3 origin, CallbackInfo ci) {
        if (entity instanceof Arrow arrow) {
            RandomSource randomsource = arrow.getRandom();
            Optional<Holder<MobEffect>> optional = toApply.getRandomElement(randomsource);
            if (optional.isPresent()) {
                int i = Math.round(Mth.randomBetween(randomsource, minDuration.calculate(enchantmentLevel), maxDuration.calculate(enchantmentLevel)) * 20.0F);
                int j = Math.max(0, Math.round(Mth.randomBetween(randomsource, minAmplifier.calculate(enchantmentLevel), maxAmplifier.calculate(enchantmentLevel))));
                arrow.addEffect(new MobEffectInstance(optional.get(), i, j));
            }
        }
    }
}
