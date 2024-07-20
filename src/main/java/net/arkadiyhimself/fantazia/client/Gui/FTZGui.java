package net.arkadiyhimself.fantazia.client.Gui;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Auras.AuraInstance;
import net.arkadiyhimself.fantazia.AdvancedMechanics.Auras.BasicAura;
import net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager.CleanseStrength;
import net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager.Cleansing;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.api.ItemRegistry;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.*;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.CommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.LayeredBarrierEffect;
import net.arkadiyhimself.fantazia.util.Capability.Level.LevelCap;
import net.arkadiyhimself.fantazia.util.Capability.Level.LevelCapGetter;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.GameRenderer;
import net.minecraft.client.renderer.texture.TextureAtlas;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.Material;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.food.FoodData;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.client.gui.overlay.IGuiOverlay;
import org.apache.commons.compress.utils.Lists;
import org.joml.Matrix4f;
import top.theillusivec4.curios.api.SlotResult;

import java.util.ArrayList;
import java.util.List;

public class FTZGui {
    private static final ResourceLocation DASH1 = Fantazia.res("textures/gui/dash/dash1.png");
    private static final ResourceLocation DASH2 = Fantazia.res("textures/gui/dash/dash2.png");
    private static final ResourceLocation DASH3 = Fantazia.res("textures/gui/dash/dash3.png");
    private static final ResourceLocation DJUMP = Fantazia.res("textures/gui/djump.png");
    private static final ResourceLocation BARRIER_LAYER = Fantazia.res("textures/gui/barrier_layers.png");
    private static final ResourceLocation NEWGUI = Fantazia.res("textures/gui/newgui.png");
    private static final ResourceLocation FTZHEALTH = Fantazia.res("textures/gui/combathealth.png");
    private static final ResourceLocation FTZMANA = Fantazia.res("textures/gui/combatmana.png");
    private static final ResourceLocation FTZSTAMINA = Fantazia.res("textures/gui/combatstamina.png");
    private static final ResourceLocation FTZHUNGER = Fantazia.res("textures/gui/combathunger.png");
    private static final ResourceLocation CURIOSLOT = Fantazia.res("textures/gui/curioslots/curioslot.png");
    private static final ResourceLocation SPELLCASTER = Fantazia.res("textures/gui/curioslots/spellcaster.png");
    private static final ResourceLocation PASSIVECASTER = Fantazia.res("textures/gui/curioslots/passivecaster.png");
    private static final ResourceLocation AURA_POSITIVE = Fantazia.res("textures/gui/aura_icon/positive.png");
    private static final ResourceLocation AURA_NEGATIVE = Fantazia.res("textures/gui/aura_icon/negative.png");
    private static final ResourceLocation AURA_MIXED = Fantazia.res("textures/gui/aura_icon/mixed.png");
    private static final ResourceLocation AURA_POSITIVE_COMPONENT = Fantazia.res("textures/gui/aura_icon/positive_component.png");
    private static final ResourceLocation AURA_NEGATIVE_COMPONENT = Fantazia.res("textures/gui/aura_icon/negative_component.png");
    private static final ResourceLocation AURA_MIXED_COMPONENT = Fantazia.res("textures/gui/aura_icon/mixed_component.png");
    private static final ResourceLocation VEINS = Fantazia.res("textures/misc/fury/veins.png");
    private static final ResourceLocation VEINS_BRIGHT = Fantazia.res("textures/misc/fury/veins_bright.png");
    private static final ResourceLocation FILLING = Fantazia.res("textures/misc/fury/filling.png");
    private static final ResourceLocation EDGES = Fantazia.res("textures/misc/fury/edges.png");
    public static final Material ANCIENT_FLAME_0 = new Material(TextureAtlas.LOCATION_BLOCKS, Fantazia.res("block/ancient_flame_0"));
    public static final Material ANCIENT_FLAME_1 = new Material(TextureAtlas.LOCATION_BLOCKS, Fantazia.res("block/ancient_flame_1"));

    // image sizes
    private static final int iconSize = 20;
    private static final int newguiX = 48;
    private static final int newguiY = 48;
    private static final int healthX = 40;
    private static final int healthY = 40;
    public static final IGuiOverlay DASH_ICON = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        Dash dash = abilityManager.takeAbility(Dash.class);
        if (dash == null || dash.getLevel() <= 0) return;
        ResourceLocation dashIcon = switch (dash.getLevel()) {
            default -> null;
            case 1 -> DASH1;
            case 2 -> DASH2;
            case 3 -> DASH3;
        };

        if (dashIcon == null) { return; }

        int percent;
        int xPlus = player.getMainArm() == HumanoidArm.RIGHT ? 95 : -95;
        int x = 10;
        int y = 40;
        if (dash.isDashing()) {
            percent = 20 - (int) ((float) dash.getDur() / (float) dash.getInitDur() * 20);
        } else {
            percent = (int) ((float) dash.getRech() / (float) dash.getInitRech() * 20);
        }
        RenderSystem.setShaderTexture(0, dashIcon);
        guiGraphics.blit(dashIcon, x, y, 0, 0, iconSize, iconSize, iconSize * 2, iconSize);
        guiGraphics.blit(dashIcon, x, y, 20, 0, iconSize - percent, iconSize, iconSize * 2, iconSize);
        RenderSystem.setShaderColor(1f,1f,1f,1f);
    });
    public static final IGuiOverlay DOUBLE_JUMP_ICON = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        DoubleJump doubleJump = abilityManager.takeAbility(DoubleJump.class);
        if (doubleJump == null || !doubleJump.isUnlocked()) return;
        int x = 40;
        int y = 40;
        RenderSystem.setShaderTexture(0, DJUMP);
        int offSet = doubleJump.canJump() ? 0 : 20;

        guiGraphics.blit(DJUMP, x, y, offSet,0,20,20,40,20);
    }));
    public static final IGuiOverlay BARRIER_LAYERS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        EffectManager effectManager = EffectGetter.getUnwrap(player);
        if (effectManager == null) return;
        LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
        if (layeredBarrierEffect == null || !layeredBarrierEffect.hasBarrier()) return;
        int amount = layeredBarrierEffect.getLayers();
        int x = 2;
        int y = Minecraft.getInstance().getWindow().getGuiScaledHeight() - 12;
        RenderSystem.setShaderTexture(0, BARRIER_LAYER);
        for (int i = 0; i < amount; i++) {
            guiGraphics.blit(BARRIER_LAYER, x,y - (i * 9),0,0,8,8,8,8);
        }
    });
    public static final IGuiOverlay FTZ_GUI = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Player player = Minecraft.getInstance().player;
        if (player == null || player.isSpectator() || player.isCreative()) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;

        // draw health bar
        float maxHP = player.getMaxHealth();
        float curHP = player.getHealth();
        float perHP = curHP / maxHP;
        int widthHP = Math.min((int) maxHP * 2, 320);
        RenderSystem.setShaderTexture(0, NEWGUI);
        guiGraphics.blit(NEWGUI,4,4,0,0,42,32, newguiX, newguiY);
        guiGraphics.blit(NEWGUI,46,7,0,33, widthHP,6,newguiX * widthHP / 40, newguiY);
        guiGraphics.blit(NEWGUI,46 + widthHP,6,40,32,7,8, newguiX, newguiY);

        int offY = 0;
        if (player.hasEffect(MobEffects.POISON)) offY = 8;
        if (player.hasEffect(MobEffects.WITHER)) offY = 16;
        if (player.isFreezing()) offY = 24;
        if (player.hasEffect(MobEffectRegistry.ABSOLUTE_BARRIER.get())) offY = 32;
        guiGraphics.blit(FTZHEALTH,45,8,0,offY + 4,widthHP,4,widthHP,40);
        guiGraphics.blit(FTZHEALTH,45,8,0, offY, (int) (widthHP * perHP),4,widthHP,40);

        // draw mana bar
        ManaData manaData = abilityManager.takeAbility(ManaData.class);
        if (manaData != null) {
            float maxMN = manaData.getMaxMana();
            float curMN = manaData.getMana();
            float perMN = curMN / maxMN;
            int widthMN = Math.min((int) maxMN * 2, 320);

            guiGraphics.blit(NEWGUI,46,17,0,33, widthMN,6, newguiX * widthMN / 40, newguiY);
            guiGraphics.blit(NEWGUI,46 + widthMN,16,40,32,7,8,newguiX,newguiY);

            offY = manaData.hasStone() ? 8 : 0;
            guiGraphics.blit(FTZMANA,45,18,0,offY + 4, widthMN,4, widthMN,40);
            guiGraphics.blit(FTZMANA, 45, 18, 0, offY, (int) (widthMN * perMN),4, widthMN,40);


        }
        // draw stamina bar
        StaminaData staminaData = abilityManager.takeAbility(StaminaData.class);
        if (staminaData != null) {
            float maxST = staminaData.getMaxStamina();
            float curST = staminaData.getStamina();
            float perST = curST / maxST;
            int widthST = Math.min((int) maxST * 2, 320);

            guiGraphics.blit(NEWGUI,46,27,0,33, widthST,6, newguiX * widthST / 40, newguiY);
            guiGraphics.blit(NEWGUI,46 + widthST,26,40,32,7,8,newguiX,newguiY);

            offY = 0;
            guiGraphics.blit(FTZSTAMINA,45,28,0,offY + 4, widthST,4, widthST,40);
            guiGraphics.blit(FTZSTAMINA, 45, 28, 0, offY, (int) (widthST * perST),4, widthST,40);
        }

        FoodData foodData = player.getFoodData();
        int num4 = 20;
        if (player.hasEffect(MobEffects.HUNGER)) { num4 = 40; }
        if (WhereMagicHappens.Abilities.hasCurio(player, ItemRegistry.SOUL_EATER.get())) { num4 = 60; }
        guiGraphics.blit(FTZHUNGER,10,10, num4-20,20,20,20,60,40);
        guiGraphics.blit(FTZHUNGER,30,30, num4,20,-20, -foodData.getFoodLevel(), 60, 40);
        RenderSystem.setShaderTexture(0, NEWGUI);

        EffectManager effectManager = EffectGetter.getUnwrap(player);
        if (effectManager == null) return;
        LayeredBarrierEffect layeredBarrierEffect = effectManager.takeEffect(LayeredBarrierEffect.class);
        if (layeredBarrierEffect == null || !layeredBarrierEffect.hasBarrier()) return;
        for (int i = 0; i < layeredBarrierEffect.getLayers(); i++) {
            guiGraphics.blit(NEWGUI, 52 + widthHP + i * 4, 6, 18, 40, 5, 8, newguiX, newguiY);
        }
    }));
    public static final IGuiOverlay CURIOSLOTS = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        Font font = gui.getFont();
        Screen screen = Minecraft.getInstance().screen;
        if (player == null || player.isCreative() || player.isSpectator()) return;
        if (screen != null) return;
        int x = 10;
        int y = 80;
        List<SlotResult> spellcasters = WhereMagicHappens.Abilities.findCurios(player, "spellcaster");
        List<SlotResult> passivecasters = WhereMagicHappens.Abilities.findCurios(player, "passivecaster");

        int num1 = spellcasters.size();
        int num2 = passivecasters.size();

        int allSlots = num1 + num2;
        for (int i = 0; i < allSlots; i++) {
            guiGraphics.blit(CURIOSLOT, x,y + i * 20,0,0,20,20,20,20);
        }
        for (int j = 0; j < num1; j++) {
            ItemStack item = spellcasters.get(j).stack();
            if (item.isEmpty()) {
                guiGraphics.blit(SPELLCASTER, x,y + j * 20,0,0,20,20,20,20);
            } else {
                guiGraphics.renderItem(item,x + 2,y + j * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + j * 20 + 2);
            }
        }
        for (int k = 0; k < num2; k++) {
            ItemStack item = passivecasters.get(k).stack();
            if (item.isEmpty()) {
                guiGraphics.blit(PASSIVECASTER, x,y + k * 20 + num1 * 20,0,0,20,20,20,20);
            } else {
                guiGraphics.renderItem(item, x + 2, y + k * 20 + num1 * 20 + 2);
                guiGraphics.renderItemDecorations(font, item,x + 2,y + 2 + 20 * (num1 + k));
            }
        }
    }));
    public static final IGuiOverlay ANCIENT_BURNING = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        CommonData data = AttachCommonData.getUnwrap(player);
        if (player == null || data == null || !data.isAncientBurning()) { return; }
        BufferBuilder bufferbuilder = Tesselator.getInstance().getBuilder();
        RenderSystem.setShader(GameRenderer::getPositionTexShader);
        RenderSystem.depthFunc(519);
        RenderSystem.depthMask(false);
        RenderSystem.enableBlend();
        TextureAtlasSprite textureAtlasSprite = ANCIENT_FLAME_1.sprite();
        RenderSystem.setShaderTexture(0, textureAtlasSprite.atlasLocation());

        PoseStack poseStack = guiGraphics.pose();
        poseStack.setIdentity();
        float pX1 = -0.85f * screenHeight;
        float pX2 = 0.85f * screenHeight;
        float pY1 = -0.85f * screenHeight;
        float pY2 = 0.65f * screenHeight;

        float u0 = textureAtlasSprite.getU0();
        float u1 = textureAtlasSprite.getU1();
        float u01 = (u0 + u1) / 2.0F;
        float v0 = textureAtlasSprite.getV0();
        float v1 = textureAtlasSprite.getV1();
        float v01 = (v0 + v1) / 2.0F;
        float ratio = textureAtlasSprite.uvShrinkRatio();
        float x1 = Mth.lerp(ratio, u0, u01);
        float x2 = Mth.lerp(ratio, u1, u01);
        float y1 = Mth.lerp(ratio, v0, v01);
        float y2 = Mth.lerp(ratio, v1, v01);
        poseStack.pushPose();
        poseStack.translate((float) screenWidth / 2, (float) screenHeight * 1, 0);
        for (int i = 0; i < 2; i++) {
            poseStack.pushPose();
            poseStack.translate((float)(-(i * 2 - 1)) * screenWidth * 0.25f, 0, 0.0F);
            poseStack.mulPose(Axis.YN.rotationDegrees(45f));
            Matrix4f matrix4f = poseStack.last().pose();
            bufferbuilder.begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX);
            bufferbuilder.vertex(matrix4f, pX1, pY1, 0f).uv(x1, y1).endVertex();
            bufferbuilder.vertex(matrix4f, pX1, pY2, 0f).uv(x1, y2).endVertex();
            bufferbuilder.vertex(matrix4f, pX2, pY2, 0f).uv(x2, y2).endVertex();
            bufferbuilder.vertex(matrix4f, pX2, pY1, 0f).uv(x2, y1).endVertex();
            BufferUploader.drawWithShader(bufferbuilder.end());
            poseStack.popPose();
        }
        poseStack.popPose();
        RenderSystem.disableBlend();
        RenderSystem.depthMask(true);
        RenderSystem.depthFunc(515);
    }));
    public static final IGuiOverlay AURAS = ((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        LocalPlayer player = Minecraft.getInstance().player;
        CommonData data = AttachCommonData.getUnwrap(player);
        if (player == null || data == null) return;
        if (Minecraft.getInstance().screen != null) return;
        List<AuraInstance<Entity, Entity>> auras = WhereMagicHappens.Abilities.getAffectingAuras(player);
        for (int i = 0; i < Math.min(auras.size(), 6); i++) {
            AuraInstance<Entity, Entity> auraInstance = auras.get(i);
            BasicAura<Entity, Entity> aura = auraInstance.getAura();
            int x = 2 + i * 22;
            int y = screenHeight - 22;
            ResourceLocation location = switch (aura.getType()) {
                case POSITIVE -> AURA_POSITIVE;
                case NEGATIVE -> AURA_NEGATIVE;
                case MIXED -> AURA_MIXED;
            };
            guiGraphics.blit(location, x, y, 0,0,20,20,20,20);
            ResourceLocation icon = aura.getIcon();
            if (!aura.secondaryFilter.test(player, auraInstance.getOwner())) RenderSystem.setShaderColor(0.65f,0.65f,0.65f,0.65f);
            guiGraphics.blit(icon, x + 2, y + 2, 0,0,16,16,16,16);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
        }
    });
    public static IGuiOverlay DEVELOPER_MODE = (((gui, guiGraphics, partialTick, screenWidth, screenHeight) -> {
        Font font = Minecraft.getInstance().font;
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null || !WhereMagicHappens.DEVELOPER_MODE) return;
        int widthText = font.width("DEVELOPER MODE");
        guiGraphics.drawString(font, "DEVELOPER MODE", screenWidth - widthText,0,0);

        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        RenderingValues renderingValues = abilityManager.takeAbility(RenderingValues.class);
        if (renderingValues != null) {
            double dX = renderingValues.deltaMovement.x();
            double dY = renderingValues.deltaMovement.y();
            double dZ = renderingValues.deltaMovement.z();
            guiGraphics.drawString(font, dX + " " + dY + " " + dZ, 0, 0,0);
        }
    }));
    public static void renderAurasInventory(GuiGraphics guiGraphics) {
        Font font = Minecraft.getInstance().font;
        LocalPlayer player = Minecraft.getInstance().player;
        ClientLevel level = Minecraft.getInstance().level;
        LevelCap levelCap = LevelCapGetter.getLevelCap(level);
        if (player == null || levelCap == null) return;

        List<AuraInstance<Entity, Entity>> auraInstances = new ArrayList<>(levelCap.getAuraInstances());
        if (WhereMagicHappens.DEVELOPER_MODE) guiGraphics.drawString(font, "All level auras: " + auraInstances.size(), 0,160,16777215);
        auraInstances.removeIf(auraInstance -> !auraInstance.isInside(player));
        if (WhereMagicHappens.DEVELOPER_MODE) guiGraphics.drawString(font, "Auras with player: " + auraInstances.size(), 0,170,16777215);
        if (auraInstances.isEmpty()) return;
        List<AuraInstance<Entity, Entity>> uniqueAuras = WhereMagicHappens.Abilities.getUniqueAuras(auraInstances, player);
        if (WhereMagicHappens.DEVELOPER_MODE) guiGraphics.drawString(font, "Affecting auras: " + uniqueAuras.size(), 0, 180, 16777215);
        for (int i = 0; i < Math.min(uniqueAuras.size(), 9); i++) {
            AuraInstance<Entity, Entity> instance = uniqueAuras.get(i);
            BasicAura<Entity,Entity> aura = instance.getAura();
            int x = 5;
            int y = 64 + i * 22;
            ResourceLocation location = switch (aura.getType()) {
                case POSITIVE -> AURA_POSITIVE_COMPONENT;
                case NEGATIVE -> AURA_NEGATIVE_COMPONENT;
                case MIXED -> AURA_MIXED_COMPONENT;
            };
            guiGraphics.blit(location, x, y, 0,0,80,20,80,20);
            ResourceLocation icon = aura.getIcon();
            Component name = aura.getAuraComponent();
            int length = font.width(name);
            if (!instance.getAura().secondaryFilter.test(player, instance.getOwner())) RenderSystem.setShaderColor(0.65f,0.65f,0.65f,0.65f);
            guiGraphics.blit(icon, x + 2, y + 2, 0,0,16,16,16,16);
            RenderSystem.setShaderColor(1f,1f,1f,1f);
            if (length > 60) {
                guiGraphics.drawString(Minecraft.getInstance().font, name,x + 20,y + 6,0);
            } else {
                guiGraphics.drawCenteredString(Minecraft.getInstance().font, name,x + 50,y + 6,0);
            }
            if (!aura.iconTooltip().isEmpty()) {
                int mouseX = (int)(Minecraft.getInstance().mouseHandler.xpos() * (double)guiGraphics.guiWidth() / (double)Minecraft.getInstance().getWindow().getScreenWidth());
                int mouseY = (int)(Minecraft.getInstance().mouseHandler.ypos() * (double)guiGraphics.guiHeight() / (double)Minecraft.getInstance().getWindow().getScreenHeight());
                if (WhereMagicHappens.Math.withinClamp(x,x + 80, mouseX) && WhereMagicHappens.Math.withinClamp(y,y + 20, mouseY)) {
                    guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, aura.iconTooltip(), mouseX, mouseY);
                }
            }
        }
    }
    public static void renderEffectTooltip(GuiGraphics guiGraphics, MobEffect effect, int mouseX, int mouseY) {
        Cleansing cleansing = CleanseStrength.getRequiredStrength(effect);
        Component clns = cleansing.getName();
        List<Component> components = Lists.newArrayList();
        components.addAll(buildTooltip(effect));
        WhereMagicHappens.Gui.addComponent(components, "tooltip.fantazia.common.cleanse", new ChatFormatting[]{ChatFormatting.GOLD}, null, clns);
        guiGraphics.renderComponentTooltip(Minecraft.getInstance().font, components, mouseX, mouseY);
    }
    public static List<Component> buildTooltip(MobEffect effect) {
        List<Component> components = Lists.newArrayList();
        String lines = Component.translatable(effect.getDescriptionId() + ".lines").getString();
        int amo;
        try {
            amo = Integer.parseInt(lines);
        } catch (NumberFormatException e) {
            return components;
        }
        for (int i = 1; i <= amo; i++) {
            String line = effect.getDescriptionId() + ".tooltip." + i;
            ChatFormatting color;
            if (i == 1) {
                color = switch (effect.getCategory()) {
                    case HARMFUL -> ChatFormatting.DARK_RED;
                    case NEUTRAL -> ChatFormatting.DARK_GRAY;
                    case BENEFICIAL -> ChatFormatting.BLUE;
                };
            } else color = switch (effect.getCategory()) {
                case HARMFUL -> ChatFormatting.RED;
                case NEUTRAL -> ChatFormatting.GRAY;
                case BENEFICIAL -> ChatFormatting.AQUA;
            };
            Component actualLine = i == 1 ? Component.translatable(line).withStyle(color, ChatFormatting.BOLD) : Component.translatable(line).withStyle(color);
            components.add(actualLine);
        }
        return components;
    }
    public static void renderFuryVeins() {
        LocalPlayer player = Minecraft.getInstance().player;
        if (player == null) return;
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return;
        RenderingValues renderingValues = abilityManager.takeAbility(RenderingValues.class);
        if (renderingValues == null) return;
        float veinTR = renderingValues.getVein() / 15;
        float allTR = renderingValues.getBack() / 20;
        WhereMagicHappens.Gui.renderOnTheWholeScreen(VEINS, 1.0F, 0, 0, 0.4F * allTR);
        WhereMagicHappens.Gui.renderOnTheWholeScreen(VEINS_BRIGHT, 1.0F, 0, 0, veinTR * allTR);
        WhereMagicHappens.Gui.renderOnTheWholeScreen(FILLING, 1.0F, 0, 0, 0.45F * allTR);
        WhereMagicHappens.Gui.renderOnTheWholeScreen(EDGES, 1.0F, 0, 0, 0.925F * allTR);
    }
}
