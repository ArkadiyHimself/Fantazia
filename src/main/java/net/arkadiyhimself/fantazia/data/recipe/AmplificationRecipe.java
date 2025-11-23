package net.arkadiyhimself.fantazia.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.registries.FTZRecipeTypes;
import net.arkadiyhimself.fantazia.data.FTZStreamCodecs;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record AmplificationRecipe(NonNullList<Ingredient> ingredients, Holder<Enchantment> amplified, Optional<Integer> limit, int fee, int wisdom) implements IRecipeWithFee<AmplificationInput> {

    @Override
    public boolean matches(@NotNull AmplificationInput input, @NotNull Level level) {
        if (level.isClientSide() || this.ingredients.size() != input.count() || !input.initial().supportsEnchantment(amplified)) {
            return false;
        } else {
            int lvl = input.initial().getEnchantmentLevel(amplified);
            if (limit.isPresent()) {
                if (limit.get() <= lvl) return false;
            } else if (lvl >= amplified.value().getMaxLevel()) return false;
            return input.size() == 1 && this.ingredients.size() == 1 ? this.ingredients.getFirst().test(input.getItem(0)) : input.contents().canCraft(this, null);
        }
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull AmplificationInput runeCarvingInput, @NotNull HolderLookup.Provider provider) {
        ItemStack initial = runeCarvingInput.initial().copy();

        int lvl = initial.getEnchantmentLevel(amplified);
        initial.enchant(amplified, ++lvl);
        return initial;
    }

    @Override
    public boolean canCraftInDimensions(int i, int i1) {
        return true;
    }

    @Override
    public @NotNull ItemStack getResultItem(@NotNull HolderLookup.Provider provider) {
        return ItemStack.EMPTY;
    }

    @Override
    public @NotNull RecipeSerializer<?> getSerializer() {
        return FTZRecipeTypes.AMPLIFICATION_SERIALIZER.value();
    }

    @Override
    public @NotNull RecipeType<AmplificationRecipe> getType() {
        return FTZRecipeTypes.AMPLIFICATION.value();
    }

    @Override
    public int getFee() {
        return fee;
    }

    @Override
    public int getWisdom() {
        return wisdom;
    }

    @Override
    public @NotNull NonNullList<Ingredient> getIngredients() {
        return ingredients;
    }

    public static final class Serializer implements RecipeSerializer<AmplificationRecipe> {

        public static final MapCodec<AmplificationRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
                    Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
                    if (aingredient.length == 0) {
                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                    } else {
                        return aingredient.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(9)) : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                    }
                }, DataResult::success).forGetter(AmplificationRecipe::ingredients),
                Enchantment.CODEC.fieldOf("amplified").forGetter(AmplificationRecipe::amplified),
                Codec.INT.optionalFieldOf("limit").forGetter(AmplificationRecipe::limit),
                Codec.INT.fieldOf("fee").forGetter(AmplificationRecipe::fee),
                Codec.INT.fieldOf("wisdom").forGetter(AmplificationRecipe::wisdom)
        ).apply(instance, AmplificationRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, AmplificationRecipe> STREAM_CODEC = StreamCodec.of(AmplificationRecipe.Serializer::toNetwork, AmplificationRecipe.Serializer::fromNetwork);

        @Override
        public @NotNull MapCodec<AmplificationRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, AmplificationRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static AmplificationRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            int fee = buffer.readVarInt();
            int wisdom = buffer.readVarInt();
            Optional<Integer> optional = buffer.readOptional(ByteBufCodecs.INT);
            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, Ingredient.EMPTY);
            nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            Holder<Enchantment> amplified = FTZStreamCodecs.ENCHANTMENT_HOLDER.decode(buffer);
            return new AmplificationRecipe(nonnulllist, amplified, optional, fee, wisdom);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, AmplificationRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            buffer.writeVarInt(recipe.fee);
            buffer.writeVarInt(recipe.wisdom);
            buffer.writeOptional(recipe.limit, ByteBufCodecs.INT);

            for(Ingredient ingredient : recipe.ingredients) Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);

            FTZStreamCodecs.ENCHANTMENT_HOLDER.encode(buffer, recipe.amplified);
        }
    }
}
