package net.arkadiyhimself.fantazia.mixin;

import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.newdata.EvasionData;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.RenderLayerParent;
import net.minecraft.client.renderer.entity.layers.ElytraLayer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ElytraLayer.class)
public abstract class MixinElytraLayer<T extends LivingEntity, M extends EntityModel<T>> extends RenderLayer<T,M> {
    public MixinElytraLayer(RenderLayerParent<T, M> pRenderer) {
        super(pRenderer);
    }
    @Inject(method = "shouldRender", at = @At("HEAD"), cancellable = true, remap = false)
    private void melding(ItemStack stack, T entity, CallbackInfoReturnable<Boolean> cir) {
        EvasionData evasionData = DataGetter.takeDataHolder(entity, EvasionData.class);
        if (evasionData != null && evasionData.getIFrames() > 0) cir.setReturnValue(false);

        if (!(entity instanceof Player player)) return;
        Dash dash = AbilityGetter.takeAbilityHolder(player, Dash.class);
        if (dash != null && dash.isDashing() && dash.getLevel() >= 3) cir.setReturnValue(false);
    }
}