package net.arkadiyhimself.combatimprovement.MobEffects.effectsdostuff;

import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.combatimprovement.api.ParticleRegistry;
import net.arkadiyhimself.combatimprovement.api.SoundRegistry;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffectCategory;
import net.minecraft.world.entity.LivingEntity;

import java.util.Random;

public class Doomed extends MobEffect {
    double x;
    double y;
    double z;
    boolean dospawnsoul = false;
    boolean dowhisper = false;
    Random random = new Random();
    int whispercooldown = 80;

    int soulcooldown = 20;
    public Doomed(MobEffectCategory mobEffectCategory, int p_19452_) {
        super(mobEffectCategory, p_19452_);
    }

    @Override
    public boolean isDurationEffectTick(int pDuration, int pAmplifier) {
        return true;
    }

    @Override
    public void applyEffectTick(LivingEntity pLivingEntity, int pAmplifier) {
        // cool down decays
        whispercooldown -= 1;
        soulcooldown -= 1;

        // when cool down is 0, sound plays and particles appear
        dowhisper = whispercooldown == 0;
        dospawnsoul = soulcooldown == 0;

        if (dospawnsoul) {
            // randomly choose one of doomed souls particle
            int num = random.nextInt(0, ParticleRegistry.doomedSoulParticles.size());
            WhereMagicHappens.Abilities.createRandomParticleOnHumanoid(pLivingEntity, ParticleRegistry.doomedSoulParticles.get(num).get(),
                    WhereMagicHappens.Abilities.ParticleMovement.CHASE_AND_FALL);
            soulcooldown = random.nextInt(6, 8);
        }

        if (dowhisper && pLivingEntity instanceof ServerPlayer serverPlayer) {
            // the game randomly decides which whisper sound to play
            NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(SoundRegistry.WHISPER.get()), serverPlayer);
            // randomly decide cool down
            whispercooldown = random.nextInt(30, 48);
        }
        super.applyEffectTick(pLivingEntity, pAmplifier);
    }
}
