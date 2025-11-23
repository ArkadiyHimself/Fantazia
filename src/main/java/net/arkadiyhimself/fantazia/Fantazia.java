package net.arkadiyhimself.fantazia;

import com.mojang.logging.LogUtils;
import net.arkadiyhimself.fantazia.client.renderers.item.FantazicItemRenderer;
import net.arkadiyhimself.fantazia.common.api.curio.CurioValidator;
import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.common.registries.custom.*;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEffectComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentEntityEffects;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentLocationBasedEffects;
import net.arkadiyhimself.fantazia.common.registries.enchantment_effect_component.FTZEnchantmentValueEffects;
import net.arkadiyhimself.fantazia.mixin.BossHealthOverlayAccessor;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BlockEntityWithoutLevelRenderer;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import org.slf4j.Logger;
import top.theillusivec4.curios.api.CuriosApi;

@Mod(Fantazia.MODID)
public class Fantazia {

    public static final String MODID = "fantazia";
    public static final Logger LOGGER = LogUtils.getLogger();
    public static final boolean DEVELOPER_MODE = true;
    private static FantazicItemRenderer CUSTOM_RENDERER = null;
    private static BossHealthOverlayAccessor accessor = null;

    public Fantazia(IEventBus modEventBus, ModContainer modContainer) {
        FantazicConfig.setup(modContainer);

        // custom registries
        Spells.register(modEventBus);
        Auras.register(modEventBus);
        Runes.register(modEventBus);
        ToolCapacityFunctions.register(modEventBus);
        ToolDamageFunctions.register(modEventBus);
        Blueprints.register(modEventBus);

        // vanilla registry
        FTZAttachmentTypes.register(modEventBus);
        FTZAttributes.register(modEventBus);
        FTZBlocks.register(modEventBus);
        FTZCreativeModeTabs.register(modEventBus);
        FTZCriterionTriggers.register(modEventBus);
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
        FTZMenuTypes.register(modEventBus);
        FTZRecipeTypes.register(modEventBus);

        FTZEnchantmentEffectComponentTypes.register(modEventBus);
        FTZEnchantmentLocationBasedEffects.register(modEventBus);
        FTZEnchantmentEntityEffects.register(modEventBus);
        FTZEnchantmentValueEffects.register(modEventBus);


        for (CurioValidator validator : CurioValidator.VALIDATORS.values())
            CuriosApi.registerCurioPredicate(validator.id(), validator.function());
    }

    public static BlockEntityWithoutLevelRenderer getItemsRenderer() {
        if (CUSTOM_RENDERER == null) CUSTOM_RENDERER = new FantazicItemRenderer();
        return CUSTOM_RENDERER;
    }

    public static ResourceLocation location(String id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id);
    }

    public static ResourceLocation changeNamespace(ResourceLocation id) {
        return ResourceLocation.fromNamespaceAndPath(MODID, id.getPath());
    }

    public static ModelResourceLocation modelLocation(ResourceLocation id) {
        return ModelResourceLocation.standalone(id);
    }

    public static ModelResourceLocation modelLocation(String id) {
        return modelLocation(location(id));
    }

    public static <T> ResourceKey<Registry<T>> resKey(String name) {
        return ResourceKey.createRegistryKey(Fantazia.location(name));
    }

    public static <T> TagKey<T> tagKey(ResourceKey<Registry<T>> registry, String name) {
        return TagKey.create(registry, location(name));
    }

    public static BossHealthOverlayAccessor getBossBarOverlay() {
        if (accessor == null) accessor = (BossHealthOverlayAccessor) Minecraft.getInstance().gui.getBossOverlay();
        return accessor;
    }

    public static boolean loadedJEI() {
        return ModList.get().isLoaded("jei");
    }

}
