package net.arkadiyhimself.fantazia.particless;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.particle.*;
import net.minecraft.core.particles.SimpleParticleType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;

public class SoulParticle extends RisingParticle {
    private static final ArrayList<SimpleParticleType> DOOMED_SOULS = new ArrayList<>(){{
        add(FTZParticleTypes.DOOMED_SOUL1);
        add(FTZParticleTypes.DOOMED_SOUL2);
        add(FTZParticleTypes.DOOMED_SOUL3);
    }};
    public static SimpleParticleType randomSoulParticle() {
        int i = Fantazia.RANDOM.nextInt(0, DOOMED_SOULS.size());
        return DOOMED_SOULS.get(i);
    }
    private final SpriteSet spriteSet;
    @Nullable
    public LivingEntity owner;
    private float xOff;
    private float yOff;
    private float zOff;
    public SoulParticle(ClientLevel pLevel, double pX, double pY, double pZ, double pXSpeed, double pYSpeed, double pZSpeed, SpriteSet spriteSet)  {
        super(pLevel, pX, pY, pZ, pXSpeed, pYSpeed, pZSpeed);

        this.friction = 0.8F;
        this.xd = pXSpeed;
        this.yd = pYSpeed;
        this.zd = pZSpeed;
        this.quadSize *= 1.1;
        this.lifetime = 15;
        this.spriteSet = spriteSet;

        this.setSpriteFromAge(spriteSet);

        this.rCol = 1F;
        this.bCol = 1F;
        this.gCol = 1F;
    }
    public SoulParticle(ClientLevel pLevel, LivingEntity owner, SpriteSet spriteSet) {
        super(pLevel, owner.getX(), owner.getY(), owner.getZ(), 0, 0, 0);
        this.spriteSet = spriteSet;
        this.owner = owner;
    }

    @Override
    public void tick() {
        this.setSpriteFromAge(this.spriteSet);
        this.fadeOut();
        if (this.owner != null) {
            if (this.age++ >= this.lifetime) {
                this.remove();
            }
            Vec3 pos = this.owner.getPosition(0f).add(this.xOff, this.yOff, this.zOff);
            this.setPos(pos.x(), pos.y(), pos.z());
        } else {
            super.tick();
        }
    }

    public void fadeOut() {
        this.alpha = (-(0.5F/(float)lifetime) * age + 1);
    }

    @Override
    public ParticleRenderType getRenderType() {
        return ParticleRenderType.PARTICLE_SHEET_TRANSLUCENT;
    }

    @OnlyIn(Dist.CLIENT)
        public record Provider(SpriteSet spriteSet) implements ParticleProvider<SimpleParticleType> {
        public Particle createParticle(SimpleParticleType particleType, ClientLevel level, double x, double y, double z, double dx, double dy, double dz) {
                return new SoulParticle(level, x, y, z, dx, dy, dz, this.spriteSet);
            }
        }
}
