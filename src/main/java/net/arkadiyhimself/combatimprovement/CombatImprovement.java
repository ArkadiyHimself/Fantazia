package net.arkadiyhimself.combatimprovement;

import com.mojang.logging.LogUtils;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.combatimprovement.client.Render.Gui.CombatGui;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.WhereMagicHappens;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Registries.AttributeRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Blocks.BlockRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Entities.EntityTypeRegistry;
import net.arkadiyhimself.combatimprovement.client.Render.Models.Entity.NewEntitites.Hatchet.HatchetRenderer;
import net.arkadiyhimself.combatimprovement.Registries.Items.ItemRegistry;
import net.arkadiyhimself.combatimprovement.Registries.LootModifierRegistry;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Particless.ParticleRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Particless.Types.BarrierPiece;
import net.arkadiyhimself.combatimprovement.Registries.Particless.Types.Blood;
import net.arkadiyhimself.combatimprovement.Registries.Particless.Types.DoomedSouls;
import net.arkadiyhimself.combatimprovement.Registries.Particless.Types.FallenSoul;
import net.arkadiyhimself.combatimprovement.Registries.SoundRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.FragileBladeCap;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.AbsoluteBarrier.AbsoluteBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.arkadiyhimself.combatimprovement.util.KeyBinding;
import net.minecraft.ChatFormatting;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
import net.minecraftforge.event.entity.EntityAttributeModificationEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.InterModEnqueueEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegistryObject;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.ArrayList;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(CombatImprovement.MODID)
public class CombatImprovement
{
    public static final String MODID = "combatimprovement";
    private static final Logger LOGGER = LogUtils.getLogger();
    public CombatImprovement()
    {
        GeckoLib.initialize();
        NetworkHandler.register();
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();


        MinecraftForge.EVENT_BUS.register(this);


        // capabilities
        AttachDataSync.register();
        AttachBlocking.register();
        AttachDash.register();
        AttachDJump.register();

        // mob effect caps
        StunEffect.register();
        BarrierEffect.register();
        LayeredBarrierEffect.register();
        AbsoluteBarrierEffect.register();

        // itemstack caps
        AttachFragileBlade.register();

        // registries
        MobEffectRegistry.register(modEventBus);
        SoundRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        ParticleRegistry.register(modEventBus);
        BlockRegistry.register(modEventBus);
        AttributeRegistry.register(modEventBus);
        LootModifierRegistry.register(modEventBus);
        EntityTypeRegistry.register(modEventBus);

        modEventBus.addListener(this::addCurio);
        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
        modEventBus.addListener(this::clientSetup);
        modEventBus.addListener(this::attributeAdding);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        MobEffects.DARKNESS.addAttributeModifier(AttributeRegistry.CAST_RANGE_TOTAL_MULTIPLIER.get(), "43f03312-3ac8-42fb-bee5-a3d26cc44feb", 0.5f, AttributeModifier.Operation.MULTIPLY_TOTAL);
        MobEffects.BLINDNESS.addAttributeModifier(AttributeRegistry.CAST_RANGE_TOTAL_MULTIPLIER.get(), "889f3c17-ec34-4e02-a4cc-f7541799d690", 0.5F, AttributeModifier.Operation.ADDITION);
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
        });
    }
    public void clientSetup(final FMLClientSetupEvent event) {
        PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                new ResourceLocation(MODID, "animation"),
                42, commonRegistry::registerPlayerAnimation);
        EntityRenderers.register(EntityTypeRegistry.HATCHET.get(), HatchetRenderer::new);
        event.enqueueWork(() -> {
            ItemProperties.register(ItemRegistry.FRAGILE_BLADE.get(), new ResourceLocation(CombatImprovement.MODID, "dmg"),
                    ((pStack, pLevel, pEntity, pSeed) -> {
                        FragileBladeCap cap = AttachFragileBlade.getUnwrap(pStack);
                        if (pEntity == null || cap == null) {
                            return 3;
                        } else {
                            return 3;
                        }
                    }));
        });
    }

    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event) {}
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class commonRegistry {
        public static ArrayList<RegistryObject<Item>> ModItems = new ArrayList<>();
        public static CreativeModeTab COMBAT_TAB;
        private static IAnimation registerPlayerAnimation(AbstractClientPlayer player) {
            return new ModifierLayer<>();
        }
        @SubscribeEvent
        public static void registerCreativeModeTab(CreativeModeTabEvent.Register event) {
            COMBAT_TAB = event.registerCreativeModeTab(new ResourceLocation(MODID, "tab"),
                    builder -> builder.icon(() -> new ItemStack(ItemRegistry.FRAGILE_BLADE.get()))
                            .title(Component.translatable("Combat Improvement").withStyle(ChatFormatting.DARK_PURPLE)));
        }
    }
    @Mod.EventBusSubscriber(modid = MODID, value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class ModRegistry
    {
        @SubscribeEvent
        public static void registerGui(RegisterGuiOverlaysEvent event) {
            event.registerAboveAll("djump_icon", CombatGui.DJUMP_ICON);
            event.registerAboveAll("dash_icon", CombatGui.DASH_ICON);
            event.registerAboveAll("barrier_layers", CombatGui.BARRIER_LAYERS);
            event.registerAboveAll("frag_sword_dmg", CombatGui.FRAG_SWORD_DMG);
          //  event.registerBelowAll("manapool", CombatGui.MANAPOOL);
            event.registerAboveAll("combatgui", CombatGui.COMBATGUI);
            event.registerAboveAll("curioslots", CombatGui.CURIOSLOTS);
        }
        @SubscribeEvent
        public static void keyBinding(RegisterKeyMappingsEvent event) {
            event.register(KeyBinding.DASH);
            event.register(KeyBinding.BLOCK);
            event.register(KeyBinding.SWORD_ABILITY);
            event.register(KeyBinding.SPELLCAST1);
            event.register(KeyBinding.SPELLCAST2);
        }
        @SubscribeEvent
        static void registerParticles(final RegisterParticleProvidersEvent event) {
            event.register(ParticleRegistry.BLOOD1.get(), Blood.Provider::new);
            event.register(ParticleRegistry.BLOOD2.get(), Blood.Provider::new);
            event.register(ParticleRegistry.BLOOD3.get(), Blood.Provider::new);
            event.register(ParticleRegistry.BLOOD4.get(), Blood.Provider::new);
            event.register(ParticleRegistry.BLOOD5.get(), Blood.Provider::new);

            event.register(ParticleRegistry.DOOMED_SOUL1.get(), DoomedSouls.Provider::new);
            event.register(ParticleRegistry.DOOMED_SOUL2.get(), DoomedSouls.Provider::new);
            event.register(ParticleRegistry.DOOMED_SOUL3.get(), DoomedSouls.Provider::new);

            event.register(ParticleRegistry.FALLEN_SOUL.get(), FallenSoul.Provider::new);

            event.register(ParticleRegistry.BARRIER_PIECE1.get(), BarrierPiece.Provider::new);
            event.register(ParticleRegistry.BARRIER_PIECE2.get(), BarrierPiece.Provider::new);
            event.register(ParticleRegistry.BARRIER_PIECE3.get(), BarrierPiece.Provider::new);
            event.register(ParticleRegistry.BARRIER_PIECE4.get(), BarrierPiece.Provider::new);
            event.register(ParticleRegistry.BARRIER_PIECE5.get(), BarrierPiece.Provider::new);
        }
    }

    private void addCurio(InterModEnqueueEvent event) {
        WhereMagicHappens.RegisterStuff.registerCurio("dashstone", 1, false, new ResourceLocation(MODID, "slots/dashstone"));
        WhereMagicHappens.RegisterStuff.registerCurio("spellcaster", 2, false, new ResourceLocation(MODID, "slots/spellcaster"));
        WhereMagicHappens.RegisterStuff.registerCurio("passivecaster", 2, false, new ResourceLocation(MODID, "slots/passivecaster"));
    }
    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == commonRegistry.COMBAT_TAB) {
            for (RegistryObject<Item> object : commonRegistry.ModItems) {
                event.accept(object);
            }
        }
    }
}
