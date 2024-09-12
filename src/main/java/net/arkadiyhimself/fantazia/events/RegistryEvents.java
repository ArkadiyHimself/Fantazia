package net.arkadiyhimself.fantazia.events;

import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.advanced.healing.HealingType;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.KeyBinding;
import net.arkadiyhimself.fantazia.api.items.IChangingIcon;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.client.models.entity.ftzentities.ThrownHatchetRenderer;
import net.arkadiyhimself.fantazia.client.models.item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.data.criteritas.ObtainTalentTrigger;
import net.arkadiyhimself.fantazia.data.tags.HealingTypeTagsProvider;
import net.arkadiyhimself.fantazia.data.tags.MobEffectTagsProvider;
import net.arkadiyhimself.fantazia.data.tags.SpellTagProvider;
import net.arkadiyhimself.fantazia.particless.*;
import net.arkadiyhimself.fantazia.registries.*;
import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.registries.DataPackRegistryEvent;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraftforge.registries.RegistryObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;

@Mod.EventBusSubscriber(modid = Fantazia.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryEvents {
    private RegistryEvents() {}
    public static final List<RegistryObject<Item>> ARTIFACTS = new ArrayList<>();
    public static final List<RegistryObject<Item>> WEAPONS = new ArrayList<>();
    public static final List<RegistryObject<Item>> EXPENDABLES = new ArrayList<>();
    private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
        return new ModifierLayer<>();
    }
    @SubscribeEvent
    public static void attributeModification(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FTZAttributes.MANA_REGEN_MULTIPLIER.get());
        event.add(EntityType.PLAYER, FTZAttributes.STAMINA_REGEN_MULTIPLIER.get());
        event.add(EntityType.PLAYER, FTZAttributes.MAX_MANA.get());
        event.add(EntityType.PLAYER, FTZAttributes.MAX_STAMINA.get());
        event.add(EntityType.PLAYER, FTZAttributes.CAST_RANGE_ADDITION.get());
        for (EntityType<? extends LivingEntity> entityType : event.getTypes()) {
            event.add(entityType, FTZAttributes.MAX_STUN_POINTS.get());
            event.add(entityType, FTZAttributes.LIFESTEAL.get());
            event.add(entityType, FTZAttributes.EVASION.get());
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
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerGui(RegisterGuiOverlaysEvent event) {
        event.registerAboveAll("ftz_gui", FTZGuis.FTZ_GUI);
        event.registerAboveAll("obtained_wisdom", FTZGuis.OBTAINED_WISDOM);
        event.registerAboveAll("curioslots", FTZGuis.CURIO_SLOTS);
        event.registerBelowAll("ancient_burning", FTZGuis.ANCIENT_FLAME);
        event.registerAboveAll("auras", FTZGuis.AURAS);
        event.registerAboveAll("developer_mode", FTZGuis.DEVELOPER_MODE);
        event.registerBelowAll("frozen_effect", FTZGuis.FROZEN_EFFECT);
        event.registerBelowAll("fury_veins", FTZGuis.FURY_VEINS);
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(FTZEntityTypes.HATCHET.get(), ThrownHatchetRenderer::new);
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
        List<RegistryObject<Item>> items = new ArrayList<>();
        items.addAll(ARTIFACTS);
        items.addAll(WEAPONS);
        items.addAll(EXPENDABLES);
        for (RegistryObject<Item> item : items) if (item.get() instanceof IChangingIcon icon) icon.registerVariants();
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Fantazia.res("animation"), 42, RegistryEvents::registerPlayerAnimation);
    }
    @SubscribeEvent
    public static void commonSetup(FMLCommonSetupEvent event) {
        CriteriaTriggers.register(ObtainTalentTrigger.INSTANCE);
    }
    @OnlyIn(Dist.CLIENT)
    @SubscribeEvent
    public static void creativeTabContents(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab tab = event.getTab();
        if (tab == FTZCreativeModeTabs.ARTIFACTS) for (RegistryObject<Item> item : ARTIFACTS) event.accept(item);
        if (tab == FTZCreativeModeTabs.WEAPONS) for (RegistryObject<Item> item : WEAPONS) event.accept(item);
        if (tab == FTZCreativeModeTabs.EXPENDABLES) for (RegistryObject<Item> item : EXPENDABLES) event.accept(item);
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
}
