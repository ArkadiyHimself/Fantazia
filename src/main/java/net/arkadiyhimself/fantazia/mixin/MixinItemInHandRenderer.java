package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.LivingDataGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.living_data.holders.EvasionHolder;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityGetter;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.minecraft.client.renderer.ItemInHandRenderer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemDisplayContext;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemInHandRenderer.class)
public class MixinItemInHandRenderer {
    @Inject(at = @At("HEAD"), method = "renderItem", cancellable = true)
    private void cancel(LivingEntity pEntity, ItemStack pItemStack, ItemDisplayContext pDisplayContext, boolean pLeftHand, PoseStack pPoseStack, MultiBufferSource pBuffer, int pSeed, CallbackInfo ci) {
        if (pDisplayContext.firstPerson()) return;
        EvasionHolder evasionHolder = LivingDataGetter.takeHolder(pEntity, EvasionHolder.class);
        if (evasionHolder != null && evasionHolder.getIFrames() > 0) ci.cancel();

        if (!(pEntity instanceof Player player)) return;
        DashHolder dashHolder = PlayerAbilityGetter.takeHolder(player, DashHolder.class);
        if (dashHolder != null && dashHolder.isDashing() && dashHolder.getLevel() >= 3) ci.cancel();
    }

}
