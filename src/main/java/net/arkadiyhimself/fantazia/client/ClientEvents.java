package net.arkadiyhimself.fantazia.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.fantazia.Items.MagicCasters.SpellCaster;
import net.arkadiyhimself.fantazia.Items.Weapons.Melee.FragileBlade;
import net.arkadiyhimself.fantazia.Items.Weapons.Melee.MeleeWeaponItem;
import net.arkadiyhimself.fantazia.Items.Weapons.Melee.Murasama;
import net.arkadiyhimself.fantazia.MobEffects.SimpleMobEffect;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Networking.packets.CapabilityUpdate.*;
import net.arkadiyhimself.fantazia.Networking.packets.KeyInputC2S.CastSpellC2S;
import net.arkadiyhimself.fantazia.api.MobEffectRegistry;
import net.arkadiyhimself.fantazia.api.SoundRegistry;
import net.arkadiyhimself.fantazia.client.Gui.FTZGui;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderAboveTypes.DeafeningType;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderAboveTypes.DisarmedSwordType;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderAboveTypes.SnowCrystalType;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderAboveTypes.StunBarType;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderLayer.AbsoluteBarrier;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderLayer.BarrierLayer;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderLayer.LayeredBarrierLayer;
import net.arkadiyhimself.fantazia.client.Models.Entity.VanillaTweaks.RenderLayer.MysticMirror;
import net.arkadiyhimself.fantazia.client.Screen.TalentsScreen;
import net.arkadiyhimself.fantazia.mixin.LivingEntityRendererAccessor;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.Dash;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.Abilities.DoubleJump;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.AttachCommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.CommonData.CommonData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectManager;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.BarrierEffect;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.Effects.StunEffect;
import net.arkadiyhimself.fantazia.util.Capability.Entity.TalentData.TalentData;
import net.arkadiyhimself.fantazia.util.Capability.Entity.TalentData.TalentGetter;
import net.arkadiyhimself.fantazia.util.KeyBinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.EffectRenderingInventoryScreen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.SwordItem;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.client.event.sound.PlaySoundEvent;
import net.minecraftforge.client.gui.overlay.VanillaGuiOverlay;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.RegistryObject;
import org.joml.Quaternionf;

import java.util.ArrayList;
import java.util.List;

public class ClientEvents {
    public static List<SoundEvent> nonMuted = new ArrayList<>(){{
        add(SoundEvents.GENERIC_EXPLODE);
        add(SoundEvents.WARDEN_SONIC_BOOM);
        add(SoundEvents.UI_TOAST_CHALLENGE_COMPLETE);
    }};
    public static ArrayList<RegistryObject<SoundEvent>> nonMutedMod = new ArrayList<>(){{
        add(SoundRegistry.HEART_BEAT);
        add(SoundRegistry.DASH1_RECH);
        add(SoundRegistry.DASH2_RECH);
        add(SoundRegistry.DASH3_RECH);
        add(SoundRegistry.RINGING);
        add(SoundRegistry.BLOODLUST_AMULET);
        add(SoundRegistry.FURY_DISPEL);
        add(SoundRegistry.FURY_PROLONG);
        add(SoundRegistry.DOOMED);
        add(SoundRegistry.DENIED);
    }};
    private static final ResourceLocation BARS = Fantazia.res("textures/gui/bars.png");
    private static void fireVertex(PoseStack.Pose pMatrixEntry, VertexConsumer pBuffer, float pX, float pY, float pZ, float pTexU, float pTexV) {
        pBuffer.vertex(pMatrixEntry.pose(), pX, pY, pZ).color(255, 255, 255, 255).uv(pTexU, pTexV).overlayCoords(0, 10).uv2(240).normal(pMatrixEntry.normal(), 0.0F, 1.0F, 0.0F).endVertex();
    }
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = Fantazia.MODID)
    public static class ClientEvent {
        @SubscribeEvent
        public static void furyFOV(ComputeFovModifierEvent event) {
            if (Minecraft.getInstance().player != null) {
                if (Minecraft.getInstance().player.hasEffect(MobEffectRegistry.FURY.get())) {
                    event.setNewFovModifier(event.getNewFovModifier() * 1.1f);
                }
            }
        }
        @SubscribeEvent
        public static void renderGui(RenderGuiOverlayEvent.Pre event) {
            Minecraft mc = Minecraft.getInstance();
            LocalPlayer player = mc.player;
            if (player == null) return;
            if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) event.setCanceled(true);
            if (event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()) event.setCanceled(true);
            if (player.hasEffect(MobEffectRegistry.FURY.get()) && event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) FTZGui.renderFuryVeins();

            if (event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type()) {
                EffectManager effectManager = EffectGetter.getUnwrap(player);
                if (effectManager == null) return;
                StunEffect stunEffect = effectManager.takeEffect(StunEffect.class);

                RenderSystem.setShaderTexture(0, BARS);
                int x = event.getWindow().getGuiScaledWidth() / 2 - 91;
                int y = event.getWindow().getGuiScaledHeight() - 29;

                if (stunEffect != null && stunEffect.renderBar()) {
                    if (stunEffect.stunned()) {
                        int stunPercent = (int) ((float) stunEffect.getDur() / (float) stunEffect.getInitDur() * 182);
                        event.getGuiGraphics().blit(BARS, x, y, 0, 10f, 182, 5, 182, 182);
                        event.getGuiGraphics().blit(BARS, x, y, 0, 0, 15F, stunPercent, 5, 182, 182);
                    } else if (stunEffect.hasPoints()) {
                        int stunPercent = (int) ((float) stunEffect.getPoints() / (float) stunEffect.getMaxPoints() * 182);
                        event.getGuiGraphics().blit(BARS, x, y, 0, 0F, 182, 5, 182, 182);
                        event.getGuiGraphics().blit(BARS, x, y, 0, 0, 5F, stunPercent, 5, 182, 182);
                    }
                    event.setCanceled(true);
                    return;
                }
                BarrierEffect barrierEffect = effectManager.takeEffect(BarrierEffect.class);
                if (barrierEffect != null && barrierEffect.hasBarrier()) {
                    int percent = (int) (barrierEffect.getHealth() / barrierEffect.getInitial() * 182);
                    event.setCanceled(true);
                    event.getGuiGraphics().blit(BARS, x, y, 0, 40F, 182, 5, 182, 182);
                    event.getGuiGraphics().blit(BARS, x, y, 0, 0, 45F, percent, 5, 182, 182);
                }
            }
        }
        @SubscribeEvent
        public static void renderBackground(ScreenEvent.Render.BackgroundRendered event) {
            int i = 0;
            if (event.getScreen() instanceof EffectRenderingInventoryScreen<?>) {

            }
            FTZGui.renderAurasInventory(event.getGuiGraphics());
        }

        @SubscribeEvent
        public static void renderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
            assert Minecraft.getInstance().player != null;
            LivingEntity entity = event.getEntity();
            PoseStack poseStack = event.getPoseStack();
            if (entity instanceof Player player) {
                AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
                if (abilityManager != null) {
                    Dash dash = abilityManager.takeAbility(Dash.class);
                    if (dash != null && dash.isDashing() && dash.getLevel() >= 3) {
                        event.setCanceled(true);
                    }
                }
            }

            boolean addedLayer = false;
            for (RenderLayer layer : ((LivingEntityRendererAccessor) event.getRenderer()).layers()) {
                if (layer instanceof BarrierLayer.LayerBarrier<?, ?>) {
                    addedLayer = true;
                    break;
                }
            }
            if (!addedLayer) {
                event.getRenderer().addLayer(new BarrierLayer.LayerBarrier<>(event.getRenderer()));
                event.getRenderer().addLayer(new LayeredBarrierLayer.LayerBarrier<>(event.getRenderer()));
                event.getRenderer().addLayer(new AbsoluteBarrier.LayerBarrier<>(event.getRenderer()));
                event.getRenderer().addLayer(new MysticMirror.LayerMirror<>(event.getRenderer()));
            }

            if (!event.getEntity().canChangeDimensions()) { return; }
            if (entity instanceof Player player) {
                if (player.isSpectator() || player.isCreative() || player == Minecraft.getInstance().player) { return; }
            }



            Quaternionf cameraOrientation = Minecraft.getInstance().getEntityRenderDispatcher().cameraOrientation();
            MultiBufferSource buffers = event.getMultiBufferSource();
            if (!entity.getPassengers().isEmpty()) {
                return;
            }
            CommonData commonData = AttachCommonData.getUnwrap(entity);
            int y0ffset = 0;
            if (commonData != null && commonData.isAncientBurning()) {
                poseStack.pushPose();
                TextureAtlasSprite textureatlassprite0 = FTZGui.ANCIENT_FLAME_0.sprite();
                TextureAtlasSprite textureatlassprite1 = FTZGui.ANCIENT_FLAME_1.sprite();
                float f = entity.getBbWidth() * 1.4F;
                poseStack.scale(f, f, f);
                float f1 = 0.5F;
                float f2 = 0.0F;
                float f3 = entity.getBbHeight() / f;
                float f4 = 0.0F;
                poseStack.mulPose(Axis.YP.rotationDegrees(-Minecraft.getInstance().getEntityRenderDispatcher().camera.getYRot()));
                poseStack.translate(0.0F, 0.0F, -0.3F + (float)((int)f3) * 0.02F);
                float f5 = 0.0F;
                int i = 0;
                VertexConsumer vertexconsumer = buffers.getBuffer(Sheets.cutoutBlockSheet());

                for(PoseStack.Pose posestack$pose = poseStack.last(); f3 > 0.0F; ++i) {
                    TextureAtlasSprite textureatlassprite2 = i % 2 == 0 ? textureatlassprite0 : textureatlassprite1;
                    float f6 = textureatlassprite2.getU0();
                    float f7 = textureatlassprite2.getV0();
                    float f8 = textureatlassprite2.getU1();
                    float f9 = textureatlassprite2.getV1();
                    if (i / 2 % 2 == 0) {
                        float f10 = f8;
                        f8 = f6;
                        f6 = f10;
                    }

                    fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 0.0F - f4, f5, f8, f9);
                    fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 0.0F - f4, f5, f6, f9);
                    fireVertex(posestack$pose, vertexconsumer, -f1 - 0.0F, 1.4F - f4, f5, f6, f7);
                    fireVertex(posestack$pose, vertexconsumer, f1 - 0.0F, 1.4F - f4, f5, f8, f7);
                    f3 -= 0.45F;
                    f4 -= 0.45F;
                    f1 *= 0.9F;
                    f5 += 0.03F;
                }
                poseStack.popPose();
            }
            EffectManager effectManager = EffectGetter.getUnwrap(entity);
            if (effectManager == null) return;
            StunEffect stunEffect = effectManager.takeEffect(StunEffect.class);
            poseStack.pushPose();
            final float globalScale = 0.0625F;
            poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
            poseStack.mulPose(cameraOrientation);
            final int light = 0xF000F0;
            poseStack.scale(-globalScale, -globalScale, -globalScale);
            if (stunEffect != null && stunEffect.renderBar()) {
                y0ffset = -8;
                VertexConsumer stunBar = buffers.getBuffer(StunBarType.BAR_TEXTURE_TYPE);

                float stunPercent;
                if (stunEffect.stunned()) {
                    stunPercent = (float) stunEffect.getDur() / (float) stunEffect.getInitDur();
                    int changingRed = stunEffect.getColor();

                    // empty bar
                    stunBar.vertex(poseStack.last().pose(), -16, -4, 0.003F).color(changingRed, 255, 255, 255).uv(0.0F, 0.5F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -16, 0, 0.003F).color(changingRed, 255, 255, 255).uv(0.0F, 0.75F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 0, 0.003F).color(changingRed, 255, 255, 255).uv(1.0F, 0.75F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, -4, 0.003F).color(changingRed, 255, 255, 255).uv(1.0F, 0.5F).uv2(light).endVertex();

                    // filling
                    stunBar.vertex(poseStack.last().pose(), -14, -4, 0.004F).color(255, 255, 255, 255).uv(0.0F, 0.75F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14, 0, 0.004F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 0, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 1.0F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, -4, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 0.75F).uv2(light).endVertex();
                } else if (stunEffect.hasPoints()) {
                    stunPercent = (float) stunEffect.getPoints() / (float) stunEffect.getMaxPoints();

                    // empty bar
                    stunBar.vertex(poseStack.last().pose(), -16, 0, 0.003F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -16, 4, 0.003F).color(255, 255, 255, 255).uv(0.0F, 0.25F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 4, 0.003F).color(255, 255, 255, 255).uv(1.0F, 0.25F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 0, 0.003F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();

                    // filling
                    stunBar.vertex(poseStack.last().pose(), -14, 0, 0.004F).color(255, 255, 255, 255).uv(0.0F, 0.25F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14, 4, 0.004F).color(255, 255, 255, 255).uv(0.0F, 0.5F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 4, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 0.5F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 0, 0.004F).color(255, 255, 255, 255).uv(stunPercent, 0.25F).uv2(light).endVertex();
                }
            }
            if (commonData != null) {
                int tick = commonData.tick;
                if (commonData.renderDisarm()) {
                    int iconHeight = -35 + y0ffset;
                    VertexConsumer disarmType = buffers.getBuffer(DisarmedSwordType.BROKEN_SWORD_TYPE);
                    disarmType.vertex(poseStack.last().pose(), -10.0F, (float) iconHeight, 0).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
                    disarmType.vertex(poseStack.last().pose(), -10.0F, 20 + (float) iconHeight, 0).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
                    disarmType.vertex(poseStack.last().pose(), 10.0F, 20 + (float) iconHeight, 0).color(255, 255, 255, 255).uv(1.0F, 1.0F).uv2(light).endVertex();
                    disarmType.vertex(poseStack.last().pose(), 10.0F, (float) iconHeight, 0).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();
                } else if (commonData.renderFreeze()) {
                    VertexConsumer freezeType = buffers.getBuffer(SnowCrystalType.SNOW_CRYSTAL_TYPE);
                    if (commonData.freezeEffectPercent() > 0) {
                        int alpha = (int) Math.ceil(commonData.freezeEffectPercent() * 255);
                        freezeType.vertex(poseStack.last().pose(), -8, y0ffset, 0).color(255, 255, 255, alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
                        freezeType.vertex(poseStack.last().pose(), -8, 16 + y0ffset, 0).color(255, 255, 255, alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
                        freezeType.vertex(poseStack.last().pose(), 8, 16 + y0ffset, 0).color(255, 255, 255, alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
                        freezeType.vertex(poseStack.last().pose(), 8, y0ffset, 0).color(255, 255, 255, alpha).uv(1.0F, 0.0F).uv2(light).endVertex();
                    } else {
                        int freezePercent = (int) (entity.getPercentFrozen() * 235);
                        freezeType.vertex(poseStack.last().pose(), -8, y0ffset, 0).color(255, 255, 255, 20 + freezePercent).uv(0.0F, 0.0F).uv2(light).endVertex();
                        freezeType.vertex(poseStack.last().pose(), -8, 16 + y0ffset, 0).color(255, 255, 255, 20 + freezePercent).uv(0.0F, 1.0F).uv2(light).endVertex();
                        freezeType.vertex(poseStack.last().pose(), 8, 16 + y0ffset, 0).color(255, 255, 255, 20 + freezePercent).uv(1.0F, 1.0F).uv2(light).endVertex();
                        freezeType.vertex(poseStack.last().pose(), 8, y0ffset, 0).color(255, 255, 255, 20 + freezePercent).uv(1.0F, 0.0F).uv2(light).endVertex();

                    }
                } else if (commonData.isDeaf() && SimpleMobEffect.affectedByDeafening.contains(entity.getType())) {
                    float alpha = 155 + 100f / commonData.jumpingTick;
                    int innerFrames = tick % 14 + 1;

                    VertexConsumer innerWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(innerFrames, "inner"));
                    innerWave.vertex(poseStack.last().pose(), -4.0F, -4, 0).color(255, 255, 255, (int) alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
                    innerWave.vertex(poseStack.last().pose(), -4.0F, 4, 0).color(255, 255, 255, (int) alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
                    innerWave.vertex(poseStack.last().pose(), 4.0F, 4, 0).color(255, 255, 255, (int) alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
                    innerWave.vertex(poseStack.last().pose(), 4.0F, -4, 0).color(255, 255, 255, (int) alpha).uv(1.0F, 0.0F).uv2(light).endVertex();

                    int middleFrames = tick % 21 + 1;
                    VertexConsumer middleWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(middleFrames, "middle"));
                    middleWave.vertex(poseStack.last().pose(), -8.0F, -8, 0).color(255, 255, 255, (int) alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
                    middleWave.vertex(poseStack.last().pose(), -8.0F, 8, 0).color(255, 255, 255, (int) alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
                    middleWave.vertex(poseStack.last().pose(), 8.0F, 8, 0).color(255, 255, 255, (int) alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
                    middleWave.vertex(poseStack.last().pose(), 8.0F, -8, 0).color(255, 255, 255, (int) alpha).uv(1.0F, 0.0F).uv2(light).endVertex();

                    int outerFrames = tick % 28 + 1;
                    VertexConsumer outerWave = buffers.getBuffer(DeafeningType.SOUND_WAVE_TYPE(outerFrames, "outer"));
                    outerWave.vertex(poseStack.last().pose(), -12.0F, -12, 0).color(255, 255, 255, (int) alpha).uv(0.0F, 0.0F).uv2(light).endVertex();
                    outerWave.vertex(poseStack.last().pose(), -12.0F, 12, 0).color(255, 255, 255, (int) alpha).uv(0.0F, 1.0F).uv2(light).endVertex();
                    outerWave.vertex(poseStack.last().pose(), 12.0F, 12, 0).color(255, 255, 255, (int) alpha).uv(1.0F, 1.0F).uv2(light).endVertex();
                    outerWave.vertex(poseStack.last().pose(), 12.0F, -12, 0).color(255, 255, 255, (int) alpha).uv(1.0F, 0.0F).uv2(light).endVertex();
                }
            }
            poseStack.popPose();
        }
        @SubscribeEvent
        public static void input(InputEvent event) {
            if (WhereMagicHappens.Abilities.canNotDoActions(Minecraft.getInstance().player)) { return; }
            Player player;
            if ((player = Minecraft.getInstance().player) != null) {
                if (KeyBinding.BLOCK.consumeClick() && (player.getMainHandItem().getItem() instanceof SwordItem ||
                        player.getMainHandItem().getItem() instanceof MeleeWeaponItem)) {
                    NetworkHandler.sendToServer(new StartedBlockingC2S());
                }
            }
        }
        @SubscribeEvent
        public static void mouseScrolling(InputEvent.MouseScrollingEvent event) {
            if (Minecraft.getInstance().level != null) {
                LocalPlayer player = Minecraft.getInstance().player;
                EffectManager effectManager = EffectGetter.getUnwrap(player);
                if (effectManager != null) {
                    StunEffect stunEffect = effectManager.takeEffect(StunEffect.class);
                    if (stunEffect != null && stunEffect.stunned()) event.setCanceled(true);
                }
                if (!event.isCanceled()) {
                    WhereMagicHappens.Abilities.animatePlayer(player, (String) null);
                }

            }
        }
        @SubscribeEvent
        public static void clientTick(TickEvent.ClientTickEvent event) {
            LocalPlayer player = Minecraft.getInstance().player;
            if (player != null) {
                Vec3 velocity = player.getDeltaMovement();
                NetworkHandler.sendToServer(new DeltaMovementC2S(velocity));
            }
        }
        @SubscribeEvent
        public static void mouseInputs(InputEvent.InteractionKeyMappingTriggered event) {
            Player player = Minecraft.getInstance().player;
            if (player != null) {
                if (WhereMagicHappens.Abilities.canNotDoActions(player)) {
                    event.setCanceled(true);
                    event.setSwingHand(false);
                }
            }
        }
        @SubscribeEvent
        public static void keyInput(InputEvent.Key event) {
            Player player = Minecraft.getInstance().player;
            if (player == null) return;
            AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
            if (abilityManager == null) return;
            if (WhereMagicHappens.Abilities.canNotDoActions(player)) { return; }

            if (KeyBinding.DASH.consumeClick()) {
                NetworkHandler.sendToServer(new StartDashC2S());
            }
            if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue()) {
                DoubleJump doubleJump = abilityManager.takeAbility(DoubleJump.class);
                if (doubleJump == null) return;
                if (event.getAction() == 0) {
                    NetworkHandler.sendToServer(new DoubleJumpTickC2S());
                } else if (event.getAction() == 1) {
                    NetworkHandler.sendToServer(new DoubleJumpC2S());
                }
            }
            if (KeyBinding.SWORD_ABILITY.consumeClick()) {

            }
            if (KeyBinding.SPELLCAST1.consumeClick()) {
                NetworkHandler.sendToServer(new CastSpellC2S(0));
            }
            if (KeyBinding.SPELLCAST2.consumeClick()) {
                NetworkHandler.sendToServer(new CastSpellC2S(1));
            }
            TalentData talentData = TalentGetter.getUnwrap(player);
            if (KeyBinding.TALENTS.consumeClick() && talentData != null) {
                Minecraft.getInstance().setScreen(new TalentsScreen(talentData));
            }
        }

        // the event is used to remove vanilla's "Attack damage: ..." and "Attack speed: ..." lines
        @SubscribeEvent
        public static void itemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            List<Component> tooltip = event.getToolTip();
            if (stack.getItem() instanceof SpellCaster spellCaster) {
                tooltip.addAll(spellCaster.buildTooltip());
            }
            if (stack.getItem() instanceof FragileBlade fragileBlade) {
                Component name = event.getToolTip().get(0);
                tooltip.removeAll(event.getToolTip());
                WhereMagicHappens.Gui.addComponent(tooltip, name.getString(), null, null);
                tooltip.add(Component.translatable(" "));
                tooltip.addAll(fragileBlade.buildTooltip(stack));
            }
            if (stack.getItem() instanceof Murasama murasama) {
                Component name = event.getToolTip().get(0);
                tooltip.removeAll(event.getToolTip());
                WhereMagicHappens.Gui.addComponent(tooltip, name.getString(), null, null);
                tooltip.add(Component.translatable(" "));
                tooltip.addAll(murasama.buildTooltip(null));
            }
        }
        @SubscribeEvent
        public static void playSound(PlaySoundEvent event) {
            if (Minecraft.getInstance().player != null && Minecraft.getInstance().player.hasEffect(MobEffectRegistry.DEAFENING.get())) {
                boolean cancel = false;
                for (SoundEvent soundEvent : nonMuted) {
                    if (event.getName().equals(soundEvent.getLocation().getPath())) {
                        cancel = true;
                    }
                }
                for (RegistryObject<SoundEvent> registryObject : nonMutedMod) {
                    if (event.getName().equals(registryObject.get().getLocation().getPath())) {
                        cancel = true;
                    }
                }
                if (!cancel) {
                    event.setSound(null);
                }
            }
        }
        @SubscribeEvent
        public static void onRenderFire(RenderBlockScreenEffectEvent event) {
            CommonData data = AttachCommonData.getUnwrap(Minecraft.getInstance().player);
            if (event.getOverlayType() == RenderBlockScreenEffectEvent.OverlayType.FIRE && data != null && data.isAncientBurning()) {
                event.setCanceled(true);
            }
        }
    }
}
