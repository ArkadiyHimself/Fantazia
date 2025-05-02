package net.arkadiyhimself.fantazia.client.render;

import net.minecraft.world.phys.Vec3;

import java.util.function.BiFunction;
import java.util.function.BinaryOperator;

public enum ParticleMovement {
    REGULAR(new Vec3(0,0,0)),
    FALL(new Vec3(0,-0.15,0)),
    ASCEND(new Vec3(0,0.15,0)),
    CHASE_AND_FALL((pos, delta) -> new Vec3(delta.x() * 1.5, 0.15, delta.z() * 1.5)),
    AWAY((pos, delta) -> new Vec3(delta.x() * (-1.5), delta.y() * -0.2 - 0.1, delta.z() * (-1.5))),
    AWAY_AND_FALL((pos, delta) -> new Vec3(delta.x() *(-1.5), -0.15, delta.z() *(-1.5)));

    private final BiFunction<Vec3, Vec3, Vec3> modifier;

    ParticleMovement(BinaryOperator<Vec3> modifier) {
        this.modifier = modifier;
    }

    ParticleMovement(Vec3 vec3) {
        this.modifier = (pos, delta) -> vec3;
    }

    public Vec3 modify(Vec3 position, Vec3 delta) {
        return modifier.apply(position, delta);
    }
}
