package net.arkadiyhimself.fantazia.util.simpleobjects;

import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.sounds.EntityBoundSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;

public class EntityChasingSoundInstance extends EntityBoundSoundInstance {

    private final float range;

    public EntityChasingSoundInstance(SoundEvent soundEvent, SoundSource source, float volume, float pitch, Entity entity, long seed) {
        super(soundEvent, source, volume, pitch, entity, seed);
        this.range = soundEvent.getRange(volume);
    }

    @Override
    public float getVolume() {
        float initial = super.getVolume();

        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return initial;
        Vec3 pos = new Vec3(getX(), getY(), getZ());
        double dist = pos.distanceTo(player.position());

        return dist > range ? 0 : initial - initial * (float) dist / range;
    }
}
