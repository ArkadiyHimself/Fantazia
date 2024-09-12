package net.arkadiyhimself.fantazia.api.capability.entity.effect.effects;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.IDamageReacting;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectHolder;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.networking.packets.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.particless.SoulParticle;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZParticleTypes;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class DoomedEffect extends EffectHolder implements IDamageReacting {
    private int soulCD = 0;
    private int whisperCD = 0;
    public DoomedEffect(LivingEntity owner) {
        super(owner, FTZMobEffects.DOOMED.get());
    }
    @Override
    public void tick() {
        super.tick();
        if (getDur() <= 0) return;
        if (soulCD > 0) soulCD--;
        if (whisperCD > 0) whisperCD--;

        if (soulCD <= 0) {
            soulCD = Fantazia.RANDOM.nextInt(6,8);
            VisualHelper.randomParticleOnModel(getOwner(), SoulParticle.DOOMED_SOULS.random(), VisualHelper.ParticleMovement.CHASE_AND_FALL);
        }

        if (whisperCD <= 0) {
            whisperCD = Fantazia.RANDOM.nextInt(85,125);
            if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.WHISPER.get()), serverPlayer);
        }
    }

    @Override
    public void onHit(LivingHurtEvent event) {
        if (getDur() > 0 && event.getAmount() > 0 && !event.isCanceled() && !event.getSource().is(FTZDamageTypeTags.NON_LETHAL)) {
            event.setAmount(Float.MAX_VALUE);
            getOwner().playSound(FTZSoundEvents.FALLEN_BREATH.get());
            double x = getOwner().getX();
            double y = getOwner().getY();
            double z = getOwner().getZ();
            double height = getOwner().getBbHeight();
            if (Minecraft.getInstance().level != null) Minecraft.getInstance().level.addParticle(FTZParticleTypes.FALLEN_SOUL.get(), x, y + height * 2 / 3, z, 0.0D, -0.135D, 0.0D);

            BlockPos blockPos = getOwner().getOnPos();
            Block block = getOwner().level().getBlockState(blockPos).getBlock();
            if (block == Blocks.DIRT || block == Blocks.SAND || block == Blocks.NETHERRACK || block == Blocks.GRASS_BLOCK) getOwner().level().setBlockAndUpdate(blockPos, Blocks.SOUL_SAND.defaultBlockState());
        }
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.DOOMED.get()), serverPlayer);
    }

    @Override
    public void ended() {
        super.ended();
        if (getOwner() instanceof ServerPlayer serverPlayer) NetworkHandler.sendToPlayer(new PlaySoundForUIS2C(FTZSoundEvents.UNDOOMED.get()), serverPlayer);
    }

    @Override
    public boolean unSyncedDuration() {
        return true;
    }
}
