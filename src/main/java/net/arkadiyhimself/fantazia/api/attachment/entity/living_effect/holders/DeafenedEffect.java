package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.DamageTypeTags;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class DeafenedEffect extends LivingEffectHolder implements IDamageEventListener {

    public DeafenedEffect(LivingEntity livingEntity) {
        super(livingEntity, Fantazia.res("deafened_effect"), FTZMobEffects.DEAFENED);
    }

    @Override
    public void onHit(LivingDamageEvent.Post event) {
        if (event.getSource().is(DamageTypes.SONIC_BOOM) || event.getSource().is(DamageTypeTags.IS_EXPLOSION)) {
            LivingEffectHelper.makeDeaf(getEntity(), 200);
            LivingEffectHelper.microStun(getEntity());
            if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.RINGING.get()));
        }
    }

    public boolean renderDeaf() {
        return duration() > 0;
    }
}
