package net.arkadiyhimself.fantazia.data.loot;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityGetter;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.AbilityManager;
import net.arkadiyhimself.fantazia.api.capability.entity.ability.abilities.LootTablePSERAN;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.util.wheremagichappens.PlayerData;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
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
    @SuppressWarnings("ConstantConditions")
    protected @NotNull ObjectArrayList<ItemStack> doApply(@NotNull ObjectArrayList<ItemStack> generatedLoot, LootContext context) {
        Entity entity = context.getParamOrNull(LootContextParams.THIS_ENTITY);
        ResourceLocation id = context.getQueriedLootTableId();
        if (!(entity instanceof Player player) || !LootTablesHelper.isVanillaChest(context)) return generatedLoot;
        if (!PlayerData.hasPersistentTag(player, "LootedDashstone")) {
            PlayerData.setPersistentBoolean(player, "LootedDashstone", true);
            generatedLoot.add(new ItemStack(FTZItems.DASHSTONE1));
        }
        AbilityManager abilityManager = AbilityGetter.getUnwrap(player);
        if (abilityManager == null) return generatedLoot;
        LootTablePSERAN LootTablePSERAN = abilityManager.takeAbility(LootTablePSERAN.class);
        if (LootTablePSERAN == null) return generatedLoot;
        if (!LootTablesHelper.isVillage(id)) FantazicLootTables.addItem(generatedLoot, FTZItems.OBSCURE_ESSENCE, -2, 3);
        if (LootTablesHelper.isNether(id)) FantazicLootTables.netherPool(generatedLoot, LootTablePSERAN);
        if (id.equals(BuiltInLootTables.ANCIENT_CITY)) FantazicLootTables.ancientCityPool(generatedLoot, LootTablePSERAN);
        if (id.equals(BuiltInLootTables.RUINED_PORTAL)) FantazicLootTables.ruinedPortalLoot(generatedLoot, LootTablePSERAN);
        if (id.equals(BuiltInLootTables.PILLAGER_OUTPOST)) FantazicLootTables.pillagerOutpostLoot(generatedLoot, LootTablePSERAN);
        if (id.equals(BuiltInLootTables.ABANDONED_MINESHAFT)) FantazicLootTables.mineshaftLoot(generatedLoot, LootTablePSERAN);

        return generatedLoot;
    }
    @Override
    public Codec<? extends IGlobalLootModifier> codec() {
        return CODEC.get();
    }

}
