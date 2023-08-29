package net.arkadiyhimself.combatimprovement;

import com.mojang.logging.LogUtils;
import dev.kosmx.playerAnim.api.layered.IAnimation;
import dev.kosmx.playerAnim.api.layered.ModifierLayer;
import dev.kosmx.playerAnim.minecraftApi.PlayerAnimationFactory;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.ClientEvents;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.CombatGui;
import net.arkadiyhimself.combatimprovement.HandlersAndHelpers.UsefulMethods;
import net.arkadiyhimself.combatimprovement.Networking.NetworkHandler;
import net.arkadiyhimself.combatimprovement.Registries.Blocks.BlockRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Items.ItemRegistry;
import net.arkadiyhimself.combatimprovement.Registries.MobEffects.MobEffectRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Particless.ParticleRegistry;
import net.arkadiyhimself.combatimprovement.Registries.Particless.Types.BloodParticle;
import net.arkadiyhimself.combatimprovement.Registries.Sounds.SoundRegistry;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Blocking.AttachBlocking;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DJump.AttachDJump;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.Dash.AttachDash;
import net.arkadiyhimself.combatimprovement.util.Capability.Abilities.DataSincyng.AttachDataSync;
import net.arkadiyhimself.combatimprovement.util.Capability.ItemStack.FragileSword.AttachFragileBlade;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.AbsoluteBarrier.AbsoluteBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.BarrierEffect.BarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.LayeredBarrierEffect.LayeredBarrierEffect;
import net.arkadiyhimself.combatimprovement.util.Capability.mobeffects.StunEffect.StunEffect;
import net.arkadiyhimself.combatimprovement.util.KeyBinding;
import net.minecraft.ChatFormatting;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.AbstractClientPlayer;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterGuiOverlaysEvent;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import net.minecraftforge.client.event.RegisterParticleProvidersEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.CreativeModeTabEvent;
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
        modEventBus.addListener(this::addCurio);

        modEventBus.addListener(this::addCreative);
        modEventBus.addListener(this::commonSetup);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
    }


    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
    public static class commonRegistry {
        public static ArrayList<RegistryObject<Item>> ModItems = new ArrayList<>();
        public static CreativeModeTab COMBAT_TAB;
        @SubscribeEvent
        public static void clientSetup(FMLClientSetupEvent event) {
            PlayerAnimationFactory.ANIMATION_DATA_FACTORY.registerFactory(
                    new ResourceLocation(MODID, "animation"),
                    42, commonRegistry::registerPlayerAnimation);
        }
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
            event.registerAboveAll("dash_icon", CombatGui.DASH_ICON);
            event.registerAboveAll("barrier_layers", CombatGui.BARRIER_LAYERS);
            event.registerAboveAll("frag_sword_dmg", CombatGui.FRAG_SWORD_DMG);
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
            Minecraft.getInstance().particleEngine.register(ParticleRegistry.BLOOD1.get(), BloodParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticleRegistry.BLOOD2.get(), BloodParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticleRegistry.BLOOD3.get(), BloodParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticleRegistry.BLOOD4.get(), BloodParticle.Provider::new);
            Minecraft.getInstance().particleEngine.register(ParticleRegistry.BLOOD5.get(), BloodParticle.Provider::new);
        }
    }

    private void addCurio(InterModEnqueueEvent event) {
        UsefulMethods.RegisterStuff.registerCurio("dashstone", 1, false, new ResourceLocation(MODID, "slots/dashstone"));
        UsefulMethods.RegisterStuff.registerCurio("spellcaster", 2, false, new ResourceLocation(MODID, "slots/spellcaster"));
    }
    private void addCreative(CreativeModeTabEvent.BuildContents event) {
        if (event.getTab() == commonRegistry.COMBAT_TAB) {
            for (RegistryObject<Item> object : commonRegistry.ModItems) {
                event.accept(object);
            }
        }
    }
}
