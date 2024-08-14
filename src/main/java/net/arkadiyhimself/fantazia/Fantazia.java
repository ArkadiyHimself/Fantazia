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
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZHealingTypes;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import software.bernie.geckolib.GeckoLib;

import java.util.Random;

@Mod(Fantazia.MODID)
public class Fantazia {
    public static final String MODID = "fantazia";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();
    public static final boolean DEVELOPER_MODE = true;
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

        // custom registry
        load(FTZSpells.class);
        load(FTZAuras.class);
        load(FTZHealingTypes.class);

        // vanilla registry
        load(FTZEnchantments.class);
        load(FTZMobEffects.class);
        load(FTZSoundEvents.class);
        load(FTZAttributes.class);
        load(FTZBlocks.class);
        load(FTZEntityTypes.class);
        load(FTZParticleTypes.class);
        load(FTZItems.class);
        load(FTZCreativeModeTabs.class);
        load(FTZLootModifiers.class);
    }
    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        return CUSTOM_RENDERER;
    }
    public static void load(Class<?> tClass) {
        try {
            Class.forName(tClass.getName());
        } catch (ClassNotFoundException ex) {
            throw new IllegalStateException("Something's wrong I can feel it...");
        }
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
}
