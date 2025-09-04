package net.arkadiyhimself.fantazia.data.recipe;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.FTZStreamCodecs;
import net.arkadiyhimself.fantazia.common.registries.FTZRecipeTypes;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.NonNullList;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.ItemEnchantments;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public record EnchantmentReplaceRecipe(NonNullList<Ingredient> ingredients, Holder<Enchantment> previous, Holder<Enchantment> next, Optional<Integer> requiredLevel, int fee, int wisdom) implements IRecipeWithFee<AmplificationInput> {

    @Override
    public boolean matches(@NotNull AmplificationInput input, @NotNull Level level) {
        ItemStack initial = input.initial().copy();
        if (level.isClientSide() || this.ingredients.size() != input.count() || !input.initial().supportsEnchantment(next)) {
            return false;
        } else {
            int prevLevel = initial.getEnchantmentLevel(previous);
            int required = requiredLevel.orElseGet(() -> previous.value().getMaxLevel());
            if (prevLevel < required) return false;
            return input.size() == 1 && this.ingredients.size() == 1 ? this.ingredients.getFirst().test(input.getItem(0)) : input.contents().canCraft(this, null);
        }
    }

    @Override
    public @NotNull ItemStack assemble(@NotNull AmplificationInput amplificationInput, @NotNull HolderLookup.Provider provider) {
        ItemStack initial = amplificationInput.initial().copy();
        ItemEnchantments itemEnchantments = EnchantmentHelper.updateEnchantments(initial, mutable -> {
            mutable.removeIf(enchantmentHolder -> enchantmentHolder == previous);
            mutable.set(next, 1);
        });
        initial.set(DataComponents.ENCHANTMENTS, itemEnchantments);
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
        return FTZRecipeTypes.ENCHANTMENT_REPLACE_SERIALIZER.value();
    }

    @Override
    public @NotNull RecipeType<?> getType() {
        return FTZRecipeTypes.ENCHANTMENT_REPLACE.value();
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

    public static final class Serializer implements RecipeSerializer<EnchantmentReplaceRecipe> {

        public static final MapCodec<EnchantmentReplaceRecipe> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(
                Ingredient.CODEC_NONEMPTY.listOf().fieldOf("ingredients").flatXmap(ingredients -> {
                    Ingredient[] aingredient = ingredients.toArray(Ingredient[]::new);
                    if (aingredient.length == 0) {
                        return DataResult.error(() -> "No ingredients for shapeless recipe");
                    } else {
                        return aingredient.length > 9 ? DataResult.error(() -> "Too many ingredients for shapeless recipe. The maximum is: %s".formatted(9)) : DataResult.success(NonNullList.of(Ingredient.EMPTY, aingredient));
                    }
                }, DataResult::success).forGetter(EnchantmentReplaceRecipe::ingredients),
                Enchantment.CODEC.fieldOf("previous").forGetter(EnchantmentReplaceRecipe::previous),
                Enchantment.CODEC.fieldOf("next").forGetter(EnchantmentReplaceRecipe::next),
                Codec.INT.optionalFieldOf("required_level").forGetter(EnchantmentReplaceRecipe::requiredLevel),
                Codec.INT.fieldOf("fee").forGetter(EnchantmentReplaceRecipe::fee),
                Codec.INT.fieldOf("wisdom").forGetter(EnchantmentReplaceRecipe::wisdom)
        ).apply(instance, EnchantmentReplaceRecipe::new));

        public static final StreamCodec<RegistryFriendlyByteBuf, EnchantmentReplaceRecipe> STREAM_CODEC = StreamCodec.of(EnchantmentReplaceRecipe.Serializer::toNetwork, EnchantmentReplaceRecipe.Serializer::fromNetwork);

        @Override
        public @NotNull MapCodec<EnchantmentReplaceRecipe> codec() {
            return CODEC;
        }

        @Override
        public @NotNull StreamCodec<RegistryFriendlyByteBuf, EnchantmentReplaceRecipe> streamCodec() {
            return STREAM_CODEC;
        }

        private static EnchantmentReplaceRecipe fromNetwork(RegistryFriendlyByteBuf buffer) {
            int size = buffer.readVarInt();
            int fee = buffer.readVarInt();
            int wisdom = buffer.readVarInt();
            Optional<Integer> optional = buffer.readOptional(ByteBufCodecs.INT);

            NonNullList<Ingredient> nonnulllist = NonNullList.withSize(size, Ingredient.EMPTY);
            nonnulllist.replaceAll(ingredient -> Ingredient.CONTENTS_STREAM_CODEC.decode(buffer));
            Holder<Enchantment> previous = FTZStreamCodecs.ENCHANTMENT_HOLDER.decode(buffer);
            Holder<Enchantment> next = FTZStreamCodecs.ENCHANTMENT_HOLDER.decode(buffer);
            return new EnchantmentReplaceRecipe(nonnulllist, previous, next, optional, fee, wisdom);
        }

        private static void toNetwork(RegistryFriendlyByteBuf buffer, EnchantmentReplaceRecipe recipe) {
            buffer.writeVarInt(recipe.ingredients.size());
            buffer.writeVarInt(recipe.fee);
            buffer.writeVarInt(recipe.wisdom);
            buffer.writeOptional(recipe.requiredLevel, ByteBufCodecs.INT);

            for(Ingredient ingredient : recipe.ingredients) Ingredient.CONTENTS_STREAM_CODEC.encode(buffer, ingredient);

            FTZStreamCodecs.ENCHANTMENT_HOLDER.encode(buffer, recipe.previous);
            FTZStreamCodecs.ENCHANTMENT_HOLDER.encode(buffer, recipe.next);
        }
    }
}
