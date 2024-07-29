package net.arkadiyhimself.fantazia.advanced.capability.entity.feature.features;

import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureHolder;
import net.arkadiyhimself.fantazia.util.interfaces.ITicking;
import net.minecraft.client.Minecraft;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.world.entity.projectile.AbstractArrow;

public class ArrowEnchant extends FeatureHolder implements ITicking {
    private static final String ID = "arrow_enchant:";
    private AbstractArrow arrow;
    private boolean frozen = false;
    private int duelist = 0;
    private int ballista = 0;
    public ArrowEnchant(AbstractArrow entity) {
        super(entity);
        this.arrow = entity;
    }
    @Override
    public AbstractArrow getEntity() {
        return arrow;
    }

    @Override
    public void tick() {
        if (arrow.isInPowderSnow) frozen = true;
        double X = arrow.position().x();
        double Y = arrow.position().y();
        double Z = arrow.position().z();
        if (frozen && Minecraft.getInstance().level != null) Minecraft.getInstance().level.addParticle(ParticleTypes.SNOWFLAKE, X, Y, Z,0,0,0);
    }
    public void freeze() {
        this.frozen = true;
    }
    public boolean isFrozen() {
        return frozen;
    }
    public void setDuelist(int value) {
        this.duelist = value;
    }

    public int getDuelist() {
        return duelist;
    }

    public void setBallista(int value) {
        this.ballista = value;
    }

    public int getBallista() {
        return ballista;
    }
}
