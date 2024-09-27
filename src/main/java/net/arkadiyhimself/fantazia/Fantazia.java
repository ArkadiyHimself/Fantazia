package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import net.arkadiyhimself.fantazia.api.FantazicRegistry;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.data.DataGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.effect.EffectGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.feature.FeatureGetter;
import net.arkadiyhimself.fantazia.api.capability.itemstack.StackDataGetter;
import net.arkadiyhimself.fantazia.api.capability.level.LevelCapGetter;
import net.arkadiyhimself.fantazia.client.models.item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.networking.NetworkHandler;
import net.arkadiyhimself.fantazia.registries.*;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Random;
import java.util.function.Function;

@Mod(Fantazia.MODID)
public class Fantazia {
    public static final String MODID = "fantazia";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();
    public static final boolean DEVELOPER_MODE = false;
    private static final CustomItemRenderer CUSTOM_RENDERER = new CustomItemRenderer();
    public Fantazia() {
        GeckoLib.initialize();
        NetworkHandler.register();
        MinecraftForge.EVENT_BUS.register(this);
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();

        // capabilities
        AbilityGetter.register();
        EffectGetter.register();
        DataGetter.register();
        FeatureGetter.register();
        StackDataGetter.register();
        LevelCapGetter.register();

        // custom registries
        FantazicRegistry.register(modEventBus);

        // vanilla registry
        FTZEnchantments.register();
        FTZMobEffects.register();
        FTZSoundEvents.register();
        FTZAttributes.register();
        FTZBlocks.register();
        FTZEntityTypes.register();
        FTZParticleTypes.register();
        FTZItems.register();
        FTZCreativeModeTabs.register();
        FTZLootModifiers.register();
    }
    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        return CUSTOM_RENDERER;
    }
    public static ResourceLocation res(String id) {
        return new ResourceLocation(MODID, id);
    }
    public static ModelResourceLocation itemModelRes(String id) {
        return new ModelResourceLocation(res(id), "inventory");
    }
    public static <T> ResourceKey<Registry<T>> resKey(String name) {
        return ResourceKey.createRegistryKey(Fantazia.res(name));
    }
    public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> pool(String name) {
        return StructurePoolElement.legacy(MODID + ":" + name);
    }
}
