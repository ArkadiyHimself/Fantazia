package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import net.arkadiyhimself.fantazia.client.renderers.item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;

import java.util.Random;

@Mod(Fantazia.MODID)
public class Fantazia {

    public static final String MODID = "fantazia";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();
    public static final boolean DEVELOPER_MODE = false;
    private static CustomItemRenderer CUSTOM_RENDERER = null;

    public Fantazia(IEventBus modEventBus, ModContainer modContainer) {
        FantazicConfig.setup(modContainer);

        // custom registries
        FTZSpells.register(modEventBus);
        FTZAuras.register(modEventBus);

        // vanilla registry
        FTZAttachmentTypes.register(modEventBus);
        FTZAttributes.register(modEventBus);
        FTZBlocks.register(modEventBus);
        FTZCreativeModeTabs.register(modEventBus);
        FTZCriterionTtiggers.register(modEventBus);
        FTZDataComponentTypes.register(modEventBus);
        FTZEntityTypes.register(modEventBus);
        FTZItems.register(modEventBus);
        FTZLootModifiers.register(modEventBus);
        FTZMobEffects.register(modEventBus);
        FTZParticleTypes.register(modEventBus);
        FTZSoundEvents.register(modEventBus);
        FTZStructureTypes.register(modEventBus);
        FTZPotions.register(modEventBus);
        FTZBlockEntityTypes.register(modEventBus);
    }

    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        if (CUSTOM_RENDERER == null) CUSTOM_RENDERER = new CustomItemRenderer();
        return CUSTOM_RENDERER;
    }

    public static ResourceLocation res(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    public static ModelResourceLocation modelRes(String id) {
        return ModelResourceLocation.standalone(res(id));
    }

    public static <T> ResourceKey<Registry<T>> resKey(String name) {
        return ResourceKey.createRegistryKey(Fantazia.res(name));
    }
}
