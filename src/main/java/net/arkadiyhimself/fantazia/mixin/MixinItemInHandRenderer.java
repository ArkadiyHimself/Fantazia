package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
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
        EvasionData evasionData = DataGetter.takeDataHolder(pEntity, EvasionData.class);
        if (evasionData != null && evasionData.getIFrames() > 0) ci.cancel();

        if (!(pEntity instanceof Player player)) return;
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        if (dash != null && dash.isDashing() && dash.getLevel() >= 3) ci.cancel();
    }

}
