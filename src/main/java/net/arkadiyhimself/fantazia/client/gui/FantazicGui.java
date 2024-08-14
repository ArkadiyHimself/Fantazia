package net.arkadiyhimself.fantazia.client.gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.aura.AuraHelper;
import net.arkadiyhimself.fantazia.advanced.aura.AuraInstance;
import net.arkadiyhimself.fantazia.advanced.aura.BasicAura;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.Dash;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.DoubleJump;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.ManaData;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.StaminaData;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.BarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.effects.StunEffect;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCap;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicMath;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class FantazicGui {
    private static final ResourceLocation AURA_POSITIVE_COMPONENT = Fantazia.res("textures/gui/aura_icon/positive_component.png");
    private static final ResourceLocation AURA_NEGATIVE_COMPONENT = Fantazia.res("textures/gui/aura_icon/negative_component.png");
    private static final ResourceLocation AURA_MIXED_COMPONENT = Fantazia.res("textures/gui/aura_icon/mixed_component.png");
    public static final ResourceLocation BARS = Fantazia.res("textures/gui/bars.png");
    // mana bar stuff
    public static final ResourceLocation MANA_FRAME = Fantazia.res("textures/gui/ftz_gui/mana/mana_frame.png");
    public static final ResourceLocation MANA_FRAME_SIDE = Fantazia.res("textures/gui/ftz_gui/mana/mana_frame_side.png");
    public static final ResourceLocation MANA_BG = Fantazia.res("textures/gui/ftz_gui/mana/mana_background.png");
    public static final ResourceLocation MANA_ICON_EMPTY = Fantazia.res("textures/gui/ftz_gui/mana/mana_icon_empty.png");
    public static final ResourceLocation MANA_ICON = Fantazia.res("textures/gui/ftz_gui/mana/mana_icon.png");
    public static final ResourceLocation MANA_ICON_HALF = Fantazia.res("textures/gui/ftz_gui/mana/mana_icon_half.png");
    // stamina bar stuff

    public static final ResourceLocation STAMINA_FRAME = Fantazia.res("textures/gui/ftz_gui/stamina/stamina_frame.png");
    public static final ResourceLocation STAMINA_FRAME_SIDE = Fantazia.res("textures/gui/ftz_gui/stamina/stamina_frame_side.png");
    public static final ResourceLocation STAMINA_BG = Fantazia.res("textures/gui/ftz_gui/stamina/stamina_background.png");
    public static final ResourceLocation STAMINA_ICON_EMPTY = Fantazia.res("textures/gui/ftz_gui/stamina/stamina_icon_empty.png");
    public static final ResourceLocation STAMINA_ICON = Fantazia.res("textures/gui/ftz_gui/stamina/stamina_icon.png");
    public static final ResourceLocation STAMINA_ICON_HALF = Fantazia.res("textures/gui/ftz_gui/stamina/stamina_icon_half.png");

    // dash ability stuff
    private static final ResourceLocation DASH1_EMPTY = Fantazia.res("textures/gui/talents/dash/dash1_empty.png");
    private static final ResourceLocation DASH2_EMPTY = Fantazia.res("textures/gui/talents/dash/dash2_empty.png");
    private static final ResourceLocation DASH3_EMPTY = Fantazia.res("textures/gui/talents/dash/dash3_empty.png");
    private static final ResourceLocation DASH1 = Fantazia.res("textures/gui/talents/dash/dash1.png");
    private static final ResourceLocation DASH2 = Fantazia.res("textures/gui/talents/dash/dash2.png");
    private static final ResourceLocation DASH3 = Fantazia.res("textures/gui/talents/dash/dash3.png");
    // double jump stuff
    private static final ResourceLocation DOUBLE_JUMP = Fantazia.res("textures/gui/talents/double_jump/double_jump.png");
    private static final ResourceLocation DOUBLE_JUMP_EMPTY = Fantazia.res("textures/gui/talents/double_jump/double_jump_empty.png");
    // layered barrier stuff
    private static final ResourceLocation LAYERS = Fantazia.res("textures/gui/ftz_gui/layers.png");
    public static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    public static boolean renderStunBar(@Nullable StunEffect stunEffect, GuiGraphics guiGraphics, int x, int y) {
        if (stunEffect == null) return false;
        if (stunEffect.stunned()) {
            int stunPercent = (int) ((float) stunEffect.getDur() / (float) stunEffect.getInitDur() * 182);
            guiGraphics.blit(FantazicGui.BARS, x, y, 0, 10f, 182, 5, 182, 182);
            guiGraphics.blit(FantazicGui.BARS, x, y, 0, 0, 15F, stunPercent, 5, 182, 182);
        } else if (stunEffect.hasPoints()) {
            int stunPercent = (int) ((float) stunEffect.getPoints() / (float) stunEffect.getMaxPoints() * 182);
            guiGraphics.blit(FantazicGui.BARS, x, y, 0, 0F, 182, 5, 182, 182);
            guiGraphics.blit(FantazicGui.BARS, x, y, 0, 0, 5F, stunPercent, 5, 182, 182);
        }
        return true;
    }
    public static boolean renderBarrierBar(@Nullable BarrierEffect barrierEffect, GuiGraphics guiGraphics, int x, int y) {
        if (barrierEffect == null || !barrierEffect.hasBarrier()) return false;
        int percent = (int) (barrierEffect.getHealth() / barrierEffect.getInitial() * 182);
        guiGraphics.blit(FantazicGui.BARS, x, y, 0, 40F, 182, 5, 182, 182);
        guiGraphics.blit(FantazicGui.BARS, x, y, 0, 0, 45F, percent, 5, 182, 182);
        return true;
    }
    public static void renderAurasInventory(GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        LevelCap levelCap = LevelCapGetter.getLevelCap(level);
        if (player == null || levelCap == null) return;
        Screen screen = Minecraft.getInstance().screen;
        int imgWDT;
        int imgHGT;
        if (screen instanceof InventoryScreen) {
            imgWDT = 176;
            imgHGT = 166;
        }
        else if (screen instanceof CreativeModeInventoryScreen) {
            imgWDT = 195;
            imgHGT = 136;
        }
        else return;
        int leftPos = (screen.width - imgWDT) / 2;
        int x = leftPos - 82;
        int topPos = (screen.height - imgHGT) / 2;

        List<AuraInstance<Entity, Entity>> auraInstances = new ArrayList<>(levelCap.getAuraInstances().stream().toList());
        if (Fantazia.DEVELOPER_MODE) guiGraphics.drawString(font, "All level auras: " + auraInstances.size(), 0,160,16777215);
        auraInstances.removeIf(auraInstance -> auraInstance.notInside(player));
        if (Fantazia.DEVELOPER_MODE) guiGraphics.drawString(font, "Auras with player: " + auraInstances.size(), 0,170,16777215);
        if (auraInstances.isEmpty()) return;
        List<AuraInstance<Entity, Entity>> uniqueAuras = AuraHelper.sortUniqueAura(auraInstances, player);
        if (Fantazia.DEVELOPER_MODE) guiGraphics.drawString(font, "Affecting auras: " + uniqueAuras.size(), 0, 180, 16777215);
        for (int i = 0; i < Math.min(uniqueAuras.size(), 9); i++) {
            AuraInstance<Entity, Entity> instance = uniqueAuras.get(i);
            BasicAura<Entity,Entity> aura = instance.getAura();

            int y = topPos + i * 22;
            ResourceLocation location = switch (aura.getType()) {
                case POSITIVE -> AURA_POSITIVE_COMPONENT;
                case NEGATIVE -> AURA_NEGATIVE_COMPONENT;
                case MIXED -> AURA_MIXED_COMPONENT;
            };
            guiGraphics.blit(location, x, y, 0,0,80,20,80,20);
            ResourceLocation icon = aura.getIcon();
            Component name = aura.getAuraComponent();
            int length = font.width(name);
            if (!instance.getAura().secondary(player, instance.getOwner())) RenderSystem.setShaderColor(0.65f,0.65f,0.65f,0.65f);
            guiGraphics.blit(icon, x + 2, y + 2, 0,0,16,16,16,16);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            if (length > 60) guiGraphics.drawString(Minecraft.getInstance().font, name,x + 20,y + 6,0);
            else guiGraphics.drawCenteredString(Minecraft.getInstance().font, name,x + 50,y + 6,0);

            if (!aura.buildIconTooltip().isEmpty()) {
                int mouseX = (int)(Minecraft.getInstance().mouseHandler.xpos() * (double)guiGraphics.guiWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth());
                int mouseY = (int)(Minecraft.getInstance().mouseHandler.ypos() * (double)guiGraphics.guiHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight());
                if (FantazicMath.withinClamp(x,x + 80, mouseX) && FantazicMath.withinClamp(y,y + 20, mouseY)) guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, aura.buildIconTooltip(), mouseX, mouseY);
            }
        }
    }
    public static int renderMana(@NotNull ManaData manaData, GuiGraphics guiGraphics, int x0, int y0) {
        int maxMana = Mth.ceil(manaData.getMaxMana() / 2);

        int fullMaxRows = (int) ((float) maxMana / 10);
        boolean flag1 = maxMana % 10 > 0;
        int maxRows = flag1 ? fullMaxRows + 1 : fullMaxRows;

        int frHGT = -3 + 8 * maxRows;

        guiGraphics.setColor(1f, 1f,1f,0.5f);
        guiGraphics.blit(MANA_BG, x0 + 3, y0 - 11 - frHGT, 0,0, 85, frHGT + 8, 128,128);
        guiGraphics.setColor(1f, 1f,1f,1f);

        guiGraphics.blit(MANA_FRAME, x0, y0 - 8, 0,8,91,8,91,16);
        guiGraphics.blit(MANA_FRAME_SIDE, x0, y0 - 6 - frHGT, 0,0, 91, frHGT, 91, 16);
        guiGraphics.blit(MANA_FRAME, x0, y0 - 14 - frHGT, 0,0, 91, 8, 91, 16);

        int pX = x0 + 5;
        int pY = y0 - 14;

        for (int i = maxMana - 1; i >= 0; i--) {
            int j1 = i / 10;
            int k1 = i % 10;
            int l1 = pX + k1 * 8;
            int i2 = pY - j1 * 8;
            guiGraphics.blit(MANA_ICON_EMPTY, l1, i2,0,0,9,9,9,9);

            int j2 = i * 2;

            if (j2 < manaData.getMana()) {
                ResourceLocation location = j2 + 1 == Mth.ceil(manaData.getMana()) ? MANA_ICON_HALF : MANA_ICON;
                guiGraphics.blit(location, l1, i2, 0,0,9,9,9,9);
            }
        }
        return y0 - 35 - frHGT;
    }
    public static int renderStamina(@NotNull StaminaData staminaData, GuiGraphics guiGraphics, int x0, int y0) {
        int maxStamina = Mth.ceil(staminaData.getMaxStamina() / 2);

        int fullMaxRows = (int) ((float) maxStamina / 10);
        boolean flag1 = maxStamina % 10 > 0;
        int maxRows = flag1 ? fullMaxRows + 1 : fullMaxRows;

        int frHGT = -3 + 8 * maxRows;

        guiGraphics.setColor(1f, 1f,1f,0.5f);
        guiGraphics.blit(STAMINA_BG, x0 + 3, y0 - 11 - frHGT, 0,0, 85, frHGT + 8, 128,128);
        guiGraphics.setColor(1f, 1f,1f,1f);

        guiGraphics.blit(STAMINA_FRAME, x0, y0 - 8, 0,8,91,8,91,16);
        guiGraphics.blit(STAMINA_FRAME_SIDE, x0, y0 - 6 - frHGT, 0,0, 91, frHGT, 91, 16);
        guiGraphics.blit(STAMINA_FRAME, x0, y0 - 14 - frHGT, 0,0, 91, 8, 91, 16);

        int pX = x0 + 5;
        int pY = y0 - 14;

        for (int i = maxStamina - 1; i >= 0; i--) {
            int j1 = i / 10;
            int k1 = i % 10;
            int l1 = pX + k1 * 8;
            int i2 = pY - j1 * 8;
            guiGraphics.blit(STAMINA_ICON_EMPTY, l1, i2,0,0,9,9,9,9);

            int j2 = i * 2;

            if (j2 < staminaData.getStamina()) {
                ResourceLocation location = j2 + 1 == Mth.ceil(staminaData.getStamina()) ? STAMINA_ICON_HALF : STAMINA_ICON;
                guiGraphics.blit(location, l1, i2, 0,0,9,9,9,9);
            }
        }
        return y0 - frHGT - 35;
    }
    public static int renderDashIcon(@NotNull Dash dash, GuiGraphics guiGraphics, int x0, int y0) {
        final int dashIconSize = 20;
        ResourceLocation emptyIcon = switch (dash.getLevel()) {
            default -> null;
            case 1 -> DASH1_EMPTY;
            case 2 -> DASH2_EMPTY;
            case 3 -> DASH3_EMPTY;
        };
        ResourceLocation dashIcon = switch (dash.getLevel()) {
            default -> null;
            case 1 -> DASH1;
            case 2 -> DASH2;
            case 3 -> DASH3;
        };
        if (dashIcon == null || emptyIcon == null) return x0;

        int filling;
        if (dash.isDashing()) filling = 20 - (int) ((float) dash.getDur() / (float) dash.getInitDur() * 20);
        else filling = (int) ((float) dash.getRecharge() / (float) dash.getInitRecharge() * 20);

        guiGraphics.blit(emptyIcon, x0, y0, 0, 0, dashIconSize, dashIconSize, dashIconSize, dashIconSize);
        guiGraphics.blit(dashIcon, x0, y0, 0, 0, dashIconSize - filling, dashIconSize, dashIconSize, dashIconSize);
        return x0 + 23;
    }
    public static void renderDoubleJumpIcon(@NotNull DoubleJump doubleJump, GuiGraphics guiGraphics, int x0, int y0) {
        ResourceLocation icon = doubleJump.canJump() ? DOUBLE_JUMP : DOUBLE_JUMP_EMPTY;
        guiGraphics.blit(icon, x0, y0, 0,0,20,20,20,20);
    }
    public static void renderBarrierLayers(@NotNull LayeredBarrierEffect layeredBarrierEffect, GuiGraphics guiGraphics, int x0, int y0) {
        RenderSystem.setShaderTexture(0, LAYERS);
        String amo = "×" + layeredBarrierEffect.getLayers();
        int offset = Minecraft.getInstance().options.mainHand().get() == HumanoidArm.RIGHT ? 92 : -120;
        guiGraphics.blit(LAYERS, x0 + offset, y0,0,0,9,9,9,9);
        guiGraphics.drawString(Minecraft.getInstance().font, amo, x0 + 10 + offset, y0 + 1, 8780799, true);
    }
}
