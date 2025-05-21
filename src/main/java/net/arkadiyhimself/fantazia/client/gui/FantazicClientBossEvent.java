package net.arkadiyhimself.fantazia.client.gui;

import net.minecraft.Util;
import net.minecraft.client.gui.components.LerpingBossEvent;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;

import java.util.UUID;

@OnlyIn(Dist.CLIENT)
public class FantazicClientBossEvent extends LerpingBossEvent {

    private float barrier;
    private float targetBarrier;

    public FantazicClientBossEvent(UUID id, Component name, float progress, float barrier, BossBarColor color, BossBarOverlay overlay, boolean darkenScreen, boolean bossMusic, boolean worldFog) {
        super(id, name, progress, color, overlay, darkenScreen, bossMusic, worldFog);
        this.barrier = barrier;
        this.targetBarrier = barrier;
    }

    public float getBarrier() {
        long i = Util.getMillis() - this.setTime;
        float f = Mth.clamp((float) i / 100.0F, 0.0F, 1.0F);
        this.barrier = Mth.lerp(f, this.barrier, this.targetBarrier);
        return barrier;
    }

    public void setProgress(float progress, float barrier) {
        super.setProgress(progress);
        this.barrier = getBarrier();
        this.targetBarrier = barrier;
    }
}
