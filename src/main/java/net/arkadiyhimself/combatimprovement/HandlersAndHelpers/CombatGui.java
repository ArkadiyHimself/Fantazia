package net.arkadiyhimself.combatimprovement.HandlersAndHelpers;

import com.mojang.blaze3d.systems.RenderSystem;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.FragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrier;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;

public class CombatGui {
    private static final ResourceLocation DASH1 = new ResourceLocation(CombatImprovement.MODID, "textures/gui/dash/dash1.png");
    private static final ResourceLocation DASH2 = new ResourceLocation(CombatImprovement.MODID, "textures/gui/dash/dash2.png");
    private static final ResourceLocation DASH3 = new ResourceLocation(CombatImprovement.MODID, "textures/gui/dash/dash3.png");
    private static final ResourceLocation BARRIER_LAYER = new ResourceLocation(CombatImprovement.MODID, "textures/gui/barrier_layers.png");
    public static final IGuiOverlay DASH_ICON = ((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        Dash dash = AttachDash.getUnwrap(player);
        if (dash == null || dash.dashLevel <= 0) { return; }

        ResourceLocation dashIcon = switch (dash.dashLevel) {
            default -> null;
            case 1 -> DASH1;
            case 2 -> DASH2;
            case 3 -> DASH3;
        };

        if (dashIcon == null) { return; }

        int recharge;
        int xPlus = player.getMainArm() == HumanoidArm.RIGHT ? 95 : -95;
        int x = screenWidth / 2 + xPlus;
        int y = screenHeight - 21;
        if (dash.getDur() > 0) {
            recharge = 20 - (int) ((float) dash.getDur() / (float) dash.initialDur * 20);
        } else {
            recharge = (int) ((float) dash.getRecharge() / (float) dash.getMaxRecharge() * 20);
        }
        RenderSystem.setShaderTexture(0, dashIcon);
        GuiComponent.blit(poseStack, x, y, 0, 0, 20, 20, 40, 20);
        float color = recharge == 0 || dash.getDur() > 0 ? 1f : 0.85f;
        RenderSystem.setShaderColor(color, color, color, color);
        GuiComponent.blit(poseStack, x, y, 20, 0, 20 - recharge, 20, 40, 20);
    });
    public static final IGuiOverlay BARRIER_LAYERS = ((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        LayeredBarrier dash = LayeredBarrierEffect.getUnwrap(player);
        int amount = dash.layers;
        int x = 2;
        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 12;
        RenderSystem.setShaderTexture(0, BARRIER_LAYER);
        for (int i = 0; i < amount; i++) {
            GuiComponent.blit(poseStack, x,y - (i * 9),0,0,8,8,8,8);
        }
    });
    public static final IGuiOverlay FRAG_SWORD_DMG = ((gui, poseStack, partialTick, screenWidth, screenHeight) -> {
        Player player = Minecraft.getInstance().player;
        for (int i = 0; i < 9; i++) {
            ItemStack stack = player.getInventory().getItem(i);
            if (stack.getItem() instanceof FragileBlade fragileBlade) {
                FragileBladeCap cap = AttachFragileBlade.getUnwrap(stack);
                int damage = (int) (cap.damage + fragileBlade.getDamage());
                int doubleDigits = damage >= 10 ? 3 : 0;
                int x0 = screenWidth / 2 - 88 + i * 20 - doubleDigits;
                int y0 = screenHeight - 19;
                Component value = Component.translatable(String.valueOf(damage)).withStyle(cap.getDamageFormatting());
                Minecraft.getInstance().font.drawShadow(poseStack, value, x0, y0, 16447222);
            }
        }
    });
}
