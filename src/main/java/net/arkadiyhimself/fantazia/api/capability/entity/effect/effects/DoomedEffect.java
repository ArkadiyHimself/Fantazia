package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.particless.SoulParticle;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DoomedEffect extends EffectHolder implements IDamageReacting {
    private int soulCD = 0;
    private int whisperCD = 0;
    public DoomedEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DOOMED);
    }
    @Override
    public void tick() {
        super.tick();
        if (getDur() <= 0) return;
        if (soulCD > 0) soulCD--;
        if (whisperCD > 0) whisperCD--;

        if (soulCD <= 0) {
            soulCD = Fantazia.RANDOM.nextInt(6,8);
            VisualHelper.randomParticleOnModel(getOwner(), SoulParticle.randomSoulParticle(), VisualHelper.ParticleMovement.CHASE_AND_FALL);
        }

        if (whisperCD <= 0) {
            whisperCD = Fantazia.RANDOM.nextInt(85,125);
            if (getOwner() instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.WHISPER), serverPlayer);
            }
        }
    }

    @Override
    public void onHit(LivingHurtEvent event) {
        if (getDur() > 0) event.setAmount(Float.MAX_VALUE);
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (getOwner() instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DOOMED), serverPlayer);
        }
    }

    @Override
    public void ended() {
        super.ended();
        if (getOwner() instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.UNDOOMED), serverPlayer);
        }
    }
}
