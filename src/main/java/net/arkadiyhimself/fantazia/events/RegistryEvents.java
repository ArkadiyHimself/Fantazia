package net.arkadiyhimself.fantazia.events;

import com.google.common.collect.Lists;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.KeyBinding;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.DashHolder;
import net.arkadiyhimself.fantazia.api.type.item.IChangingIcon;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.client.models.entity.DashStoneRenderer;
import net.arkadiyhimself.fantazia.client.models.entity.ThrownHatchetRenderer;
import net.arkadiyhimself.fantazia.client.models.item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.client.render.layers.AbsoluteBarrier;
import net.arkadiyhimself.fantazia.client.render.layers.BarrierLayer;
import net.arkadiyhimself.fantazia.client.render.layers.LayeredBarrierLayer;
import net.arkadiyhimself.fantazia.client.render.layers.MysticMirror;
import net.arkadiyhimself.fantazia.data.tags.HealingTypeTagsProvider;
import net.arkadiyhimself.fantazia.data.tags.MobEffectTagsProvider;
import net.arkadiyhimself.fantazia.data.tags.SpellTagProvider;
import net.arkadiyhimself.fantazia.networking.packets.attachment_modify.EntityMadeSoundS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_modify.SoundExpiredS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_modify.TalentBuyingC2S;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.LevelAttributesUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.LivingDataUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.LivingEffectUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.attachment_syncing.PlayerAbilityUpdateS2C;
import net.arkadiyhimself.fantazia.networking.packets.stuff.*;
import net.arkadiyhimself.fantazia.particless.*;
import net.arkadiyhimself.fantazia.registries.*;
import net.minecraft.client.model.EntityModel;
import net.minecraft.client.model.HumanoidArmorModel;
import net.minecraft.client.model.HumanoidModel;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.LivingEntityRenderer;
import net.minecraft.client.renderer.entity.layers.RenderLayer;
import net.minecraft.client.resources.PlayerSkin;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.*;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.entity.EntityAttributeModificationEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.neoforged.neoforge.registries.DataPackRegistryEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegisterEvent;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = Fantazia.MODID, bus = EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    private RegistryEvents() {}

    public static final List<DeferredHolder<Item, ? extends Item>> ARTIFACTS = new ArrayList<>();
    public static final List<DeferredHolder<Item, ? extends Item>> WEAPONS = new ArrayList<>();
    public static final List<DeferredHolder<Item, ? extends Item>> EXPENDABLES = new ArrayList<>();

    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private static <E extends Player, M extends HumanoidModel<E>> void addLayerToPlayerSkin(EntityRenderersEvent.AddLayers event, PlayerSkin.Model skinName) {
        EntityRenderer renderer = event.getSkin(skinName);
        if (renderer instanceof LivingEntityRenderer livingEntityRenderer) {
            livingEntityRenderer.addLayer(new BarrierLayer.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new LayeredBarrierLayer.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new AbsoluteBarrier.LayerBarrier<>(livingEntityRenderer));
            livingEntityRenderer.addLayer(new MysticMirror.LayerMirror<>(livingEntityRenderer));
        };
    }

    private static <T extends LivingEntity, M extends EntityModel<T>> void addLayers(LivingEntityRenderer<T,M> livingEntityRenderer) {
        Fantazia.LOGGER.info("Adding layers...");
        livingEntityRenderer.addLayer(new BarrierLayer.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new LayeredBarrierLayer.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new AbsoluteBarrier.LayerBarrier<>(livingEntityRenderer));
        livingEntityRenderer.addLayer(new MysticMirror.LayerMirror<>(livingEntityRenderer));
        Fantazia.LOGGER.info("Layers are added!");
    }

    @SubscribeEvent
    public static void attributeModification(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FTZAttributes.MANA_REGEN_MULTIPLIER);
        event.add(EntityType.PLAYER, FTZAttributes.STAMINA_REGEN_MULTIPLIER);
        event.add(EntityType.PLAYER, FTZAttributes.MAX_MANA);
        event.add(EntityType.PLAYER, FTZAttributes.MAX_STAMINA);
        event.add(EntityType.PLAYER, FTZAttributes.CAST_RANGE_ADDITION);
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, FTZAttributes.MAX_STUN_POINTS);
            event.add(entityType, FTZAttributes.LIFESTEAL);
            event.add(entityType, FTZAttributes.EVASION);
        }
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void modelRegistry(ModelEvent.RegisterAdditional event) {
        event.register(CustomItemRenderer.BLADE0);
        event.register(CustomItemRenderer.BLADE1);
        event.register(CustomItemRenderer.BLADE2);
        event.register(CustomItemRenderer.BLADE3);
        event.register(CustomItemRenderer.BLADE4);

        event.register(CustomItemRenderer.BLADE_MODEL);

        event.register(ThrownHatchetRenderer.WOODEN);
        event.register(ThrownHatchetRenderer.STONE);
        event.register(ThrownHatchetRenderer.GOLDEN);
        event.register(ThrownHatchetRenderer.IRON);
        event.register(ThrownHatchetRenderer.DIAMOND);
        event.register(ThrownHatchetRenderer.NETHERITE);

        event.register(DashStoneRenderer.DASHSTONE2);
        event.register(DashStoneRenderer.DASHSTONE3);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGui(RegisterGuiLayersEvent event) {
        event.registerAboveAll(Fantazia.res("ftz_gui"), FTZGuis.FTZ_GUI);
        event.registerAboveAll(Fantazia.res("obtained_wisdom"), FTZGuis.OBTAINED_WISDOM);
        event.registerAboveAll(Fantazia.res("curioslots"), FTZGuis.CURIO_SLOTS);
        event.registerBelowAll(Fantazia.res("ancient_burning"), FTZGuis.ANCIENT_FLAME);
        event.registerAboveAll(Fantazia.res("auras"), FTZGuis.AURAS);
        event.registerAboveAll(Fantazia.res("developer_mode"), FTZGuis.DEVELOPER_MODE);
        event.registerBelowAll(Fantazia.res("frozen_effect"), FTZGuis.FROZEN_EFFECT);
        event.registerBelowAll(Fantazia.res("fury_veins"), FTZGuis.FURY_VEINS);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FTZEntityTypes.HATCHET.get(), ThrownHatchetRenderer::new);
        event.registerEntityRenderer(FTZEntityTypes.DASHSTONE.get(), DashStoneRenderer::new);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void keyBinding(RegisterKeyMappingsEvent event) {
        event.register(KeyBinding.DASH);
        event.register(KeyBinding.BLOCK);
        event.register(KeyBinding.SWORD_ABILITY);
        event.register(KeyBinding.SPELLCAST1);
        event.register(KeyBinding.SPELLCAST2);
        event.register(KeyBinding.TALENTS);
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
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void clientSetup(FMLClientSetupEvent event) {
        List<DeferredHolder<Item, ? extends Item>> items = Lists.newArrayList();
        items.addAll(ARTIFACTS);
        items.addAll(WEAPONS);
        items.addAll(EXPENDABLES);
        for (DeferredHolder<Item, ? extends Item> item : items) if (item.get() instanceof IChangingIcon icon) icon.registerVariants();
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Fantazia.res("animation"), 42, RegistryEvents::registerPlayerAnimation);
    }

    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void creativeTabContents(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab tab = event.getTab();
        if (tab == FTZCreativeModeTabs.ARTIFACTS) for (DeferredHolder<Item, ? extends Item> item : ARTIFACTS) event.accept(new ItemStack(item));
        if (tab == FTZCreativeModeTabs.WEAPONS) for (DeferredHolder<Item, ? extends Item> item : WEAPONS) event.accept(new ItemStack(item));
        if (tab == FTZCreativeModeTabs.EXPENDABLES) for (DeferredHolder<Item, ? extends Item> item : EXPENDABLES) event.accept(new ItemStack(item));
    }

    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator gen = event.getGenerator();
        PackOutput packOutput = gen.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();
        gen.addProvider(event.includeServer(), new MobEffectTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new HealingTypeTagsProvider(packOutput, lookupProvider, existingFileHelper));
        gen.addProvider(event.includeServer(), new SpellTagProvider(packOutput, lookupProvider, existingFileHelper));
    }

    @SubscribeEvent
    public static void newRegistry(DataPackRegistryEvent.NewRegistry event) {
        event.dataPackRegistry(FantazicRegistry.Keys.HEALING_TYPE, HealingType.CODEC, HealingType.CODEC);
    }

    @SubscribeEvent
    public static void registerEvent(RegisterEvent event) {
        FTZItems.onRegistry(event);
    }

    @SubscribeEvent
    public static void registerPayload(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar(Fantazia.MODID);

        // attachment syncing
        registrar.playToClient(PlayerAbilityUpdateS2C.TYPE, PlayerAbilityUpdateS2C.CODEC, PlayerAbilityUpdateS2C::handle);
        registrar.playToClient(LivingDataUpdateS2C.TYPE, LivingDataUpdateS2C.CODEC, LivingDataUpdateS2C::handle);
        registrar.playToClient(LivingEffectUpdateS2C.TYPE, LivingEffectUpdateS2C.CODEC, LivingEffectUpdateS2C::handle);
        registrar.playToClient(LevelAttributesUpdateS2C.TYPE, LevelAttributesUpdateS2C.CODEC, LevelAttributesUpdateS2C::handle);

        // attachment modifying
        registrar.playToClient(EntityMadeSoundS2C.TYPE, EntityMadeSoundS2C.CODEC, EntityMadeSoundS2C::handle);
        registrar.playToClient(SoundExpiredS2C.TYPE, SoundExpiredS2C.CODEC, SoundExpiredS2C::handle);
        registrar.playToServer(TalentBuyingC2S.TYPE, TalentBuyingC2S.CODEC, TalentBuyingC2S::handle);

        // stuff
        registrar.playToClient(AddParticleS2C.TYPE, AddParticleS2C.CODEC, AddParticleS2C::handle);
        registrar.playToClient(KickOutOfGuiS2C.TYPE, KickOutOfGuiS2C.CODEC, KickOutOfGuiS2C::handle);
        registrar.playToServer(KeyInputC2S.TYPE, KeyInputC2S.CODEC, KeyInputC2S::handle);
        registrar.playToClient(SwingHandS2C.TYPE, SwingHandS2C.CODEC, SwingHandS2C::handle);
        registrar.playToClient(PlayAnimationS2C.TYPE, PlayAnimationS2C.CODEC, PlayAnimationS2C::handle);
        registrar.playToClient(PlaySoundForUIS2C.TYPE, PlaySoundForUIS2C.CODEC, PlaySoundForUIS2C::handle);
    }

    @SubscribeEvent
    public static void newRegistry(NewRegistryEvent event) {
        event.register(FantazicRegistry.SPELLS);
        event.register(FantazicRegistry.AURAS);
    }

    @SubscribeEvent
    public static void addLayer(EntityRenderersEvent.AddLayers event) {
        for (EntityType<?> entityType : event.getEntityTypes()) {
            EntityRenderer<?> renderer = event.getRenderer(entityType);
            if (renderer instanceof LivingEntityRenderer<?, ?> livingEntityRenderer) addLayers(livingEntityRenderer);
        }

        addLayerToPlayerSkin(event, PlayerSkin.Model.WIDE);
        addLayerToPlayerSkin(event, PlayerSkin.Model.SLIM);
    }
}
