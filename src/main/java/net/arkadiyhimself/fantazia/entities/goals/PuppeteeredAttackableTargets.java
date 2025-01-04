package net.arkadiyhimself.fantazia.entities.goals;

import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.goal.target.NearestAttackableTargetGoal;
import net.minecraft.world.entity.monster.Monster;

public class PuppeteeredAttackableTargets<T extends Monster> extends NearestAttackableTargetGoal<T> {
    public PuppeteeredAttackableTargets(Mob mob, Class<T> targetType, boolean mustSee) {
        super(mob, targetType, mustSee);
    }

    @Override
    public boolean canUse() {
        return this.mob.hasEffect(FTZMobEffects.PUPPETEERED) && super.canUse();
    }
}
