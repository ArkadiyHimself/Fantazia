package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.fantazia.AdvancedMechanics.CleanseManager.CleanseStrength;
import net.arkadiyhimself.fantazia.Networking.NetworkHandler;
import net.arkadiyhimself.fantazia.Particless.Types.BarrierPiece;
import net.arkadiyhimself.fantazia.Particless.Types.Blood;
import net.arkadiyhimself.fantazia.Particless.Types.DoomedSouls;
import net.arkadiyhimself.fantazia.Particless.Types.FallenSoul;
import net.arkadiyhimself.fantazia.api.*;
import net.arkadiyhimself.fantazia.client.Gui.FTZGui;
import net.arkadiyhimself.fantazia.client.Models.Entity.NewEntitites.Hatchet.HatchetModel;
import net.arkadiyhimself.fantazia.client.Models.Entity.NewEntitites.Hatchet.HatchetRenderer;
import net.arkadiyhimself.fantazia.client.Models.Item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.util.Capability.Entity.AbilityManager.AbilityGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.EffectManager.EffectGetter;
import net.arkadiyhimself.fantazia.util.Capability.Entity.TalentData.TalentGetter;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.Common.AttachCommonItem;
import net.arkadiyhimself.fantazia.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.fantazia.util.Capability.Level.LevelCapGetter;
import net.arkadiyhimself.fantazia.util.Interfaces.IChangingIcon;
import net.arkadiyhimself.fantazia.util.KeyBinding;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
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

// The value here should match an entry in the META-INF/mods.toml file
@Mod(Fantazia.MODID)
public class Fantazia
{
    public static final String MODID = "fantazia";
    private static final Logger LOGGER = LogUtils.getLogger();
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

        // item stack caps
        AttachFragileBlade.register();
        AttachCommonItem.register();

        // level cap
        LevelCapGetter.register();

        // registries
        MobEffectRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        ParticleRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        AttributeRegistry.register(modEventBus);
        LootModifierRegistry.register(modEventBus);
        EntityTypeRegistry.register(modEventBus);
        EnchantmentRegistry.register(modEventBus);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::attributeAdding);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::modelRegistry);
    }
    public void clientSetup(final FMLClientSetupEvent event) {
        List<RegistryObject<Item>> items = new ArrayList<>();
        items.addAll(commonRegistry.MagicItems);
        items.addAll(commonRegistry.WeaponItems);
        items.forEach(item -> {
            if (item.get() instanceof IChangingIcon icon) {
                icon.registerVariants();
            }
        });
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(new ResourceLocation(MODID, "animation"), 42, commonRegistry::registerPlayerAnimation);
        ItemBlockRenderTypes.setRenderLayer(BlockRegistry.ANCIENT_FLAME.get(), RenderType.cutout());
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        MobEffects.DARKNESS.addAttributeModifier(AttributeRegistry.CAST_RANGE_TOTAL_MULTIPLIER.get(), "43f03312-3ac8-42fb-bee5-a3d26cc44feb", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        MobEffects.BLINDNESS.addAttributeModifier(AttributeRegistry.CAST_RANGE_TOTAL_MULTIPLIER.get(), "889f3c17-ec34-4e02-a4cc-f7541799d690", 0.5F, AttributeModifier.Operation.ADDITION);

        CleanseStrength.onSetup();
    }
    private void attributeAdding(final EntityAttributeModificationEvent event) {
        event.add(EntityType.PLAYER, AttributeRegistry.MANA_REGEN_MULTIPLIER.get());
        event.add(EntityType.PLAYER, AttributeRegistry.STAMINA_REGEN_MULTIPLIER.get());
        event.add(EntityType.PLAYER, AttributeRegistry.MAX_MANA.get());
        event.add(EntityType.PLAYER, AttributeRegistry.MAX_STAMINA.get());
        event.add(EntityType.PLAYER, AttributeRegistry.CAST_RANGE_BASE_MULTIPLIER.get());
        event.add(EntityType.PLAYER, AttributeRegistry.CAST_RANGE_ADDITION.get());
        event.add(EntityType.PLAYER, AttributeRegistry.CAST_RANGE_TOTAL_MULTIPLIER.get());
        event.getTypes().forEach(entityType -> {
            event.add(entityType, AttributeRegistry.MAX_STUN_POINTS.get());
            event.add(entityType, AttributeRegistry.LIFESTEAL.get());
        });
    }
    public static ResourceLocation res(String id) {
        return new ResourceLocation(MODID, id);
    }
    public static ModelResourceLocation itemModelRes(String id) {
        ModelResourceLocation model = new ModelResourceLocation(res(id), "inventory");
        return model;
    }

    public void modelRegistry(ModelEvent.RegisterAdditional event) {
        event.register(CustomItemRenderer.BLADE0);
        event.register(CustomItemRenderer.BLADE1);
        event.register(CustomItemRenderer.BLADE2);
        event.register(CustomItemRenderer.BLADE3);
        event.register(CustomItemRenderer.BLADE4);
        event.register(CustomItemRenderer.BLADE_MODEL);
    }
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class commonRegistry {
        public static ArrayList<RegistryObject<Item>> MagicItems = new ArrayList<>();
        public static ArrayList<RegistryObject<Item>> WeaponItems = new ArrayList<>();
        private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
            return new ModifierLayer<>();
        }
    }
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModRegistry
    {
        @SubscribeEvent
        public static void registerGui(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("double_jump_icon", FTZGui.DOUBLE_JUMP_ICON);
            event.registerAboveAll("dash_icon", FTZGui.DASH_ICON);
            event.registerAboveAll("ftz_gui", FTZGui.FTZ_GUI);
            event.registerAboveAll("curioslots", FTZGui.CURIOSLOTS);
            event.registerBelowAll("ancient_burning", FTZGui.ANCIENT_BURNING);
            event.registerAboveAll("auras", FTZGui.AURAS);
            event.registerAboveAll("developer_mode", FTZGui.DEVELOPER_MODE);
        }
        @SubscribeEvent
        public static void registerRenderer(EntityRenderersEvent.RegisterRenderers event) {
            event.registerEntityRenderer(EntityTypeRegistry.HATCHET.get(), HatchetRenderer::new);
        }
        @SubscribeEvent
        public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
            event.registerLayerDefinition(HatchetModel.LAYER_LOCATION, HatchetModel::createBodyLayer);
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
            event.registerSpriteSet(ParticleRegistry.BLOOD1.get(), Blood.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BLOOD2.get(), Blood.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BLOOD3.get(), Blood.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BLOOD4.get(), Blood.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BLOOD5.get(), Blood.Provider::new);

            event.registerSpriteSet(ParticleRegistry.DOOMED_SOUL1.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.DOOMED_SOUL2.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.DOOMED_SOUL3.get(), DoomedSouls.Provider::new);

            event.registerSpriteSet(ParticleRegistry.FALLEN_SOUL.get(), FallenSoul.Provider::new);

            event.registerSpriteSet(ParticleRegistry.BARRIER_PIECE1.get(), BarrierPiece.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BARRIER_PIECE2.get(), BarrierPiece.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BARRIER_PIECE3.get(), BarrierPiece.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BARRIER_PIECE4.get(), BarrierPiece.Provider::new);
            event.registerSpriteSet(ParticleRegistry.BARRIER_PIECE5.get(), BarrierPiece.Provider::new);

            event.registerSpriteSet(ParticleRegistry.LIFESTEAL1.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.LIFESTEAL2.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.LIFESTEAL3.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.LIFESTEAL4.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.LIFESTEAL5.get(), DoomedSouls.Provider::new);

            event.registerSpriteSet(ParticleRegistry.REGEN1.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.REGEN2.get(), DoomedSouls.Provider::new);
            event.registerSpriteSet(ParticleRegistry.REGEN3.get(), DoomedSouls.Provider::new);
        }
    }
    private void addCreative(BuildCreativeModeTabContentsEvent event) {
        CreativeModeTab tab = event.getTab();
        if (tab == ItemRegistry.MAGIC_TAB.get()) {
            for (RegistryObject<Item> object : commonRegistry.MagicItems) {
                event.accept(object);
            }
        } else if (tab == ItemRegistry.WEAPON_TAB.get()) {
            for (RegistryObject<Item> object : commonRegistry.WeaponItems) {
                event.accept(object);
            }
        }
    }
    private static CustomItemRenderer CUSTOM_RENDERER = null;
    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        if(CUSTOM_RENDERER == null) {
            CUSTOM_RENDERER = new CustomItemRenderer();
        }
        return CUSTOM_RENDERER;
    }
}
