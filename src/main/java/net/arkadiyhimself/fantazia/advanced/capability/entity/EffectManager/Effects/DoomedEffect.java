package net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.Effects;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.capability.entity.EffectManager.EffectHolder;
import net.arkadiyhimself.fantazia.events.WhereMagicHappens;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registry.MobEffectRegistry;
import net.arkadiyhimself.fantazia.registry.ParticleRegistry;
import net.arkadiyhimself.fantazia.registry.SoundRegistry;
import net.arkadiyhimself.fantazia.util.interfaces.IDamageReacting;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DoomedEffect extends EffectHolder implements IDamageReacting {
    private int soulCD = 0;
    private int whisperCD = 0;
    public DoomedEffect(LivingEntity owner) {
        super(owner, MobEffectRegistry.DOOMED.get());
    }
    @Override
    public void tick() {
        super.tick();
        if (getDur() <= 0) return;
        if (soulCD > 0) soulCD--;
        if (whisperCD > 0) whisperCD--;

        if (soulCD <= 0) {
            soulCD = Fantazia.RANDOM.nextInt(6,8);
            int num = Fantazia.RANDOM.nextInt(0, ParticleRegistry.DOOMED_SOULS.size());
            WhereMagicHappens.Abilities.randomParticleOnModel(getOwner(), ParticleRegistry.DOOMED_SOULS.get(num).get(),
                    WhereMagicHappens.Abilities.ParticleMovement.CHASE_AND_FALL);
        }

        if (whisperCD <= 0) {
            whisperCD = Fantazia.RANDOM.nextInt(85,125);
            if (getOwner() instanceof ServerPlayer serverPlayer) {
                NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.WHISPER.get()), serverPlayer);
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
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.DOOMED.get()), serverPlayer);
        }
    }

    @Override
    public void ended() {
        super.ended();
        if (getOwner() instanceof ServerPlayer serverPlayer) {
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.UNDOOMED.get()), serverPlayer);
        }
    }
}
