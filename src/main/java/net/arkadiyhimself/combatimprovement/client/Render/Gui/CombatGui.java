package net.arkadiyhimself.combatimprovement.client.Render.Gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.api.ItemRegistry;
import net.arkadiyhimself.combatimprovement.api.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.DJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.DataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrier;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import top.theillusivec4.curios.api.SlotResult;

import java.awt.*;
import java.util.List;

public class CombatGui {
    private static final ResourceLocation DASH1 = new ResourceLocation(CombatImprovement.MODID, "textures/gui/dash/dash1.png");
    private static final ResourceLocation DASH2 = new ResourceLocation(CombatImprovement.MODID, "textures/gui/dash/dash2.png");
    private static final ResourceLocation DASH3 = new ResourceLocation(CombatImprovement.MODID, "textures/gui/dash/dash3.png");
    private static final ResourceLocation DJUMP = new ResourceLocation(CombatImprovement.MODID, "textures/gui/djump.png");
    private static final ResourceLocation BARRIER_LAYER = new ResourceLocation(CombatImprovement.MODID, "textures/gui/barrier_layers.png");
    private static final ResourceLocation NEWGUI = new ResourceLocation(CombatImprovement.MODID, "textures/gui/newgui.png");
    private static final ResourceLocation COMBATHEALTH = new ResourceLocation(CombatImprovement.MODID, "textures/gui/combathealth.png");
    private static final ResourceLocation COMBATMANA = new ResourceLocation(CombatImprovement.MODID, "textures/gui/combatmana.png");
    private static final ResourceLocation COMBATSTAMINA = new ResourceLocation(CombatImprovement.MODID, "textures/gui/combatstamina.png");
    private static final ResourceLocation COMBATHUNGER = new ResourceLocation(CombatImprovement.MODID, "textures/gui/combathunger.png");
    private static final ResourceLocation CURIOSLOT = new ResourceLocation(CombatImprovement.MODID, "textures/gui/curioslots/curioslot.png");
    private static final ResourceLocation SPELLCASTER = new ResourceLocation(CombatImprovement.MODID, "textures/gui/curioslots/spellcaster.png");
    private static final ResourceLocation PASSIVECASTER = new ResourceLocation(CombatImprovement.MODID, "textures/gui/curioslots/passivecaster.png");

    // image sizes
    private static final int iconSize = 20;
    private static final int newguiX = 48;
    private static final int newguiY = 48;
    private static final int healthX = 40;
    private static final int healthY = 40;
    public static final IGuiOverlay DASH_ICON = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        Dash dash = AttachDash.getUnwrap(player);
        if (dash == null || dash.dashLevel <= 0 || player.isCreative() || player.isSpectator()) { return; }

        ResourceLocation dashIcon = switch (dash.dashLevel) {
            default -> null;
            case 1 -> DASH1;
            case 2 -> DASH2;
            case 3 -> DASH3;
        };

        if (dashIcon == null) { return; }

        int recharge;
        int xPlus = player.getMainArm() == HumanoidArm.RIGHT ? 95 : -95;
        int x = 10;
        int y = 40;
        if (dash.getDur() > 0) {
            recharge = 20 - (int) ((float) dash.getDur() / (float) dash.initialDur * 20);
        } else {
            recharge = (int) ((float) dash.getRecharge() / (float) dash.getMaxRecharge() * 20);
        }
        RenderSystem.setShaderTexture(0, dashIcon);
        guiGraphics.blit(dashIcon, x, y, 0, 0, iconSize, iconSize, iconSize * 2, iconSize);

        float color = recharge == 0 || dash.getDur() > 0 ? 1f : 0.85f;
       // RenderSystem.setShaderColor(color, color, color, color);
        guiGraphics.blit(dashIcon, x, y, 20, 0, iconSize - recharge, iconSize, iconSize * 2, iconSize);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
    });
    public static final IGuiOverlay DJUMP_ICON = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        DJump dJump = AttachDJump.getUnwrap(player);
        if (dJump == null || player.isCreative() || player.isSpectator()) { return; }
        int x = 40;
        int y = 40;
        RenderSystem.setShaderTexture(0, DJUMP);
        int offSet = dJump.canDJump() ? 0 : 20;
        guiGraphics.blit(DJUMP, x, y, offSet,0,20,20,40,20);
    }));
    public static final IGuiOverlay BARRIER_LAYERS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        LayeredBarrier dash = LayeredBarrierEffect.getUnwrap(player);
        int amount = dash.layers;
        int x = 2;
        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 12;
        RenderSystem.setShaderTexture(0, BARRIER_LAYER);
        for (int i = 0; i < amount; i++) {
            guiGraphics.blit(BARRIER_LAYER, x,y - (i * 9),0,0,8,8,8,8);
        }
    });
    public static final IGuiOverlay COMBATGUI = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Player player = Minecraft.getInstance().player;
        if (player.isSpectator() || player.isCreative()) { return; }

        float maxHP = player.getMaxHealth();
        float curHP = player.getHealth();
        float perHP = curHP / maxHP;
        int widthHP = Math.min((int) maxHP * 2, 320);
        RenderSystem.setShaderTexture(0, NEWGUI);
        guiGraphics.blit(NEWGUI,4,4,0,0,42,32, newguiX, newguiY);
        guiGraphics.blit(NEWGUI,46,7,0,33, widthHP,6,newguiX * widthHP / 40, newguiY);
        guiGraphics.blit(NEWGUI,46 + widthHP,6,40,32,7,8, newguiX, newguiY);

        DataSync data = AttachDataSync.getUnwrap(player);
        if (data != null) {
            float maxMN = data.getMaxMana();
            float curMN = data.mana;
            float perMN = curMN / maxMN;
            int widthMN = Math.min((int) maxMN * 2, 320);

            float maxST = data.getMaxStamina();
            float curST = data.stamina;
            float perST = curST / maxMN;
            int widthST = Math.min((int) maxST * 2, 320);

            guiGraphics.blit(NEWGUI,46,17,0,33, widthMN,6, newguiX * widthMN / 40, newguiY);
            guiGraphics.blit(NEWGUI,46 + widthMN,16,40,32,7,8,newguiX,newguiY);
            guiGraphics.blit(NEWGUI,46,27,0,33, widthST,6, newguiX * widthST / 40, newguiY);
            guiGraphics.blit(NEWGUI,46 + widthST,26,40,32,7,8,newguiX,newguiY);

            RenderSystem.setShaderTexture(0, COMBATHEALTH);
            int num1 = 0;
            if (player.hasEffect(MobEffects.POISON)) { num1 = 8; }
            if (player.hasEffect(MobEffects.WITHER)) { num1 = 16; }
            if (player.isFreezing()) { num1 = 24; }
            if (player.hasEffect(MobEffectRegistry.ABSOLUTE_BARRIER.get())) { num1 = 32; }
            guiGraphics.blit(COMBATHEALTH,45,8,0,num1 + 4,widthHP,4,widthHP,40);
            guiGraphics.blit(COMBATHEALTH,45,8,0, num1, (int) (widthHP * perHP),4,widthHP,40);

            RenderSystem.setShaderTexture(0, COMBATMANA);
            int num2 = data.philStoned ? 8 : 0;
            guiGraphics.blit(COMBATMANA,45,18,0,num2 + 4, widthMN,4, widthMN,40);
            guiGraphics.blit(COMBATMANA, 45, 18, 0, num2, (int) (widthMN * perMN),4, widthMN,40);

            RenderSystem.setShaderTexture(0, COMBATSTAMINA);
            int num3 = 0;
            guiGraphics.blit(COMBATSTAMINA,45,28,0,num3 + 4, widthST,4, widthST,40);
            guiGraphics.blit(COMBATSTAMINA, 45, 28, 0, num3, (int) (widthST * perST),4, widthST,40);
        }

        FoodData foodData = player.getFoodData();
        RenderSystem.setShaderTexture(0, COMBATHUNGER);
        int num4 = 20;
        if (player.hasEffect(MobEffects.HUNGER)) { num4 = 40; }
        if (WhereMagicHappens.Abilities.hasCurio(player, ItemRegistry.SOUL_EATER.get())) { num4 = 60; }
        guiGraphics.blit(COMBATHUNGER,10,10, num4-20,20,20,20,60,40);
        guiGraphics.blit(COMBATHUNGER,30,30, num4,20,-20, -foodData.getFoodLevel(), 60, 40);

        RenderSystem.setShaderTexture(0, NEWGUI);

        LayeredBarrierEffect.get(player).ifPresent(layered -> {
            for (int i = 0; i < layered.layers; i++) {
                guiGraphics.blit(NEWGUI, 52 + widthHP + i * 4, 6, 18, 40, 5, 8, newguiX, newguiY);
            }
        });
    }));
    public static final IGuiOverlay CURIOSLOTS = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = gui.getFont();
        if (player == null || player.isCreative() || player.isSpectator()) { return; }
        int x = 10;
        int y = 80;
        List<SlotResult> spellcasters = WhereMagicHappens.Abilities.findAllCurios(player, "spellcaster");
        List<SlotResult> passivecasters = WhereMagicHappens.Abilities.findAllCurios(player, "passivecaster");

        int num1 = spellcasters.size();
        int num2 = passivecasters.size();

        int allSlots = num1 + num2;
        RenderSystem.setShaderTexture(0, CURIOSLOT);
        for (int i = 0; i < allSlots; i++) {
            guiGraphics.blit(CURIOSLOT, x,y + i * 20,0,0,20,20,20,20);
        }

        for (int j = 0; j < num1; j++) {
            RenderSystem.setShaderTexture(0, SPELLCASTER);
            ItemStack item = spellcasters.get(j).stack();
            if (item.isEmpty()) {
                guiGraphics.blit(SPELLCASTER, x,y + j * 20,0,0,20,20,20,20);
            } else {
              //  Minecraft.getInstance().getItemRenderer().(item, x + 2, y + j * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + j * 20 + 2);
                float f = player.getCooldowns().getCooldownPercent(item.getItem(), Minecraft.getInstance().getFrameTime());
                if (false) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.disableBlend();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tesselator tesselator1 = Tesselator.getInstance();
                    BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                    WhereMagicHappens.Gui.fillRect(bufferbuilder1, x + 2, y + 2 + Mth.floor(16.0F * (1.0F - f)) + j * 20, 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                    RenderSystem.enableBlend();
                    RenderSystem.enableDepthTest();
                }
            }
        }

        for (int k = 0; k < num2; k++) {
            RenderSystem.setShaderTexture(0, PASSIVECASTER);
            ItemStack item = passivecasters.get(k).stack();
            if (item.isEmpty()) {
                guiGraphics.blit(PASSIVECASTER, x,y + k * 20 + num1 * 20,0,0,20,20,20,20);
            } else {
             //   Minecraft.getInstance().getItemRenderer().renderGuiItem(item, x + 2, y + k * 20 + num1 * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + k * 20 * (num1 + 2));
               float f = player.getCooldowns().getCooldownPercent(item.getItem(), Minecraft.getInstance().getFrameTime());
                if (false) {
                    RenderSystem.disableDepthTest();
                    RenderSystem.enableBlend();
                    RenderSystem.defaultBlendFunc();
                    Tesselator tesselator1 = Tesselator.getInstance();
                    BufferBuilder bufferbuilder1 = tesselator1.getBuilder();
                    WhereMagicHappens.Gui.fillRect(bufferbuilder1, x + 2, y + 2 + num1 * 20 + Mth.floor(16.0F * (1.0F - f)) + k * 20, 16, Mth.ceil(16.0F * f), 255, 255, 255, 127);
                    RenderSystem.enableDepthTest();
                }
            }
        }
    }));
}
