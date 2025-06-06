package net.arkadiyhimself.fantazia.registries;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.talent.wisdom_reward.WisdomRewardInstance;
import net.arkadiyhimself.fantazia.integration.jei.categories.wisdom_reward.RewardPair;
import net.arkadiyhimself.fantazia.recipe.AmplificationRecipe;
import net.arkadiyhimself.fantazia.recipe.EnchantmentReplaceRecipe;
import net.arkadiyhimself.fantazia.recipe.RuneCarvingRecipe;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class FTZRecipeTypes {

    public static final DeferredRegister<RecipeType<?>> TYPES = DeferredRegister.create(Registries.RECIPE_TYPE, Fantazia.MODID);
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS = DeferredRegister.create(Registries.RECIPE_SERIALIZER, Fantazia.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<RuneCarvingRecipe>> RUNE_CARVING = register("rune_carving");
    public static final DeferredHolder<RecipeSerializer<?>, RuneCarvingRecipe.Serializer> RUNE_CARVING_SERIALIZER = SERIALIZERS.register("rune_carving", RuneCarvingRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<AmplificationRecipe>> AMPLIFICATION = register("amplification");
    public static final DeferredHolder<RecipeSerializer<?>, AmplificationRecipe.Serializer> AMPLIFICATION_SERIALIZER = SERIALIZERS.register("amplification", AmplificationRecipe.Serializer::new);

    public static final DeferredHolder<RecipeType<?>, RecipeType<EnchantmentReplaceRecipe>> ENCHANTMENT_REPLACE = register("enchantment_replace");
    public static final DeferredHolder<RecipeSerializer<?>, EnchantmentReplaceRecipe.Serializer> ENCHANTMENT_REPLACE_SERIALIZER = SERIALIZERS.register("enchantment_replace", EnchantmentReplaceRecipe.Serializer::new);

    private static <T extends Recipe<?>> DeferredHolder<RecipeType<?> , RecipeType<T>> register(final String identifier) {
        return TYPES.register(identifier, () -> new RecipeType<T>() {
            public String toString() {
                return identifier;
            }
        });
    }

    public static void register(IEventBus eventBus) {
        TYPES.register(eventBus);
        SERIALIZERS.register(eventBus);
    }
}
