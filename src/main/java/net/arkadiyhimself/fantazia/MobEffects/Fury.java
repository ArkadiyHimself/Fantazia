package net.arkadiyhimself.fantazia.MobEffects;

import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.RenderingValues;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

public class Fury extends MobEffect {
    private int heartBeatdelay = 0;
    private boolean resetFirst = true;
    private boolean resetSecond = false;
    private int firstBeat = 0;
    private int secondBeat = 9;
    private int veinTransparency = 10;
    private int allTR = 20;
    public Fury(MobEffectCategory pCategory, int pColor) { super(pCategory, pColor); }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        allTR = Math.min(20, pDuration);


        return true;
    }
    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        if (pLivingEntity instanceof ServerPlayer player) {
            veinTransparency = Math.max(0, veinTransparency - 1);
            heartBeatdelay = Math.max(0, heartBeatdelay - 1);
            if (firstBeat > 0 && resetFirst) {
                firstBeat--;
            } else if (firstBeat == 0) {
                firstBeat = 10;
                resetFirst = false;
                resetSecond = true;
                veinTransparency = 10;
            }
            if (secondBeat > 0 && resetSecond) {
                secondBeat--;
            } else if (secondBeat == 0) {
                secondBeat = 9;
                resetFirst = true;
                resetSecond = false;
                veinTransparency = 10;
            }
            if (heartBeatdelay == 0) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.HEART_BEAT.get()), player);
                heartBeatdelay = 20;
            }
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager == null) return;
            abilityManager.getAbility(RenderingValues.class).ifPresent(renderingValues -> {
                renderingValues.setVein(veinTransparency);
                renderingValues.setBack(allTR);

                renderingValues.heartbeat = heartBeatdelay;
            });
        }
    }
}
