package net.arkadiyhimself.fantazia.entities.goals;

import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.minecraft.world.entity.PathfinderMob;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.phys.Vec3;

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
    @SuppressWarnings("ConstantConditions")
    public boolean isBleeding() {
        return mob.hasEffect(FTZMobEffects.HAEMORRHAGE.get());
    }
    @Override
    public void start() {
        Vec3 pos = mob.position();
        this.mob.getMoveControl().setWantedPosition(pos.x(), pos.y(), pos.z(), 0);
    }
}
