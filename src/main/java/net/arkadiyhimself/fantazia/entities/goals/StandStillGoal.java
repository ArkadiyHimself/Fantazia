package net.arkadiyhimself.fantazia.entities.goals;

import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;

import java.util.EnumSet;

public class StandStillGoal extends Goal {
    protected final PathfinderMob mob;
    public StandStillGoal(PathfinderMob pMob) {
        this.mob = pMob;
        this.setFlags(EnumSet.of(Flag.MOVE));
    }
    @Override
    public boolean canUse() {
        return this.isBleeding();
    }
    public boolean isBleeding() {
        return mob.hasEffect(FTZMobEffects.HAEMORRHAGE);
    }
    @Override
    public void start() {
        this.mob.getNavigation().stop();
    }
}
