package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.fantazia.advanced.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.advanced.capability.entity.talent.TalentGetter;
import net.arkadiyhimself.fantazia.advanced.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.advanced.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.advanced.cleansing.CleanseStrength;
import net.arkadiyhimself.fantazia.client.gui.FTZGuis;
import net.arkadiyhimself.fantazia.client.models.entity.ftzentities.hatchet.ThrownHatchetRenderer;
import net.arkadiyhimself.fantazia.client.models.item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.particless.*;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.util.KeyBinding;
import net.arkadiyhimself.fantazia.util.interfaces.IChangingIcon;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.*;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Fantazia.MODID)
public class Fantazia
{
    public static final String MODID = "fantazia";
    private static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();
    public static final boolean DEVELOPER_MODE = true;
    public Fantazia()
    {
        GeckoLib.initialize();
        NetworkHandler.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        MinecraftForge.EVENT_BUS.register(this);

        // capabilities
        AbilityGetter.register();
        TalentGetter.register();
        EffectGetter.register();
        DataGetter.register();
        FeatureGetter.register();

        StackDataGetter.register();

        // level cap
        LevelCapGetter.register();

        // registries
        this.load(FTZEnchantments.class);
        this.load(FTZMobEffects.class);
        this.load(FTZSoundEvents.class);
        this.load(FTZAttributes.class);
        this.load(FTZBlocks.class);
        this.load(FTZEntityTypes.class);
        this.load(FTZParticleTypes.class);
        this.load(FTZItems.class);
        this.load(FTZCreativeModeTabs.class);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::attributeAdding);
        modEventBus.addListener(this::modelRegistry);
    }
    private void load(Class<?> tClass) {
        try {
            Class.forName(tClass.getName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Something's wrong I can feel it...");
        }
    }

    private void commonSetup(final FMLCommonSetupEvent event) {
        CleanseStrength.onSetup();
    }
    private void attributeAdding(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, FTZAttributes.MANA_REGEN_MULTIPLIER);
        event.add(EntityType.PLAYER, FTZAttributes.STAMINA_REGEN_MULTIPLIER);
        event.add(EntityType.PLAYER, FTZAttributes.MAX_MANA);
        event.add(EntityType.PLAYER, FTZAttributes.MAX_STAMINA);
        event.add(EntityType.PLAYER, FTZAttributes.CAST_RANGE_ADDITION);
        event.getTypes().forEach(entityType -> {
            event.add(entityType, FTZAttributes.MAX_STUN_POINTS);
            event.add(entityType, FTZAttributes.LIFESTEAL);
        });
    }
    public static ResourceLocation res(String id) {
        return new ResourceLocation(MODID, id);
    }
    public static ModelResourceLocation itemModelRes(String id) {
        return new ModelResourceLocation(res(id), "inventory");
    }

    public void modelRegistry(ModelEvent.RegisterAdditional event) {
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
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class CommonRegistry {
        public static ArrayList<RegistryObject<Item>> MAGIC_ITEM = new ArrayList<>();
        public static ArrayList<RegistryObject<Item>> WEAPON_ITEM = new ArrayList<>();
    }
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModRegistry {
        private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
            return new ModifierLayer<>();
        }
        @SubscribeEvent
        public static void registerGui(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("double_jump_icon", FTZGuis.DOUBLE_JUMP_ICON);
            event.registerAboveAll("dash_icon", FTZGuis.DASH_ICON);
            event.registerAboveAll("ftz_gui", FTZGuis.FTZ_GUI);
            event.registerAboveAll("curioslots", FTZGuis.CURIOSLOTS);
            event.registerBelowAll("ancient_burning", FTZGuis.ANCIENT_FLAME);
            event.registerAboveAll("auras", FTZGuis.AURAS);
            event.registerAboveAll("developer_mode", FTZGuis.DEVELOPER_MODE);
        }
        @SubscribeEvent
        public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(FTZEntityTypes.HATCHET, ThrownHatchetRenderer::new);
        }
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
          //  event.registerLayerDefinition(HatchetModel.LAYER_LOCATION, HatchetModel::createBodyLayer);
        }
        @SubscribeEvent
        public static void keyBinding(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.DASH);
            event.register(KeyBinding.BLOCK);
            event.register(KeyBinding.SWORD_ABILITY);
            event.register(KeyBinding.SPELLCAST1);
            event.register(KeyBinding.SPELLCAST2);
            event.register(KeyBinding.TALENTS);
        }
        @SubscribeEvent
        static void registerParticles(final RegisterParticleProvidersEvent event) {
            event.registerSpriteSet(FTZParticleTypes.BLOOD1, BloodParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BLOOD2, BloodParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BLOOD3, BloodParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BLOOD4, BloodParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BLOOD5, BloodParticle.Provider::new);

            event.registerSpriteSet(FTZParticleTypes.DOOMED_SOUL1, SoulParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.DOOMED_SOUL2, SoulParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.DOOMED_SOUL3, SoulParticle.Provider::new);

            event.registerSpriteSet(FTZParticleTypes.FALLEN_SOUL, FallenSoulParticle.Provider::new);

            event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE1, BarrierParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE2, BarrierParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE3, BarrierParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE4, BarrierParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.BARRIER_PIECE5, BarrierParticle.Provider::new);

            event.registerSpriteSet(FTZParticleTypes.LIFESTEAL1, GenericParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.LIFESTEAL2, GenericParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.LIFESTEAL3, GenericParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.LIFESTEAL4, GenericParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.LIFESTEAL5, GenericParticle.Provider::new);

            event.registerSpriteSet(FTZParticleTypes.REGEN1, GenericParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.REGEN2, GenericParticle.Provider::new);
            event.registerSpriteSet(FTZParticleTypes.REGEN3, GenericParticle.Provider::new);
        }
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            List<RegistryObject<Item>> items = new ArrayList<>();
            items.addAll(Fantazia.CommonRegistry.MAGIC_ITEM);
            items.addAll(Fantazia.CommonRegistry.WEAPON_ITEM);
            for (RegistryObject<Item> item : items) if (item.get() instanceof IChangingIcon icon) icon.registerVariants();
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(Fantazia.res("animation"), 42, Fantazia.ModRegistry::registerPlayerAnimation);
        }
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab tab = event.getTab();
        if (tab == FTZCreativeModeTabs.FTZ_MAGIC) for (RegistryObject<Item> item : CommonRegistry.MAGIC_ITEM) event.accept(item);
        if (tab == FTZCreativeModeTabs.FTZ_WEAPONS) for (RegistryObject<Item> item : CommonRegistry.WEAPON_ITEM) event.accept(item);
    }
    private static CustomItemRenderer CUSTOM_RENDERER = null;
    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        if (CUSTOM_RENDERER == null) CUSTOM_RENDERER = new CustomItemRenderer();
        return CUSTOM_RENDERER;
    }
}
