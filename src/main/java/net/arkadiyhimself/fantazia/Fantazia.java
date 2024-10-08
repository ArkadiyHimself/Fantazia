package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import net.arkadiyhimself.fantazia.client.models.item.CustomItemRenderer;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.FTZAuras;
import net.arkadiyhimself.fantazia.registries.custom.FTZSpells;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.ServerAdvancementManager;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.levelgen.structure.pools.LegacySinglePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import top.theillusivec4.curios.common.util.EquipCurioTrigger;

import java.util.Random;
import java.util.function.Function;

@Mod(Fantazia.MODID)
public class Fantazia {
    public static final String MODID = "fantazia";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();
    public static final boolean DEVELOPER_MODE = false;
    private static final CustomItemRenderer CUSTOM_RENDERER = new CustomItemRenderer();

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
    }

    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
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

    public static Function<StructureTemplatePool.Projection, LegacySinglePoolElement> pool(String name) {
        return StructurePoolElement.legacy(MODID + ":" + name);
    }
}
