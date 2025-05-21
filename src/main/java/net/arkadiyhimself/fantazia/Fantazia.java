package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import net.arkadiyhimself.fantazia.api.curio.CurioValidator;
import net.arkadiyhimself.fantazia.client.renderers.item.FantazicItemRenderer;
import net.arkadiyhimself.fantazia.mixin.BossHealthOverlayAccessor;
import net.arkadiyhimself.fantazia.registries.*;
import net.arkadiyhimself.fantazia.registries.custom.Auras;
import net.arkadiyhimself.fantazia.registries.custom.Runes;
import net.arkadiyhimself.fantazia.registries.custom.Spells;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

import java.util.Random;

@Mod(Fantazia.MODID)
public class Fantazia {

    public static final String MODID = "fantazia";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final Random RANDOM = new Random();
    public static final boolean DEVELOPER_MODE = false;
    private static FantazicItemRenderer CUSTOM_RENDERER = null;
    private static BossHealthOverlayAccessor accessor = null;

    public Fantazia(IEventBus modEventBus, ModContainer modContainer) {
        FantazicConfig.setup(modContainer);

        // custom registries
        Spells.register(modEventBus);
        Auras.register(modEventBus);
        Runes.register(modEventBus);

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
        FTZGameRules.onModSetup();

        for (CurioValidator validator : CurioValidator.VALIDATORS.values()) registerCurioValidator(validator);

    }

    private static void registerCurioValidator(CurioValidator validator) {
        CuriosApi.registerCurioPredicate(validator.id(), validator.function());
    }

    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        if (CUSTOM_RENDERER == null) CUSTOM_RENDERER = new FantazicItemRenderer();
        return CUSTOM_RENDERER;
    }

    public static ResourceLocation res(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    public static ResourceLocation changeNamespace(ResourceLocation id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id.getPath());
    }

    public static ModelResourceLocation modelRes(ResourceLocation id) {
        return ModelResourceLocation.standalone(id);
    }

    public static ModelResourceLocation modelRes(String id) {
        return modelRes(res(id));
    }

    public static <T> ResourceKey<Registry<T>> resKey(String name) {
        return ResourceKey.createRegistryKey(Fantazia.res(name));
    }

    public static BossHealthOverlayAccessor getBossBarOverlay() {
        if (accessor == null) accessor = (BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay();
        return accessor;
    }

}
