package net.arkadiyhimself.fantazia.mixin;

import com.mojang.blaze3d.vertex.PoseStack;
import net.arkadiyhimself.fantazia.client.gui.RenderBuffers;
import net.arkadiyhimself.fantazia.client.render.VisualHelper;
import net.arkadiyhimself.fantazia.client.screen.AmplifyResource;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.ToolUtilisationHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.data.item.rechargeable_tool.RechargeableToolData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(GuiGraphics.class)
public abstract class MixinGuiGraphics {

    @Shadow public abstract int drawString(Font font, String text, int x, int y, int color, boolean dropShadow);

    @Shadow @Final private PoseStack pose;
    @Shadow @Final private Minecraft minecraft;

    @Shadow public abstract int drawString(Font font, Component text, int x, int y, int color);

    @Redirect(at = @At(value = "INVOKE", target = "Ljava/util/List;get(I)Ljava/lang/Object;"), method = "renderTooltipInternal")
    private <E> E getLine(List<E> instance, int i) {
        return instance.get(i);
    }

    @Inject(
            at = @At(
                    value = "INVOKE",
                    target = "Lcom/mojang/blaze3d/vertex/PoseStack;pushPose()V"
            ),
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
    )
    private void renderStackOneItem(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci) {
        if (stack.getCount() == 1 && text == null && RenderBuffers.AMPLIFY_ITEM_STACK_COUNT.notEnough()) {
            RenderBuffers.AMPLIFY_ITEM_STACK_COUNT = AmplifyResource.REGULAR;
            String s = String.valueOf(1);
            this.pose.translate(0.0F, 0.0F, 200.0F);
            this.drawString(font, s, x + 19 - 2 - font.width(s), y + 6 + 3, 16733525, true);
        }
    }

    @Redirect(at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"),
            method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V"
    )
    private int changeCountColor(GuiGraphics instance, Font font, String text, int x, int y, int color, boolean dropShadow) {
        AmplifyResource resource = RenderBuffers.AMPLIFY_ITEM_STACK_COUNT;
        if (resource.notEnough()) color = 16733525;
        else if (resource.isEnough()) color = 5635925;

        RenderBuffers.AMPLIFY_ITEM_STACK_COUNT = AmplifyResource.REGULAR;
        drawString(font, text, x, y, color, dropShadow);
        return x;
    }

    @Inject(at = @At("HEAD"), method = "Lnet/minecraft/client/gui/GuiGraphics;renderItem(Lnet/minecraft/world/entity/LivingEntity;Lnet/minecraft/world/level/Level;Lnet/minecraft/world/item/ItemStack;IIII)V")
    private void renderAmplifiedEnchantment(LivingEntity entity, Level level, ItemStack stack, int x, int y, int seed, int guiOffset, CallbackInfo ci) {
        Integer value = stack.get(FTZDataComponentTypes.JEI_AMPLIFIED_ENCHANTMENT);
        if (value != null && value > 0) {
            Component component = VisualHelper.componentLevel(value);
            pose.pushPose();
            pose.translate(0,0,500);
            drawString(minecraft.font, component, x + 11, y - 1, 16777215);
            pose.popPose();
        }
    }

    @Inject(at = @At("HEAD"), method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V")
    private void renderItemDecorations(Font font, ItemStack stack, int x, int y, String s, CallbackInfo ci) {
        if (!RenderBuffers.RENDER_RECHARGEABLE_TOOL_DATA) return;
        RenderBuffers.RENDER_RECHARGEABLE_TOOL_DATA = false;
        if (stack.isEmpty()) return;

        Item item = stack.getItem();
        RechargeableToolData data = RechargeableToolData.getToolData(item);
        ToolUtilisationHolder holder = PlayerAbilityHelper.takeHolder(Minecraft.getInstance().player, ToolUtilisationHolder.class);

        if (holder == null || data == null) return;


        int amount = holder.getCapacity(stack.getItem());
        if (amount == -1) return;

        pose.pushPose();
        String text = String.valueOf(amount);

        int color = 16777215;
        if (amount >= holder.maxCapacity(item)) color = 16755200;
        else if (amount == 0) color = 16733525;
        pose.translate(0,0,200);
        drawString(font, text, x, y, color, true);

        pose.popPose();
    }
}
