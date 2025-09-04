package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.common.advanced.cleanse.EffectCleansing;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.arkadiyhimself.fantazia.common.registries.FTZAttachmentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.util.wheremagichappens.ActionsHelper;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.monster.Creeper;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Creeper.class)
public abstract class MixinCreeper extends LivingEntity {

    @Shadow private int swell;

    @Shadow @Final private static EntityDataAccessor<Boolean> DATA_IS_IGNITED;

    @Shadow protected abstract void spawnLingeringCloud();

    protected MixinCreeper(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;discard()V"), method = "explodeCreeper")
    private void cancelDiscard(Creeper instance) {
        int layers = instance.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS);
        float barrier = instance.getData(FTZAttachmentTypes.BARRIER_HEALTH);
        if (layers > 0 ) {
            entityData.set(DATA_IS_IGNITED, false);
            swell = 0;

            layers--;
            if (layers <= 0) {
                EffectCleansing.forceCleanse(instance, FTZMobEffects.LAYERED_BARRIER);
                instance.level().playSound(null, instance.blockPosition(), FTZSoundEvents.EFFECT_LAYERED_BARRIER_BREAK.get(), SoundSource.AMBIENT);
            } else instance.level().playSound(null, instance.blockPosition(), FTZSoundEvents.EFFECT_LAYERED_BARRIER_DAMAGE.get(), SoundSource.AMBIENT);
            instance.setData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS, layers);
            ActionsHelper.interrupt(instance);
            if (!level().isClientSide()) IPacket.layeredBarrierDamaged(instance);
            return;
        }
        instance.discard();
    }

    @Redirect(at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/monster/Creeper;spawnLingeringCloud()V"), method = "explodeCreeper")
    private void cancelCloud(Creeper instance) {
        if (instance.getData(FTZAttachmentTypes.LAYERED_BARRIER_LAYERS) <= 0) spawnLingeringCloud();
    }
}
