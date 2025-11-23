package net.arkadiyhimself.fantazia.common.world.inventory;

import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.common.registries.FTZRecipeTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.recipe.*;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.core.NonNullList;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.CraftingContainer;
import net.minecraft.world.inventory.RecipeCraftingHolder;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.CraftingInput;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.event.EventHooks;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AmplifyResultSlot extends Slot {

    private final CraftingContainer craftSlots;
    private final AmplifyInitialContainer amplifyInitialContainer;
    private final Player player;
    private int removeCount;
    private RecipeType<?> recipeType = null;
    private TalentsHolder talentsHolder;

    public AmplifyResultSlot(Player player, TalentsHolder talentsHolder, CraftingContainer craftSlots, AmplifyInitialContainer amplifyInitialContainer, Container container, int slot, int xPosition, int yPosition) {
        super(container, slot, xPosition, yPosition);
        this.player = player;
        this.craftSlots = craftSlots;
        this.amplifyInitialContainer = amplifyInitialContainer;
        this.talentsHolder = talentsHolder;
    }

    public boolean mayPlace(@NotNull ItemStack stack) {
        return false;
    }

    public @NotNull ItemStack remove(int amount) {
        if (this.hasItem()) {
            this.removeCount += Math.min(amount, this.getItem().getCount());
        }
        return super.remove(amount);
    }

    protected void onQuickCraft(@NotNull ItemStack stack, int amount) {
        this.removeCount += amount;
        this.checkTakeAchievements(stack);
    }

    protected void onSwapCraft(int numItemsCrafted) {
        this.removeCount += numItemsCrafted;
    }

    protected void checkTakeAchievements(@NotNull ItemStack stack) {
        if (this.removeCount > 0) {
            stack.onCraftedBy(this.player.level(), this.player, this.removeCount);
            EventHooks.firePlayerCraftingEvent(this.player, stack, this.craftSlots);
        }

        if (this.container instanceof RecipeCraftingHolder recipecraftingholder) {
            recipecraftingholder.awardUsedRecipes(this.player, this.craftSlots.getItems());
        }

        this.removeCount = 0;
    }

    @Override
    public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
        if (recipeType == FTZRecipeTypes.RUNE_CARVING.value()) onRuneCarvingTake(player, stack);
        else if (recipeType == FTZRecipeTypes.AMPLIFICATION.value()) onAmplificationTake(player, stack);
        else if (recipeType == FTZRecipeTypes.ENCHANTMENT_REPLACE.value()) onEnchantReplaceTake(player, stack);
    }

    private void onRuneCarvingTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);
        if (player instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.RUNE_CARVED.value());

        RuneCarvingInput runeCarvingInput = RuneCarvingInput.input(amplifyInitialContainer.getItem(0), this.craftSlots.getItems(), this.amplifyInitialContainer.getItem(1).getCount(), talentsHolder.getWisdom());
        Optional<RecipeHolder<RuneCarvingRecipe>> optional = player.level().getRecipeManager().getRecipeFor(FTZRecipeTypes.RUNE_CARVING.value(), runeCarvingInput, player.level());
        if (optional.isEmpty()) return;
        RecipeHolder<RuneCarvingRecipe> recipeHolder = optional.get();
        RuneCarvingRecipe carvingRecipe = recipeHolder.value();
        this.amplifyInitialContainer.removeItem(0, 1);
        this.amplifyInitialContainer.removeItem(1, carvingRecipe.fee());
        talentsHolder.spendWisdom(carvingRecipe.wisdom());
        CommonHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> remaining = player.level().getRecipeManager().getRemainingItemsFor(FTZRecipeTypes.RUNE_CARVING.value(), runeCarvingInput, player.level());
        CommonHooks.setCraftingPlayer(null);
        CraftingInput.Positioned positioned = craftSlots.asPositionedCraftInput();
        CraftingInput craftinginput = positioned.input();
        int i = positioned.left();
        int j = positioned.top();

        for(int k = 0; k < craftinginput.height(); ++k) {
            for(int l = 0; l < craftinginput.width(); ++l) {
                int i1 = l + i + (k + j) * this.craftSlots.getWidth();
                ItemStack itemstack = this.craftSlots.getItem(i1);
                ItemStack itemStack1 = remaining.get(l + k * craftinginput.width());
                if (!itemstack.isEmpty()) {
                    this.craftSlots.removeItem(i1, 1);
                    itemstack = this.craftSlots.getItem(i1);
                }

                if (!itemStack1.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        this.craftSlots.setItem(i1, itemStack1);
                    } else if (ItemStack.isSameItemSameComponents(itemstack, itemStack1)) {
                        itemStack1.grow(itemstack.getCount());
                        this.craftSlots.setItem(i1, itemStack1);
                    } else if (!this.player.getInventory().add(itemStack1)) {
                        this.player.drop(itemStack1, false);
                    }
                }
            }
        }
    }

    private void onAmplificationTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);

        if (player instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.AMPLIFICATION.value());

        AmplificationInput amplificationInput = AmplificationInput.input(amplifyInitialContainer.getItem(0), this.craftSlots.getItems(), this.amplifyInitialContainer.getItem(1).getCount(), talentsHolder.getWisdom());;
        Optional<RecipeHolder<AmplificationRecipe>> optional = player.level().getRecipeManager().getRecipeFor(FTZRecipeTypes.AMPLIFICATION.value(), amplificationInput, player.level());
        if (optional.isEmpty()) return;
        RecipeHolder<AmplificationRecipe> recipeHolder = optional.get();
        AmplificationRecipe amplificationRecipe = recipeHolder.value();
        this.amplifyInitialContainer.removeItem(0, 1);
        this.amplifyInitialContainer.removeItem(1, amplificationRecipe.fee());
        talentsHolder.spendWisdom(amplificationRecipe.wisdom());
        CommonHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> remaining = amplificationRecipe.getRemainingItems(amplificationInput);
        CommonHooks.setCraftingPlayer(null);
        CraftingInput.Positioned positioned = craftSlots.asPositionedCraftInput();
        CraftingInput craftinginput = positioned.input();
        int i = positioned.left();
        int j = positioned.top();

        for(int k = 0; k < craftinginput.height(); ++k) {
            for(int l = 0; l < craftinginput.width(); ++l) {
                int i1 = l + i + (k + j) * this.craftSlots.getWidth();
                ItemStack itemstack = this.craftSlots.getItem(i1);
                ItemStack itemStack1 = remaining.get(l + k * craftinginput.width());
                if (!itemstack.isEmpty()) {
                    this.craftSlots.removeItem(i1, 1);
                    itemstack = this.craftSlots.getItem(i1);
                }

                if (!itemStack1.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        this.craftSlots.setItem(i1, itemStack1);
                    } else if (ItemStack.isSameItemSameComponents(itemstack, itemStack1)) {
                        itemStack1.grow(itemstack.getCount());
                        this.craftSlots.setItem(i1, itemStack1);
                    } else if (!this.player.getInventory().add(itemStack1)) {
                        this.player.drop(itemStack1, false);
                    }
                }
            }
        }
    }

    private void onEnchantReplaceTake(@NotNull Player player, @NotNull ItemStack stack) {
        this.checkTakeAchievements(stack);

        if (player instanceof ServerPlayer serverPlayer) IPacket.playSoundForUI(serverPlayer, FTZSoundEvents.AMPLIFICATION.value());

        AmplificationInput amplificationInput = AmplificationInput.input(amplifyInitialContainer.getItem(0), this.craftSlots.getItems(), this.amplifyInitialContainer.getItem(1).getCount(), talentsHolder.getWisdom());;
        Optional<RecipeHolder<EnchantmentReplaceRecipe>> optional = player.level().getRecipeManager().getRecipeFor(FTZRecipeTypes.ENCHANTMENT_REPLACE.value(), amplificationInput, player.level());
        if (optional.isEmpty()) return;
        RecipeHolder<EnchantmentReplaceRecipe> recipeHolder = optional.get();
        EnchantmentReplaceRecipe enchantmentReplaceRecipe = recipeHolder.value();
        this.amplifyInitialContainer.removeItem(0, 1);
        this.amplifyInitialContainer.removeItem(1, enchantmentReplaceRecipe.fee());
        talentsHolder.spendWisdom(enchantmentReplaceRecipe.wisdom());
        CommonHooks.setCraftingPlayer(player);
        NonNullList<ItemStack> remaining = enchantmentReplaceRecipe.getRemainingItems(amplificationInput);
        CommonHooks.setCraftingPlayer(null);
        CraftingInput.Positioned positioned = craftSlots.asPositionedCraftInput();
        CraftingInput craftinginput = positioned.input();
        int i = positioned.left();
        int j = positioned.top();

        for(int k = 0; k < craftinginput.height(); ++k) {
            for(int l = 0; l < craftinginput.width(); ++l) {
                int i1 = l + i + (k + j) * this.craftSlots.getWidth();
                ItemStack itemstack = this.craftSlots.getItem(i1);
                ItemStack itemStack1 = remaining.get(l + k * craftinginput.width());
                if (!itemstack.isEmpty()) {
                    this.craftSlots.removeItem(i1, 1);
                    itemstack = this.craftSlots.getItem(i1);
                }

                if (!itemStack1.isEmpty()) {
                    if (itemstack.isEmpty()) {
                        this.craftSlots.setItem(i1, itemStack1);
                    } else if (ItemStack.isSameItemSameComponents(itemstack, itemStack1)) {
                        itemStack1.grow(itemstack.getCount());
                        this.craftSlots.setItem(i1, itemStack1);
                    } else if (!this.player.getInventory().add(itemStack1)) {
                        this.player.drop(itemStack1, false);
                    }
                }
            }
        }
    }

    public void setRecipeType(RecipeType<?> recipeType) {
        this.recipeType = recipeType;
    }

    public boolean isFake() {
        return true;
    }
}
