package net.arkadiyhimself.fantazia.common.mob_effect;

import net.arkadiyhimself.fantazia.data.tags.FTZItemTags;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.entity.monster.Ghast;
import org.jetbrains.annotations.NotNull;

public class DisarmMobEffect extends SimpleMobEffect {

    public DisarmMobEffect() {
        super(MobEffectCategory.HARMFUL, 16447222, true, true);
    }

    @Override
    public void onEffectAdded(@NotNull LivingEntity livingEntity, int amplifier) {
        super.onEffectAdded(livingEntity, amplifier);
        if (livingEntity.getUseItem().is(FTZItemTags.DISABLED_BY_DISARM)) livingEntity.stopUsingItem();

        if (livingEntity instanceof Creeper creeper) creeper.setSwellDir(-1);
        if (livingEntity instanceof Ghast ghast) ghast.setCharging(false);
    }
}
