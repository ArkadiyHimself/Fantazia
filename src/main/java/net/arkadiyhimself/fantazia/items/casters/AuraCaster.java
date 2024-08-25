package net.arkadiyhimself.fantazia.items.casters;

import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.items.IChangingIcon;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.registries.FTZSoundEvents;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.TamableAnimal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Supplier;

public class AuraCaster extends Item implements IChangingIcon {
    private final Supplier<BasicAura<? extends Entity>> basicAura;
    public AuraCaster(Supplier<BasicAura<? extends Entity>> basicAura) {
        super(new Properties().stacksTo(1).fireResistant().rarity(Rarity.RARE));
        this.basicAura = basicAura;
    }
    @Override
    public void registerVariants() {
        ItemProperties.register(FTZItems.LEADERS_HORN.get(), new ResourceLocation("tooting"), ((pStack, pLevel, pEntity, pSeed) -> pEntity != null && pEntity.isUsingItem() && pEntity.getUseItem() == pStack ? 1f : 0f));
    }
    @Override
    public @NotNull InteractionResultHolder<ItemStack> use(@NotNull Level pLevel, @NotNull Player pPlayer, @NotNull InteractionHand pUsedHand) {
        if (this == FTZItems.LEADERS_HORN.get() && !pPlayer.getCooldowns().isOnCooldown(this)) {
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
        } else return super.use(pLevel, pPlayer,pUsedHand);
    }
    @Override
    public @NotNull UseAnim getUseAnimation(@NotNull ItemStack pStack) {
        return this == FTZItems.LEADERS_HORN.get() ? UseAnim.TOOT_HORN : super.getUseAnimation(pStack);
    }
    public BasicAura<? extends Entity> getBasicAura() {
        return basicAura.get();
    }
    public List<Component> buildTooltip() {
        List<Component> components = Lists.newArrayList();
        if (getBasicAura() != null) components.addAll(getBasicAura().buildItemTooltip(null));
        return components;
    }
}
