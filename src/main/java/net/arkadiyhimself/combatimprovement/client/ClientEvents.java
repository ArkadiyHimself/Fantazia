package net.arkadiyhimself.combatimprovement.client;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.*;
import net.arkadiyhimself.combatimprovement.CombatImprovement;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Networking.packets.CapabilityUpdate.*;
import net.arkadiyhimself.combatimprovement.Networking.packets.KeyInputC2S.CastSpellC2S;
import net.arkadiyhimself.combatimprovement.Networking.packets.KeyInputC2S.WeaponAbilityC2S;
import net.arkadiyhimself.combatimprovement.Networking.packets.ResetFallDistanceC2S;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Melee.FragileBlade;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Melee.Murasama;
import net.arkadiyhimself.combatimprovement.Registries.Items.Weapons.Melee.MeleeWeaponItem;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.VanillaTweaks.RenderAboveTypes.StunBarType;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.VanillaTweaks.RenderLayer.AbsoluteBarrier;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.VanillaTweaks.RenderLayer.BarrierLayer;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.VanillaTweaks.RenderLayer.LayeredBarrierLayer;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.VanillaTweaks.RenderLayer.MysticMirror;
import net.arkadiyhimself.combatimprovement.mixin.LivingEntityRendererAccessor;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.Dash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.DataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.arkadiyhimself.combatimprovement.util.KeyBinding;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiComponent;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
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
import net.minecraftforge.client.model.SeparateTransformsModel;
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
    }};
    public static ArrayList<RegistryObject<SoundEvent>> nonMutedMod = new ArrayList<>(){{
        add(SoundRegistry.HEART_BEAT);
        add(SoundRegistry.DASH1_RECH);
        add(SoundRegistry.DASH2_RECH);
        add(SoundRegistry.DASH3_RECH);
        add(SoundRegistry.RINGING);
    }};
    private static final ResourceLocation BARS = new ResourceLocation(CombatImprovement.MODID, "textures/gui/bars.png");
    private static final ResourceLocation VEINS = new ResourceLocation(CombatImprovement.MODID, "textures/misc/fury/veins.png");
    private static final ResourceLocation VEINS_BRIGHT = new ResourceLocation(CombatImprovement.MODID, "textures/misc/fury/veins_bright.png");
    private static final ResourceLocation FILLING = new ResourceLocation(CombatImprovement.MODID, "textures/misc/fury/filling.png");
    private static final ResourceLocation EDGES = new ResourceLocation(CombatImprovement.MODID, "textures/misc/fury/edges.png");
    @Mod.EventBusSubscriber(value = Dist.CLIENT, modid = CombatImprovement.MODID)
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
            assert player != null;
            PoseStack poseStack = event.getPoseStack();
            if (event.getOverlay() == VanillaGuiOverlay.EXPERIENCE_BAR.type()) {
                RenderSystem.setShaderTexture(0, BARS);
                int x = event.getWindow().getGuiScaledWidth() / 2 - 91;
                int y = event.getWindow().getGuiScaledHeight() - 29;
                if (StunEffect.getUnwrap(player).renderBar()) {
                    if (StunEffect.getUnwrap(player).isStunned()) {
                        int stunPercent = (int) ((float) StunEffect.getUnwrap(player).duration / (float) StunEffect.getUnwrap(player).maxDuration * 182);
                        GuiComponent.blit(poseStack, x, y, 0, 10F, 182, 5, 182, 182);
                        GuiComponent.blit(poseStack, x, y, 0, 0, 15F, stunPercent, 5, 182, 182);
                    } else if (StunEffect.getUnwrap(player).hasPoints()) {
                        int stunPercent = (int) ((float) StunEffect.getUnwrap(player).stunPoints / (float) StunEffect.getUnwrap(player).getMaxPoints() * 182);
                        GuiComponent.blit(poseStack, x, y, 0, 0F, 182, 5, 182, 182);
                        GuiComponent.blit(poseStack, x, y, 0, 0, 5F, stunPercent, 5, 182, 182);
                    }
                    event.setCanceled(true);
                    return;
                }
                if (BarrierEffect.getUnwrap(player).hasBarrier()) {
                    int percent = (int) (BarrierEffect.getUnwrap(player).barrierAmount / BarrierEffect.getUnwrap(player).barrierInitialAmount * 182);
                    event.setCanceled(true);
                    GuiComponent.blit(poseStack, x, y, 0, 40F, 182, 5, 182, 182);
                    GuiComponent.blit(poseStack, x, y, 0, 0, 45F, percent, 5, 182, 182);
                }
            }
            if (event.getOverlay() == VanillaGuiOverlay.PLAYER_HEALTH.type()) { event.setCanceled(true); }
            if (event.getOverlay() == VanillaGuiOverlay.FOOD_LEVEL.type()) { event.setCanceled(true); }
            DataSync dataSync = AttachDataSync.getUnwrap(player);
            if (player.hasEffect(MobEffectRegistry.FURY.get()) && dataSync != null && event.getOverlay() == VanillaGuiOverlay.HOTBAR.type()) {
                float veinTR = dataSync.getVeinTR() / 15;
                float allTR = dataSync.getAllTR() / 20;
                WhereMagicHappens.Gui.renderOnTheWholeScreen(VEINS, 1.0F, 0, 0, 0.4F * allTR);
                WhereMagicHappens.Gui.renderOnTheWholeScreen(VEINS_BRIGHT, 1.0F, 0, 0, veinTR * allTR);
                WhereMagicHappens.Gui.renderOnTheWholeScreen(FILLING, 1.0F, 0, 0, 0.45F * allTR);
                WhereMagicHappens.Gui.renderOnTheWholeScreen(EDGES, 1.0F, 0, 0, 0.925F * allTR);
            }
        }
        @SubscribeEvent
        public static void renderLiving(RenderLivingEvent.Pre<LivingEntity, EntityModel<LivingEntity>> event) {
            assert Minecraft.getInstance().player != null;
            LivingEntity entity = event.getEntity();
            PoseStack poseStack = event.getPoseStack();
            if (entity instanceof Player player) {
                Dash dash = AttachDash.getUnwrap(player);
                if (dash != null && dash.isDashing() && dash.dashLevel == 3) {
                    event.setCanceled(true);
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
            poseStack.pushPose();
            final float globalScale = 0.0625F;
            if (StunEffect.getUnwrap(entity).renderBar()) {
                poseStack.translate(0, entity.getBbHeight() + 0.75, 0);
                poseStack.mulPose(cameraOrientation);
                final int light = 0xF000F0;
                poseStack.scale(-globalScale, -globalScale, -globalScale);

                VertexConsumer stunBar = buffers.getBuffer(StunBarType.BAR_TEXTURE_TYPE);

                float stunPercent;
                if (StunEffect.getUnwrap(entity).isStunned()) {
                    stunPercent = (float) StunEffect.getUnwrap(entity).duration / (float) StunEffect.getUnwrap(entity).maxDuration;
                    int changingRed = StunEffect.getUnwrap(entity).redColor;

                    // empty bar
                    stunBar.vertex(poseStack.last().pose(), -16, 0, 0.001F).color(changingRed, 255, 255, 255).uv(0.0F, 0.5F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -16, 4, 0.001F).color(changingRed, 255, 255, 255).uv(0.0F, 0.75F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 4, 0.001F).color(changingRed, 255, 255, 255).uv(1.0F, 0.75F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 0, 0.001F).color(changingRed, 255, 255, 255).uv(1.0F, 0.5F).uv2(light).endVertex();

                    // filling
                    stunBar.vertex(poseStack.last().pose(), -14, 0, 0.002F).color(255, 255, 255, 255).uv(0.0F, 0.75F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14, 4, 0.002F).color(255, 255, 255, 255).uv(0.0F, 1.0F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 4, 0.002F).color(255, 255, 255, 255).uv(stunPercent, 1.0F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 0, 0.002F).color(255, 255, 255, 255).uv(stunPercent, 0.75F).uv2(light).endVertex();
                } else if (StunEffect.getUnwrap(entity).hasPoints()) {
                    stunPercent = (float) StunEffect.getUnwrap(entity).stunPoints / (float) StunEffect.getUnwrap(entity).getMaxPoints();

                    // empty bar
                    stunBar.vertex(poseStack.last().pose(), -16, 0, 0.001F).color(255, 255, 255, 255).uv(0.0F, 0.0F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -16, 4, 0.001F).color(255, 255, 255, 255).uv(0.0F, 0.25F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 4, 0.001F).color(255, 255, 255, 255).uv(1.0F, 0.25F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), 16, 0, 0.001F).color(255, 255, 255, 255).uv(1.0F, 0.0F).uv2(light).endVertex();

                    // filling
                    stunBar.vertex(poseStack.last().pose(), -14, 0, 0.002F).color(255, 255, 255, 255).uv(0.0F, 0.25F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14, 4, 0.002F).color(255, 255, 255, 255).uv(0.0F, 0.5F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 4, 0.002F).color(255, 255, 255, 255).uv(stunPercent, 0.5F).uv2(light).endVertex();
                    stunBar.vertex(poseStack.last().pose(), -14 + 28 * stunPercent, 0, 0.002F).color(255, 255, 255, 255).uv(stunPercent, 0.25F).uv2(light).endVertex();
                }
            }
            poseStack.popPose();
        }
        @SubscribeEvent
        public static void inout(InputEvent event) {
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
                if (StunEffect.getUnwrap(Minecraft.getInstance().player).isStunned()) { event.setCanceled(true); }
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
            if (player == null) { return; }
            if (WhereMagicHappens.Abilities.canNotDoActions(player)) { return; }

            if (KeyBinding.DASH.consumeClick() && AttachDash.getUnwrap(player).canDash()) {
                NetworkHandler.sendToServer(new StartedDashingC2S());
            }
            if (event.getKey() == Minecraft.getInstance().options.keyJump.getKey().getValue()) {
                if (event.getAction() == 0) {
                    NetworkHandler.sendToServer(new DJumpStartTickC2S());
                } else if (event.getAction() == 1 && AttachDJump.getUnwrap(player).canDJump()) {
                    NetworkHandler.sendToServer(new DoubleJumpC2S());
                    NetworkHandler.sendToServer(new ResetFallDistanceC2S());
                }
            }
            if (KeyBinding.SWORD_ABILITY.consumeClick()) {
                NetworkHandler.sendToServer(new WeaponAbilityC2S());
            }
            if (KeyBinding.SPELLCAST1.consumeClick()) {
                NetworkHandler.sendToServer(new CastSpellC2S(0));
            }
            if (KeyBinding.SPELLCAST2.consumeClick()) {
                NetworkHandler.sendToServer(new CastSpellC2S(1));
            }
        }

        // the event is used to remove vanilla's "Attack damage: ..." and "Attack speed: ..." lines
        @SubscribeEvent
        public static void itemTooltip(ItemTooltipEvent event) {
            ItemStack stack = event.getItemStack();
            List<Component> tooltip = event.getToolTip();
            if (stack.getItem() instanceof FragileBlade fragileBlade) {
                FragileBladeCap cap = AttachFragileBlade.getUnwrap(stack);
                assert cap != null;
                Component name = event.getToolTip().get(0);
                tooltip.clear();
                WhereMagicHappens.Gui.addComponent(tooltip, name.getString(), null, null);
                tooltip.add(Component.translatable(""));
                float minDMG = fragileBlade.getDamage() + 1;
                if (Screen.hasShiftDown()) {
                    float maxDmg = cap.MAX_DMG + minDMG;
                    float curDmg = cap.damage + minDMG;
                    ChatFormatting[] active = new ChatFormatting[]{ChatFormatting.RED};
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.common.active", active, new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.GOLD}, "tooltip.combatimprovement.fragile_blade.ability");
                    tooltip.add(Component.translatable(""));
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.press.1", active, null);
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.press.2", active, new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.GOLD}, maxDmg);
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.press.3", active, null);
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.press.4", active, null);
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.press.5", active, null);
                    tooltip.add(Component.translatable(""));
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.press.6", new ChatFormatting[]{ChatFormatting.LIGHT_PURPLE}, cap.getDamageFormatting(), curDmg);
                } else {
                    ChatFormatting[] passive = new ChatFormatting[]{ChatFormatting.BLUE};
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.release.1", passive, null);
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.release.2", passive, null);
                    tooltip.add(Component.translatable(""));
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.fragile_blade.release.3", new ChatFormatting[]{ChatFormatting.GRAY}, new ChatFormatting[]{ChatFormatting.BOLD, ChatFormatting.DARK_GRAY}, minDMG);
                    tooltip.add(Component.translatable(""));
                    WhereMagicHappens.Gui.addComponent(tooltip,"tooltip.combatimprovement.common.attack_speed", new ChatFormatting[]{ChatFormatting.RED},  new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, 4 + fragileBlade.getAttackSpeedModifier());
                }
            }
            if (stack.getItem() instanceof Murasama sword) {
                Component name = event.getToolTip().get(0);
                tooltip.removeAll(event.getToolTip());
                WhereMagicHappens.Gui.addComponent(tooltip, name.getString(), null, null);
                tooltip.add(Component.translatable(" "));
                if (!Screen.hasShiftDown()) {
                    ChatFormatting[] passive = new ChatFormatting[]{ChatFormatting.BLUE};
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.murasama.release.1", passive, null);
                    tooltip.add(Component.translatable(" "));
                    WhereMagicHappens.Gui.addComponent(tooltip,"tooltip.combatimprovement.common.attack_damage", new ChatFormatting[]{ChatFormatting.GRAY},  new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, sword.getDamage());
                    WhereMagicHappens.Gui.addComponent(tooltip,"tooltip.combatimprovement.common.attack_speed", new ChatFormatting[]{ChatFormatting.GRAY},  new ChatFormatting[]{ChatFormatting.DARK_RED, ChatFormatting.BOLD}, 4 + sword.getAttackSpeedModifier());
                } else {
                    ChatFormatting[] active = new ChatFormatting[]{ChatFormatting.RED};
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.common.active", active, new ChatFormatting[] {ChatFormatting.BOLD, ChatFormatting.GOLD}, "Taunt");
                    tooltip.add(Component.translatable(" "));
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.murasama.press.1", active, new ChatFormatting[] {ChatFormatting.BOLD, ChatFormatting.DARK_RED}, MeleeWeaponItem.abilityKey.getKey().getDisplayName().getString().toUpperCase());
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.murasama.press.2", active, null);
                    WhereMagicHappens.Gui.addComponent(tooltip, "tooltip.combatimprovement.murasama.press.3", active, null);  }
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
    }
}
