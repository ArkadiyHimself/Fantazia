package net.arkadiyhimself.fantazia.data.item.rechargeable_tool;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.registries.FTZDataMapTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public record RechargeableToolData(
        ToolCapacityLevelFunction capacity,
        Optional<Integer> initialAmount,
        ToolDamageLevelFunction damage,
        List<SimpleIngredient> ingredients
) {

    public static final Codec<RechargeableToolData> CODEC = RecordCodecBuilder.<RechargeableToolData>create(instance -> instance.group(
            ToolCapacityLevelFunction.DISPATCH_CODEC.fieldOf("capacity").forGetter(RechargeableToolData::capacity),
            Codec.INT.optionalFieldOf("initial_amount").forGetter(RechargeableToolData::initialAmount),
            ToolDamageLevelFunction.DISPATCH_CODEC.fieldOf("damage").forGetter(RechargeableToolData::damage),
            SimpleIngredient.CODEC.listOf().fieldOf("ingredients").forGetter(RechargeableToolData::ingredients)
    ).apply(instance, RechargeableToolData::new)).validate(RechargeableToolData::validate);

    public int getMaxAmount(int level) {
        return capacity.apply(level);
    }

    public int getInitialAmount() {
        return Math.max(0, initialAmount.orElseGet(() -> capacity.apply(0)));
    }

    public record SimpleIngredient(Item item, int amount) {

        public static final Codec<SimpleIngredient> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                BuiltInRegistries.ITEM.byNameCodec().fieldOf("item").forGetter(SimpleIngredient::item),
                Codec.INT.fieldOf("amount").forGetter(SimpleIngredient::amount)
        ).apply(instance, SimpleIngredient::new));

        public ItemStack toStack() {
            return new ItemStack(item, amount);
        }
    }

    public static @Nullable RechargeableToolData getToolData(Item item) {
        Optional<ResourceKey<Item>> key = BuiltInRegistries.ITEM.getResourceKey(item);
        return key.map(itemResourceKey -> BuiltInRegistries.ITEM.getData(FTZDataMapTypes.RECHARGEABLE_TOOLS, itemResourceKey)).orElse(null);
    }

    private static DataResult<RechargeableToolData> validate(RechargeableToolData data) {
        if (data.ingredients.isEmpty()) return DataResult.error(() -> "Ingredients can not be empty!");
        List<Item> itemList = Lists.newArrayList();
        for (SimpleIngredient ingredient : data.ingredients) {
            if (itemList.contains(ingredient.item)) return DataResult.error(() -> "Duplicating ingredients: " + ingredient.item.toString());
            itemList.add(ingredient.item);
        }
        return DataResult.success(data);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private ToolCapacityLevelFunction capacity = ToolCapacityLevelFunction.constant(0);
        private Integer initialAmount = null;
        private ToolDamageLevelFunction damage = ToolDamageLevelFunction.constant(0);
        private final List<SimpleIngredient> ingredients = Lists.newArrayList();

        public Builder capacity(ToolCapacityLevelFunction capacity) {
            this.capacity = capacity;
            return this;
        }

        public Builder initialAmount(int initialAmount) {
            this.initialAmount = initialAmount;
            return this;
        }

        public Builder damage(ToolDamageLevelFunction damage) {
            this.damage = damage;
            return this;
        }

        public Builder ingredient(Item ingredient, int amount) {
            this.ingredients.add(new SimpleIngredient(ingredient, amount));
            return this;
        }

        public RechargeableToolData build() {
            return new RechargeableToolData(
                    capacity,
                    Optional.ofNullable(initialAmount),
                    damage,
                    ingredients
            );
        }
    }
}
