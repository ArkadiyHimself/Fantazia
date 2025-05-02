package net.arkadiyhimself.fantazia.data.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.api.attachment.entity.player_ability.holders.LootTableModifiersHolder;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.util.wheremagichappens.StuffHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.neoforged.neoforge.common.loot.IGlobalLootModifier;
import net.neoforged.neoforge.common.loot.LootModifier;
import org.jetbrains.annotations.NotNull;

public class FantazicLootModifier extends LootModifier {

    public static final MapCodec<FantazicLootModifier> CODEC = RecordCodecBuilder.mapCodec(instance -> codecStart(instance).apply(instance, FantazicLootModifier::new));
    public FantazicLootModifier(LootItemCondition[] conditionsIn) {
        super(conditionsIn);
    }

    @Override
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        Entity killer = context.getParamOrNull(LootContextParams.ATTACKING_ENTITY);
        ResourceLocation id = context.getQueriedLootTableId();
        boolean chest = LootTablesHelper.isVanillaChest(context);
        boolean slayed = LootTablesHelper.isSlayed(context);

        if (chest && !LootTablesHelper.isVillage(context.getQueriedLootTableId())) addItem(generatedLoot, FTZItems.OBSCURE_SUBSTANCE.get(), -2, 3);
        if (slayed && killer instanceof Player playerKiller) PlayerAbilityHelper.acceptConsumer(playerKiller, LootTableModifiersHolder.class, lootTableModifiersHolder -> lootTableModifiersHolder.attemptLoot(generatedLoot, id));

        if (!(entity instanceof Player player)) return generatedLoot;

        PlayerAbilityHelper.acceptConsumer(player, LootTableModifiersHolder.class, lootTableModifiersHolder -> lootTableModifiersHolder.attemptLoot(generatedLoot, id));
        if (!StuffHelper.hasPersistentTag(player, "LootedFirstDashstone") && chest) {
            StuffHelper.setPersistentBoolean(player, "LootedFirstDashstone");
            generatedLoot.add(new ItemStack(FTZItems.DASHSTONE1.get()));
        }

        return generatedLoot;
    }
    @Override
    public @NotNull MapCodec<? extends IGlobalLootModifier> codec() {
        return CODEC;
    }

    private static void addItem(@NotNull ObjectArrayList<ItemStack> generatedLoot, Item item, int min, int max) {
        int amo = Fantazia.RANDOM.nextInt(min, max);
        if (amo > 0) generatedLoot.add(new ItemStack(item, amo));
    }
}
