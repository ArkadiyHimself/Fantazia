package net.arkadiyhimself.fantazia.data.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.LootTableModifiersHolder;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.util.wheremagichappens.PlayerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraftforge.common.loot.IGlobalLootModifier;
import net.minecraftforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

import java.util.function.Supplier;

public class FantazicLootModifier extends LootModifier {
    public static final Supplier<Codec<FantazicLootModifier>> CODEC = Suppliers.memoize(() -> RecordCodecBuilder.create(inst -> codecStart(inst).apply(inst, FantazicLootModifier::new)));
    public FantazicLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity killer = context.getParamOrNull(LootContextParams.KILLER_ENTITY);
        ResourceLocation id = context.getQueriedLootTableId();
        boolean chest = LootTablesHelper.isVanillaChest(context);
        boolean slayed = LootTablesHelper.isSlayed(context);

        if (chest && !LootTablesHelper.isVillage(id)) addItem(generatedLoot, FTZItems.OBSCURE_ESSENCE.get(), -2, 3);
        if (slayed && killer instanceof Player playerKiller) AbilityGetter.abilityConsumer(playerKiller, LootTableModifiersHolder.class, lootTableModifiersHolder -> lootTableModifiersHolder.attemptLoot(generatedLoot, id));

        if (!(entity instanceof Player player)) return generatedLoot;

        AbilityGetter.abilityConsumer(player, LootTableModifiersHolder.class, lootTableModifiersHolder -> lootTableModifiersHolder.attemptLoot(generatedLoot, id));
        if (!PlayerData.hasPersistentTag(player, "LootedFirstDashstone") && chest) {
            PlayerData.setPersistentBoolean(player, "LootedFirstDashstone");
            generatedLoot.add(new ItemStack(FTZItems.DASHSTONE1.get()));
        }

        return generatedLoot;
    }
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }
    private static void addItem(@NotNull ObjectArrayList<ItemStack> generatedLoot, Item item, int max, int min) {
        int amo = Math.round(Mth.lerp(Fantazia.RANDOM.nextFloat(), min, max));
        if (amo > 0) generatedLoot.add(new ItemStack(item, amo));
    }
}
