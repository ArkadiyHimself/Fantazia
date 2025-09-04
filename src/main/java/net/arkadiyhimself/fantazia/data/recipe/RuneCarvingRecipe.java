package net.arkadiyhimself.fantazia.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.rune.Rune;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.arkadiyhimself.fantazia.data.FTZStreamCodecs;
import net.arkadiyhimself.fantazia.common.item.RuneWielderItem;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.*;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

public record RuneCarvingRecipe(NonNullList<Ingredient> ingredients, Holder<Rune> output, int fee, int wisdom) implements IRecipeWithFee<RuneCarvingInput> {

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    @Override
    public boolean matches(@NotNull RuneCarvingInput input, @NotNull Level level) {
        if (level.isClientSide() || input.count() != this.ingredients.size() || !RuneWielderItem.isEmptyRune(input.emptyRune())) {
            return false;
        } else {
            return input.size() == 1 && this.ingredients.size() == 1 ? this.ingredients.getFirst().test(input.getItem(0)) : input.contents().canCraft(this, null);
        }
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull RuneCarvingInput runeCarvingInput, @NotNull HolderLookup.Provider provider) {
        ItemStack newRune = runeCarvingInput.emptyRune().copy();
        newRune.set(FTZDataComponentTypes.RUNE, output);
        return newRune;
    }

    public @NotNull ItemStack getResultItem() {
        return RuneWielderItem.rune(output);
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return getResultItem();
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return FTZRecipeTypes.RUNE_CARVING_SERIALIZER.value();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return FTZRecipeTypes.RUNE_CARVING.value();
    }

    @Override
    public int getFee() {
        return fee;
    }

    @Override
    public int getWisdom() {
        return wisdom;
    }

    public static final class Serializer implements RecipeSerializer<RuneCarvingRecipe> {

        public static final MapCodec<RuneCarvingRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
                    Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
                    if (aingredient.length == 0) {
                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                    } else {
                        return aingredient.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(9)) : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                    }
                }, DataResult::success).forGetter(RuneCarvingRecipe::ingredients),
                FantazicRegistries.RUNES.holderByNameCodec().fieldOf("rune").forGetter(RuneCarvingRecipe::output),
                Codec.INT.fieldOf("fee").forGetter(RuneCarvingRecipe::fee),
                Codec.INT.fieldOf("wisdom").forGetter(RuneCarvingRecipe::wisdom)
        ).apply(instance, RuneCarvingRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, RuneCarvingRecipe> STREAM_CODEC = StreamCodec.of(Serializer::toNetwork, Serializer::fromNetwork);

        @Override
        public @NotNull MapCodec<RuneCarvingRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, RuneCarvingRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static RuneCarvingRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int i = buffer.readVarInt();
            int fee = buffer.readVarInt();
            int wisdom = buffer.readVarInt();
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(i, Ingredient.EMPTY);
            nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            Holder<Rune> runeHolder = FTZStreamCodecs.RUNE_HOLDER.decode(buffer);
            return new RuneCarvingRecipe(nonnulllist, runeHolder, fee, wisdom);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, RuneCarvingRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            buffer.writeVarInt(recipe.fee);
            buffer.writeVarInt(recipe.wisdom);

            for(Ingredient ingredient : recipe.ingredients) Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);

            FTZStreamCodecs.RUNE_HOLDER.encode(buffer, recipe.output);
        }
    }
}
