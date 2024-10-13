package net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.holders;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_effect.LivingEffectHolder;
import net.arkadiyhimself.fantazia.api.type.entity.IDamageEventListener;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.networking.packets.stuff.PlaySoundForUIS2C;
import net.arkadiyhimself.fantazia.particless.options.EntityChasingParticleOption;
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
import net.neoforged.neoforge.event.entity.living.LivingDamageEvent;
import net.neoforged.neoforge.network.PacketDistributor;

public class DoomedEffect extends LivingEffectHolder implements IDamageEventListener {

    private int soul = 0;
    private int whisper = 0;

    public DoomedEffect(LivingEntity owner) {
        super(owner, Fantazia.res("doomed_effect"), FTZMobEffects.DOOMED);
    }

    @Override
    public void tick() {
        super.tick();
        if (duration() <= 0) return;

        if (soul-- <= 0) {
            soul = Fantazia.RANDOM.nextInt(6,8);
            VisualHelper.randomEntityChasingParticle(getEntity(), ((entity, vec3) -> new EntityChasingParticleOption<>(entity.getId(), vec3, FTZParticleTypes.DOOMED_SOULS.random())), 0.75f);
        }

        if (whisper-- <= 0) {
            whisper = Fantazia.RANDOM.nextInt(85,125);
            if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.WHISPER.get()));
        }
    }

    @Override
    public void onHit(LivingDamageEvent.Pre event) {
        if (duration() <= 0 || event.getNewDamage() <= 0 || event.getSource().is(FTZDamageTypeTags.NON_LETHAL)) return;
        event.setNewDamage(Float.MAX_VALUE);
        getEntity().playSound(FTZSoundEvents.ENTITY_FALLEN_BREATH.get());
        double x = getEntity().getX();
        double y = getEntity().getY();
        double z = getEntity().getZ();
        double height = getEntity().getBbHeight();
        if (Minecraft.getInstance().level != null) Minecraft.getInstance().level.addParticle(FTZParticleTypes.FALLEN_SOUL.get(), x, y + height * 2 / 3, z, 0.0D, -0.135D, 0.0D);

        BlockPos blockPos = getEntity().getOnPos();
        Block block = getEntity().level().getBlockState(blockPos).getBlock();
        if (block == Blocks.DIRT || block == Blocks.SAND || block == Blocks.NETHERRACK || block == Blocks.GRASS_BLOCK) getEntity().level().setBlockAndUpdate(blockPos, Blocks.SOUL_SAND.defaultBlockState());
    }

    @Override
    public void added(MobEffectInstance instance) {
        super.added(instance);
        if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.DOOMED.get()));
    }

    @Override
    public void ended() {
        super.ended();
        if (getEntity() instanceof ServerPlayer serverPlayer) PacketDistributor.sendToPlayer(serverPlayer, new PlaySoundForUIS2C(FTZSoundEvents.UNDOOMED.get()));
    }
}
