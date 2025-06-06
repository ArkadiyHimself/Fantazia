package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.aura.Aura;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.registries.custom.Auras;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class LeadersHornItem extends AuraCasterItem {

    public LeadersHornItem() {
        super(Auras.LEADERSHIP);
    }

    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        pPlayer.startUsingItem(pUsedHand);
        pPlayer.getCooldowns().addCooldown(this, 400);
        pPlayer.awardStat(Stats.ITEM_USED.get(this));
        pPlayer.level().playSound(null, pPlayer.blockPosition(), FTZSoundEvents.LEADERS_HORN.get(), SoundSource.NEUTRAL);

        AABB aabb = pPlayer.getBoundingBox().inflate(128);
        if (pLevel instanceof ServerLevel serverLevel) {
            List<TamableAnimal> tamableAnimals = serverLevel.getEntitiesOfClass(TamableAnimal.class, aabb);
            tamableAnimals.removeIf(tamableAnimal -> tamableAnimal.getOwner() != pPlayer || !tamableAnimal.isInSittingPose());
            tamableAnimals.forEach(tamableAnimal -> tamableAnimal.interact(pPlayer, InteractionHand.MAIN_HAND));
        }
        return InteractionResultHolder.success(pPlayer.getItemInHand(pUsedHand));
    }

    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return UseAnim.TOOT_HORN;
    }

    @Override
    public int getUseDuration(@NotNull ItemStack stack, @NotNull LivingEntity entity) {
        return 140;
    }

}
