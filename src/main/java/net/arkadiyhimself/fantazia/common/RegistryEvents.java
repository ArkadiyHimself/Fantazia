package net.arkadiyhimself.fantazia.common;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.client.particless.particles.*;
import net.arkadiyhimself.fantazia.client.render.FTZRenderTypes;
import net.arkadiyhimself.fantazia.client.render.layers.*;
import net.arkadiyhimself.fantazia.client.renderers.block_entity.AmplificationBenchRenderer;
import net.arkadiyhimself.fantazia.client.renderers.entity.*;
import net.arkadiyhimself.fantazia.client.renderers.item.FantazicItemRenderer;
import net.arkadiyhimself.fantazia.client.screen.AmplificationScreen;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.FTZKeyMappings;
import net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments.LocationHolder;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.LivingEffectHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.living_effect.holders.PuppeteeredEffectHolder;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.common.entity.BlockFly;
import net.arkadiyhimself.fantazia.common.item.BlueprintItem;
import net.arkadiyhimself.fantazia.common.item.RuneWielderItem;
import net.arkadiyhimself.fantazia.common.item.casters.DashStoneItem;
import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.common.registries.custom.Blueprints;
import net.arkadiyhimself.fantazia.common.registries.custom.Runes;
import net.arkadiyhimself.fantazia.data.datagen.*;
import net.arkadiyhimself.fantazia.data.datagen.advancement.FantazicAdvancementsRegular;
import net.arkadiyhimself.fantazia.data.datagen.advancement.FantazicAdvancementsTalent;
import net.arkadiyhimself.fantazia.data.datagen.advancement.FantazicAdvancementsTheWorldliness;
import net.arkadiyhimself.fantazia.data.datagen.effect_from_damage.DefaultEffectsFromDamage;
import net.arkadiyhimself.fantazia.data.datagen.effect_from_damage.FantazicEffectFromDamageProvider;
import net.arkadiyhimself.fantazia.data.datagen.effect_spawn_applier.DefaultEffectSpawnAppliers;
import net.arkadiyhimself.fantazia.data.datagen.effect_spawn_applier.FantazicEffectSpawnApplierProvider;
import net.arkadiyhimself.fantazia.data.datagen.loot_modifier.DefaultFantazicLootModifiers;
import net.arkadiyhimself.fantazia.data.datagen.model.*;
import net.arkadiyhimself.fantazia.data.datagen.patchouli.TheWorldlinessProvider;
import net.arkadiyhimself.fantazia.data.datagen.recipe.FantazicRecipeProvider;
import net.arkadiyhimself.fantazia.data.datagen.tag_providers.*;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.hierarchy.DefaultTalentHierarchies;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.hierarchy.FantazicTalentHierarchyProvider;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent.DefaultTalents;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent.FantazicTalentProvider;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent_tab.DefaultTalentTabs;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.talent_tab.FantazicTalentTabProvider;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.wisdom_reward.DefaultWisdomRewardsCombined;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.wisdom_reward.FantazicWisdomRewardCombinedProvider;
import net.arkadiyhimself.fantazia.networking.attachment_modify.*;
import net.arkadiyhimself.fantazia.networking.attachment_syncing.*;
import net.arkadiyhimself.fantazia.networking.commands.BuildAuraTooltipSC2;
import net.arkadiyhimself.fantazia.networking.commands.BuildRuneTooltipSC2;
import net.arkadiyhimself.fantazia.networking.commands.BuildSpellTooltipSC2;
import net.arkadiyhimself.fantazia.networking.fantazic_boss_event.FantazicBossEventPacket;
import net.arkadiyhimself.fantazia.networking.stuff.*;
import net.minecraft.client.model.BoatModel;
import net.minecraft.client.model.ChestBoatModel;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.HangingSignRenderer;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.item.CompassItemPropertyFunction;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.loot.LootTableProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.client.extensions.common.IClientItemExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
import net.neoforged.neoforge.common.data.AdvancementProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeCreationEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.*;
import net.neoforged.neoforge.registries.datamaps.RegisterDataMapTypesEvent;
import org.jetbrains.annotations.NotNull;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Fantazia.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEvents {

    public static final List<DeferredItem<? extends Item>> ARTIFACTS = new ArrayList<>();
    public static final List<DeferredItem<? extends Item>> EXPENDABLES = new ArrayList<>();
    public static final List<DeferredItem<? extends Item>> BLOCK_ITEMS = new ArrayList<>();
    public static final List<DeferredBlock<? extends Block>> BLOCKS = new ArrayList<>();

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }

    private static final IClientItemExtensions iClientItemExtensions = new IClientItemExtensions() {
        @Override
        public @NotNull BlockEntityWithoutLevelRenderer getCustomRenderer() {
            return Fantazia.getItemsRenderer();
        }
    };

    private static void registerVariants() {

        ItemProperties.register(FTZItems.LEADERS_HORN.get(),
                ResourceLocation.withDefaultNamespace("tooting"),
                (itemStack, clientLevel, livingEntity, i) ->
                        livingEntity != null && livingEntity.isUsingItem() && livingEntity.getUseItem() == itemStack ? 1f : 0f);

        ItemProperties.register(FTZItems.CARD_DECK.get(),
                Fantazia.location("excluded_outcome"),
                (itemStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null) return 0;
            return (float) livingEntity.getData(FTZAttachmentTypes.ALL_IN_PREVIOUS_OUTCOME.value()) / 4;
                });

        ItemProperties.register(FTZItems.BLOODLUST_AMULET.get(),
                Fantazia.location("furious"),
                (itemStack, clientLevel, livingEntity, i) ->
                        livingEntity != null && LivingEffectHelper.hasEffect(livingEntity, FTZMobEffects.FURY.value()) ? 1f : 0f);

        ItemProperties.register(FTZItems.PUPPET_DOLL.get(),
                Fantazia.location("has_puppet"),
                (itemStack, clientLevel, livingEntity, i) -> {
            if (livingEntity == null) return 0;
            PuppeteeredEffectHolder effectHolder = LivingEffectHelper.takeHolder(livingEntity, PuppeteeredEffectHolder.class);
            return effectHolder != null && effectHolder.hasPuppet() ? 1f : 0f;
                });

        ItemProperties.register(FTZItems.ROAMERS_COMPASS.get(),
                ResourceLocation.withDefaultNamespace("angle"), new CompassItemPropertyFunction(((clientLevel, itemStack, entity) -> {
                    LocationHolder locationHolder = entity.getData(FTZAttachmentTypes.WANDERERS_SPIRIT_LOCATION);
                    return locationHolder.empty() ? null : locationHolder.globalPos();
                })));
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skinName) {
        EntityRenderer renderer = event.getSkin(skinName);
        if (renderer instanceof LivingEntityRenderer livingEntityRenderer) {
            Fantazia.LOGGER.info("Adding layers to player...");
            livingEntityRenderer.addLayer(new BarrierLayer.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new LayeredBarrierLayer.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new AbsoluteBarrier.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new MysticMirror.LayerMirror<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new DeflectLayer.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new HatchetLayer<>(event.getContext(), livingEntityRenderer));
            livingEntityRenderer.addLayer(new WitherBarrierLayer<>(livingEntityRenderer));
            Fantazia.LOGGER.info("Layers are addedInitial to player!");
        }
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addLayers(EntityRenderersEvent.AddLayers event, LivingEntityRenderer<T,M> livingEntityRenderer) {
        Fantazia.LOGGER.info("Adding layers to entity type...");
        livingEntityRenderer.addLayer(new BarrierLayer.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new LayeredBarrierLayer.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new AbsoluteBarrier.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new MysticMirror.LayerMirror<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new DeflectLayer.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new HatchetLayer<>(event.getContext(), livingEntityRenderer));
        livingEntityRenderer.addLayer(new WitherBarrierLayer<>(livingEntityRenderer));
        Fantazia.LOGGER.info("Layers are addedInitial to entity type!");
    }

    @SubscribeEvent
    public static void registerDataMapTypes(RegisterDataMapTypesEvent event) {
        event.register(FTZDataMapTypes.SKULLS);
        event.register(FTZDataMapTypes.RECHARGEABLE_TOOLS);
        event.register(FTZDataMapTypes.MOB_EFFECT_WHITE_LIST);
        event.register(FTZDataMapTypes.MOB_EFFECT_BLACK_LIST);
    }

    @SubscribeEvent
    public static void attributeModification(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FTZAttributes.MAX_MANA);
        event.add(EntityType.PLAYER, FTZAttributes.MAX_STAMINA);
        event.add(EntityType.PLAYER, FTZAttributes.CAST_RANGE_ADDITION);
        event.add(EntityType.PLAYER, FTZAttributes.RECHARGE_MULTIPLIER);
        event.add(EntityType.PLAYER, FTZAttributes.AURA_RANGE_ADDITION);
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, FTZAttributes.MAX_STUN_POINTS);
            event.add(entityType, FTZAttributes.LIFESTEAL);
            event.add(entityType, FTZAttributes.EVASION);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void modelRegistry(ModelEvent.RegisterAdditional event) {
        event.register(FantazicItemRenderer.BLADE0);
        event.register(FantazicItemRenderer.BLADE1);
        event.register(FantazicItemRenderer.BLADE2);
        event.register(FantazicItemRenderer.BLADE3);
        event.register(FantazicItemRenderer.BLADE4);
        event.register(FantazicItemRenderer.BLADE_MODEL);
        event.register(FantazicItemRenderer.WISDOM_CATCHER_GUI_ABSORB);
        event.register(FantazicItemRenderer.WISDOM_CATCHER_MODEL_ABSORB);
        event.register(FantazicItemRenderer.WISDOM_CATCHER_MODEL_USED_ABSORB);
        event.register(FantazicItemRenderer.WISDOM_CATCHER_GUI_RELEASE);
        event.register(FantazicItemRenderer.WISDOM_CATCHER_MODEL_RELEASE);
        event.register(FantazicItemRenderer.WISDOM_CATCHER_MODEL_USED_RELEASE);
        event.register(FantazicItemRenderer.DASHSTONE1);
        event.register(FantazicItemRenderer.DASHSTONE2);
        event.register(FantazicItemRenderer.DASHSTONE3);

        event.register(ThrownHatchetRenderer.WOODEN);
        event.register(ThrownHatchetRenderer.STONE);
        event.register(ThrownHatchetRenderer.GOLDEN);
        event.register(ThrownHatchetRenderer.IRON);
        event.register(ThrownHatchetRenderer.DIAMOND);
        event.register(ThrownHatchetRenderer.NETHERITE);

        for (Rune rune : FantazicRegistries.RUNES.stream().toList()) event.register(rune.getIcon());
        for (Blueprint blueprint : FantazicRegistries.BLUEPRINTS.stream().toList()) event.register(blueprint.getIcon());
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGui(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Fantazia.location("ftz_gui"), FTZGuis.FTZ_GUI);
        event.registerAboveAll(Fantazia.location("obtained_wisdom"), FTZGuis.OBTAINED_WISDOM);
        event.registerAboveAll(Fantazia.location("curioslots"), FTZGuis.CURIO_SLOTS);
        event.registerBelowAll(Fantazia.location("ancient_burning"), FTZGuis.ANCIENT_FLAME);
        event.registerAboveAll(Fantazia.location("auras"), FTZGuis.AURAS);
        event.registerAboveAll(Fantazia.location("developer_mode"), FTZGuis.DEVELOPER_MODE);
        event.registerBelowAll(Fantazia.location("frozen_effect"), FTZGuis.FROZEN_EFFECT);
        event.registerBelowAll(Fantazia.location("fury_veins"), FTZGuis.FURY_VEINS);
        event.registerAboveAll(Fantazia.location("custom_bars"), FTZGuis.CUSTOM_BARS);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(CustomBoatRenderer.OBSCURE_BOAT_LAYER, BoatModel::createBodyModel);
        event.registerLayerDefinition(CustomBoatRenderer.OBSCURE_CHEST_BOAT_LAYER, ChestBoatModel::createBodyModel);
        event.registerLayerDefinition(SimpleChasingProjectileRenderer.SIMPLE_CHASING_PROJECTILE_LAYER, SimpleChasingProjectileModel::createBodyLayer);
        event.registerLayerDefinition(PimpilloRenderer.PIMPILLO_LAYER, PimpilloModel::createBodyLayer);
        event.registerLayerDefinition(ThrownPinRenderer.THROWN_PIN_LAYER, ThrownPinModel::createBodyLayer);
        event.registerLayerDefinition(BlockFlyRenderer.BLOCK_FLY_LAYER, BlockFlyModel::createBodyLayer);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FTZEntityTypes.HATCHET.get(), ThrownHatchetRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.DASHSTONE.get(), DashStoneRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.SHOCKWAVE.get(), ShockwaveRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.CUSTOM_BOAT.get(), context -> new CustomBoatRenderer(context, false));
        event.registerEntityRenderer(FTZEntityTypes.CUSTOM_CHEST_BOAT.get(), context -> new CustomBoatRenderer(context, true));
        event.registerEntityRenderer(FTZEntityTypes.FANTAZIC_PAINTING.get(), FantazicPaintingRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.SIMPLE_CHASING_PROJECTILE.get(), SimpleChasingProjectileRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.PIMPILLO.get(), PimpilloRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.THROWN_PIN.get(), ThrownPinRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.BLOCK_FLY.get(), BlockFlyRenderer::new);

        event.registerBlockEntityRenderer(FTZBlockEntityTypes.OBSCURE_SIGN.value(), SignRenderer::new);
        event.registerBlockEntityRenderer(FTZBlockEntityTypes.OBSCURE_HANGING_SIGN.value(), HangingSignRenderer::new);
        event.registerBlockEntityRenderer(FTZBlockEntityTypes.AMPLIFICATION_BENCH.value(), AmplificationBenchRenderer::new);
    }

    @SubscribeEvent
    public static void entityAttributeCreation(EntityAttributeCreationEvent event) {
        event.put(FTZEntityTypes.BLOCK_FLY.value(), BlockFly.createAttributes().build());
    }

    @SubscribeEvent
    public static void registerMenuScreens(RegisterMenuScreensEvent event) {
        event.register(FTZMenuTypes.AMPLIFICATION.value(), AmplificationScreen::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(FTZKeyMappings.DASH);
        event.register(FTZKeyMappings.BLOCK);
        event.register(FTZKeyMappings.SWORD_ABILITY);
        event.register(FTZKeyMappings.SPELLCAST1);
        event.register(FTZKeyMappings.SPELLCAST2);
        event.register(FTZKeyMappings.SPELLCAST3);
        event.register(FTZKeyMappings.TALENTS);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerParticles(final RegisterParticleProvidersEvent event) {
        event.registerSpriteSet(FTZParticleTypes.BLOOD1.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BLOOD2.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BLOOD3.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BLOOD4.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BLOOD5.get(), BloodParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.DOOMED_SOUL1.get(), SoulParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.DOOMED_SOUL2.get(), SoulParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.DOOMED_SOUL3.get(), SoulParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.FALLEN_SOUL.get(), FallenSoulParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE1.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE2.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE3.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE4.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE5.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE1_FURY.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE2_FURY.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE3_FURY.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE4_FURY.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE5_FURY.get(), BarrierParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.LIFESTEAL1.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.LIFESTEAL2.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.LIFESTEAL3.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.LIFESTEAL4.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.LIFESTEAL5.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.REGEN1.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.REGEN2.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.REGEN3.get(), GenericParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.TIME_TRAVEL.get(), TimeTravelParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.ELECTRO1.get(), EntityChasingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.ELECTRO2.get(), EntityChasingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.ELECTRO3.get(), EntityChasingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.ELECTRO4.get(), EntityChasingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.ELECTRO5.get(), EntityChasingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.WITHER.get(), EntityChasingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.CHAINED.get(), ChainedParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.METAL_SCRAP1.get(), SimpleFallingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.METAL_SCRAP2.get(), SimpleFallingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.METAL_SCRAP3.get(), SimpleFallingParticle.Provider::new);
        event.registerSpriteSet(FTZParticleTypes.METAL_SCRAP4.get(), SimpleFallingParticle.Provider::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        registerVariants();
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Fantazia.location("animation"), 42, RegistryEvents::registerPlayerAnimation);
        Sheets.addWoodType(FTZWoodTypes.OBSCURE);
        FTZBlocks.onSetup();
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void creativeTabContents(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab tab = event.getTab();
        if (tab == FTZCreativeModeTabs.ARTIFACTS) {
            for (int i = 1; i <= 3; i++) event.accept(DashStoneItem.dashStone(i));
            for (DeferredItem<? extends Item> item : ARTIFACTS) event.accept(new ItemStack(item.asItem()));
            for (Holder<Rune> rune : Runes.REGISTER.getEntries()) event.accept(RuneWielderItem.rune(rune));
        }
        if (tab == FTZCreativeModeTabs.EXPENDABLES) {
            for (DeferredItem<? extends Item> item : EXPENDABLES) event.accept(new ItemStack(item.asItem()));
            for (Holder<Blueprint> blueprint : Blueprints.REGISTER.getEntries()) event.accept(BlueprintItem.blueprint(blueprint));
        }
        if (tab == FTZCreativeModeTabs.BLOCKS) {
            for (DeferredBlock<? extends Block> block : BLOCKS) event.accept(new ItemStack(block.get()));
            for (DeferredItem<? extends Item> item : BLOCK_ITEMS) event.accept(new ItemStack(item.asItem()));
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerClientExtensions(RegisterClientExtensionsEvent event) {
        event.registerItem(iClientItemExtensions, FTZItems.FRAGILE_BLADE);
        event.registerItem(iClientItemExtensions, FTZItems.WISDOM_CATCHER);
        event.registerItem(iClientItemExtensions, FTZItems.RUNE_WIELDER);
        event.registerItem(iClientItemExtensions, FTZItems.DASHSTONE);
        event.registerItem(iClientItemExtensions, FTZItems.BLUEPRINT);
    }

    @SubscribeEvent
    public static void newRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(FantazicRegistries.Keys.HEALING_TYPE, HealingType.CODEC, HealingType.CODEC);
    }

    @SubscribeEvent
    public static void registerEvent(RegisterEvent event) {
        FTZItems.onRegistry(event);
    }

    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Fantazia.MODID);

        // attachment modifying
        registrar.playToClient(AllInPreviousOutcomeS2C.TYPE, AllInPreviousOutcomeS2C.CODEC, AllInPreviousOutcomeS2C::handle);
        registrar.playToClient(ArrowIsFurious.TYPE, ArrowIsFurious.CODEC, ArrowIsFurious::handle);
        registrar.playToClient(BarrierChangedSC2.TYPE, BarrierChangedSC2.CODEC, BarrierChangedSC2::handle);
        registrar.playToClient(BarrierDamagedS2C.TYPE, BarrierDamagedS2C.CODEC, BarrierDamagedS2C::handle);
        registrar.playToServer(BeginDashC2S.TYPE, BeginDashC2S.CODEC, BeginDashC2S::handle);
        registrar.playToClient(BlockAttackSC2.TYPE, BlockAttackSC2.CODEC, BlockAttackSC2::handle);
        registrar.playToServer(CancelDashS2C.TYPE, CancelDashS2C.CODEC, CancelDashS2C::handle);
        registrar.playToClient(EffectSyncSC2.TYPE, EffectSyncSC2.CODEC, EffectSyncSC2::handle);
        registrar.playToClient(EntityMadeSoundS2C.TYPE, EntityMadeSoundS2C.CODEC, EntityMadeSoundS2C::handle);
        registrar.playToClient(IncreaseEuphoriaSC2.TYPE, IncreaseEuphoriaSC2.CODEC, IncreaseEuphoriaSC2::handle);
        registrar.playToServer(JumpButtonReleasedC2S.TYPE, JumpButtonReleasedC2S.CODEC, JumpButtonReleasedC2S::handle);
        registrar.playToClient(LayeredBarrierChangedSC2.TYPE, LayeredBarrierChangedSC2.CODEC, LayeredBarrierChangedSC2::handle);
        registrar.playToClient(LayeredBarrierDamagedS2C.TYPE, LayeredBarrierDamagedS2C.CODEC, LayeredBarrierDamagedS2C::handle);
        registrar.playToClient(ManaChangedS2C.TYPE, ManaChangedS2C.CODEC, ManaChangedS2C::handle);
        registrar.playToClient(ObtainedRewardS2C.TYPE, ObtainedRewardS2C.CODEC, ObtainedRewardS2C::handle);
        registrar.playToClient(ParryAttackS2C.TYPE, ParryAttackS2C.CODEC, ParryAttackS2C::handle);
        registrar.playToServer(PerformDoubleJumpC2S.TYPE, PerformDoubleJumpC2S.CODEC, PerformDoubleJumpC2S::handle);
        registrar.playToClient(PogoPlayerSC2.TYPE, PogoPlayerSC2.CODEC, PogoPlayerSC2::handle);
        registrar.playToClient(PuppeteerChangeSC2.TYPE, PuppeteerChangeSC2.CODEC, PuppeteerChangeSC2::handle);
        registrar.playToClient(ReflectActivateSC2.TYPE, ReflectActivateSC2.CODEC, ReflectActivateSC2::handle);
        registrar.playToClient(ResetEuphoriaSC2.TYPE, ResetEuphoriaSC2.CODEC, ResetEuphoriaSC2::handle);
        registrar.playToClient(ResetWisdomRewardsSC2.TYPE, ResetWisdomRewardsSC2.CODEC, ResetWisdomRewardsSC2::handle);
        registrar.playToClient(RevokeAllTalentsS2C.TYPE, RevokeAllTalentsS2C.CODEC, RevokeAllTalentsS2C::handle);
        registrar.playToClient(SetDashStoneEntitySC2.TYPE, SetDashStoneEntitySC2.CODEC, SetDashStoneEntitySC2::handle);
        registrar.playToClient(SetWisdomSC2.TYPE, SetWisdomSC2.CODEC, SetWisdomSC2::handle);
        registrar.playToClient(StaminaChangedSC2.TYPE, StaminaChangedSC2.CODEC, StaminaChangedSC2::handle);
        registrar.playToServer(StartBlockingC2S.TYPE, StartBlockingC2S.CODEC, StartBlockingC2S::handle);
        registrar.playToClient(StopDashS2C.TYPE, StopDashS2C.CODEC, StopDashS2C::handle);
        registrar.playToClient(SuccessfulEvasionSC2.TYPE, SuccessfulEvasionSC2.CODEC, SuccessfulEvasionSC2::handle);
        registrar.playToServer(TalentDisableC2S.TYPE, TalentDisableC2S.CODEC, TalentDisableC2S::handle);
        registrar.playToServer(TalentBuyingC2S.TYPE, TalentBuyingC2S.CODEC, TalentBuyingC2S::handle);
        registrar.playToClient(TalentPossessionSC2.TYPE, TalentPossessionSC2.CODEC, TalentPossessionSC2::handle);
        registrar.playToClient(TickingIntegerUpdateS2C.TYPE, TickingIntegerUpdateS2C.CODEC, TickingIntegerUpdateS2C::handle);
        registrar.playToClient(WanderersSpiritLocationS2C.TYPE, WanderersSpiritLocationS2C.CODEC, WanderersSpiritLocationS2C::handle);
        registrar.playToClient(WisdomObtainedSC2.TYPE, WisdomObtainedSC2.CODEC, WisdomObtainedSC2::handle);

        // attachment syncing
        registrar.playToClient(LevelAttributesUpdateS2C.TYPE, LevelAttributesUpdateS2C.CODEC, LevelAttributesUpdateS2C::handle);
        registrar.playToClient(LivingEntityAttachmentInitialSyncSC2.TYPE, LivingEntityAttachmentInitialSyncSC2.CODEC, LivingEntityAttachmentInitialSyncSC2::handle);
        registrar.playToClient(LivingEntityAttachmentTickSyncSC2.TYPE, LivingEntityAttachmentTickSyncSC2.CODEC, LivingEntityAttachmentTickSyncSC2::handle);
        registrar.playToClient(PlayerAttachmentInitialSyncSC2.TYPE, PlayerAttachmentInitialSyncSC2.CODEC, PlayerAttachmentInitialSyncSC2::handle);
        registrar.playToClient(PlayerAttachmentTickSyncSC2.TYPE, PlayerAttachmentTickSyncSC2.CODEC, PlayerAttachmentTickSyncSC2::handle);
        registrar.playToClient(SimpleMobEffectSyncingS2C.TYPE, SimpleMobEffectSyncingS2C.CODEC, SimpleMobEffectSyncingS2C::handle);
        registrar.playToClient(UpdateAuraInstancesS2C.TYPE, UpdateAuraInstancesS2C.CODEC, UpdateAuraInstancesS2C::handle);

        // commands
        registrar.playToClient(BuildAuraTooltipSC2.TYPE, BuildAuraTooltipSC2.CODEC, BuildAuraTooltipSC2::handle);
        registrar.playToClient(BuildRuneTooltipSC2.TYPE, BuildRuneTooltipSC2.CODEC, BuildRuneTooltipSC2::handle);
        registrar.playToClient(BuildSpellTooltipSC2.TYPE, BuildSpellTooltipSC2.CODEC, BuildSpellTooltipSC2::handle);

        // stuff
        registrar.playToClient(AddChasingParticlesS2C.TYPE, AddChasingParticlesS2C.CODEC, AddChasingParticlesS2C::handle);
        registrar.playToClient(AddDashStoneProtectorsSC2.TYPE, AddDashStoneProtectorsSC2.CODEC, AddDashStoneProtectorsSC2::handle);
        registrar.playToClient(AddParticlesOnEntitySC2.TYPE, AddParticlesOnEntitySC2.CODEC, AddParticlesOnEntitySC2::handle);
        registrar.playToClient(AmplificationMenuEnoughResourcesSC2.TYPE, AmplificationMenuEnoughResourcesSC2.CODEC, AmplificationMenuEnoughResourcesSC2::handle);
        registrar.playToClient(AnimatePlayerSC2.TYPE, AnimatePlayerSC2.CODEC, AnimatePlayerSC2::handle);
        registrar.playToClient(HatchetRemovedSC2.TYPE, HatchetRemovedSC2.CODEC, HatchetRemovedSC2::handle);
        registrar.playToClient(HatchetStuckSC2.TYPE, HatchetStuckSC2.CODEC, HatchetStuckSC2::handle);
        registrar.playToClient(InterruptPlayerS2C.TYPE, InterruptPlayerS2C.CODEC, InterruptPlayerS2C::handle);
        registrar.playToServer(KeyInputC2S.TYPE, KeyInputC2S.CODEC, KeyInputC2S::handle);
        registrar.playToClient(PlaySoundForUIS2C.TYPE, PlaySoundForUIS2C.CODEC, PlaySoundForUIS2C::handle);
        registrar.playToClient(PromptPlayerSC2.TYPE, PromptPlayerSC2.CODEC, PromptPlayerSC2::handle);
        registrar.playToServer(SetAmplificationTabC2S.TYPE, SetAmplificationTabC2S.CODEC, SetAmplificationTabC2S::handle);
        registrar.playToServer(SummonShockwaveC2S.TYPE, SummonShockwaveC2S.CODEC, SummonShockwaveC2S::handle);
        registrar.playToClient(SwingHandS2C.TYPE, SwingHandS2C.CODEC, SwingHandS2C::handle);
        registrar.playToServer(UsedPromptC2S.TYPE, UsedPromptC2S.CODEC, UsedPromptC2S::handle);

        // boss event
        registrar.playToClient(FantazicBossEventPacket.TYPE, FantazicBossEventPacket.CODEC, FantazicBossEventPacket::handle);
    }

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(FantazicRegistries.SPELLS);
        event.register(FantazicRegistries.AURAS);
        event.register(FantazicRegistries.RUNES);
        event.register(FantazicRegistries.TOOL_CAPACITY_LEVEL_FUNCTIONS);
        event.register(FantazicRegistries.TOOL_DAMAGE_LEVEL_FUNCTIONS);
        event.register(FantazicRegistries.BLUEPRINTS);
    }

    @SubscribeEvent
    public static void addLayer(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(entityType);
            if (renderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) addLayers(event, livingEntityRenderer);
        }

        addLayerToPlayerSkin(event, PlayerSkin.Model.WIDE);
        addLayerToPlayerSkin(event, PlayerSkin.Model.SLIM);
    }

    @SubscribeEvent
    public static void renderType(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                Fantazia.location("rendertype_custom_glint"), DefaultVertexFormat.NEW_ENTITY), shaderInstance -> FTZRenderTypes.customGlintShader = shaderInstance);
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        // client
        FantazicDatapackProvider dataPackProvider = new FantazicDatapackProvider(packOutput, lookupProvider);
        generator.addProvider(event.includeClient(), dataPackProvider);
        generator.addProvider(event.includeClient(), new FantazicBlockModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new FantazicBlockStateProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new FantazicItemModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new FantazicRuneModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new FantazicBlueprintModelProvider(packOutput, existingFileHelper));
        generator.addProvider(event.includeClient(), new FantazicParticleProvider(packOutput, existingFileHelper));

        // tags providers
        FantazicBlockTagsProvider blockTagsProvider = new FantazicBlockTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper);

        generator.addProvider(event.includeServer(), new FantazicBiomeTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), blockTagsProvider);
        generator.addProvider(event.includeServer(), new FantazicDamageTypeTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicEnchantmentTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicEntityTypeTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicHealingTypeTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicItemTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), blockTagsProvider.contentsGetter(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicMobEffectTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicSoundEventTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicSpellTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));
        generator.addProvider(event.includeServer(), new FantazicPaintingVariantTagsProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper));

        // stuff
        generator.addProvider(event.includeServer(), new FantazicRecipeProvider(packOutput, dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(), List.of(new LootTableProvider.SubProviderEntry(FantazicBlockLootSubProvider::new, LootContextParamSets.BLOCK)), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new net.arkadiyhimself.fantazia.data.datagen.loot_modifier.FantazicLootModifierProvider(packOutput, List.of(DefaultFantazicLootModifiers.create()), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicEffectSpawnApplierProvider(packOutput, List.of(DefaultEffectSpawnAppliers.create()), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicWisdomRewardCombinedProvider(packOutput, List.of(DefaultWisdomRewardsCombined.create()), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new AdvancementProvider(packOutput, dataPackProvider.getRegistryProvider(), existingFileHelper, List.of(
                FantazicAdvancementsRegular.create(),
                FantazicAdvancementsTalent.create(),
                FantazicAdvancementsTheWorldliness.create()
        )));
        generator.addProvider(event.includeServer(), new FantazicTalentProvider(packOutput, List.of(DefaultTalents.create()), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicTalentHierarchyProvider(packOutput, List.of(DefaultTalentHierarchies.create()), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicTalentTabProvider(packOutput, List.of(DefaultTalentTabs.create()), dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicCurioProvider(packOutput, existingFileHelper, dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicLootModifierProvider(packOutput, dataPackProvider.getRegistryProvider()));
        /*
        generator.addProvider(event.includeServer(), new TheWorldlinessEntryProvider(packOutput, dataPackProvider.getRegistryProvider(), List.of(
                ArtifactCategoryEntries.create(),
                EnchantmentCategoryEntries.create(),
                ExpendableCategoryEntries.create(),
                MobEffectCategoryEntries.create(),
                WeaponCategoryEntries.create(),
                WorldCategoryEntries.create()
        )));
         */
        //generator.addProvider(event.includeServer(), new TheWorldlinessCategoryProvider(packOutput, dataPackProvider.getRegistryProvider(), List.of(TheWorldlinessCategories.create())));
        generator.addProvider(event.includeServer(), new TheWorldlinessProvider(packOutput, dataPackProvider.getRegistryProvider()));
        generator.addProvider(event.includeServer(), new FantazicEffectFromDamageProvider(packOutput, List.of(DefaultEffectsFromDamage.create()), lookupProvider));
        generator.addProvider(event.includeServer(), new FantazicDataMapsProvider(packOutput, lookupProvider));
    }
}
