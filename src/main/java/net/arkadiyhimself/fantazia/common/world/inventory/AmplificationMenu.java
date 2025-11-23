package net.arkadiyhimself.fantazia.common.world.inventory;

import com.mojang.datafixers.util.Pair;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.client.screen.AmplificationTab;
import net.arkadiyhimself.fantazia.client.screen.AmplifyResource;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.PlayerAbilityHelper;
import net.arkadiyhimself.fantazia.common.api.attachment.entity.player_ability.holders.TalentsHolder;
import net.arkadiyhimself.fantazia.common.item.RuneWielderItem;
import net.arkadiyhimself.fantazia.common.registries.*;
import net.arkadiyhimself.fantazia.data.recipe.*;
import net.arkadiyhimself.fantazia.networking.IPacket;
import net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class AmplificationMenu extends AbstractContainerMenu {

    private static final ResourceLocation EMPTY_SLOT_SUBSTANCE = Fantazia.location("item/empty_slot_substance");
    private static final ResourceLocation EMPTY_SLOT_SUBSTANCE_RED = Fantazia.location("item/empty_slot_substance_red");

    private final TalentsHolder talents;
    private final Player player;
    /**
     * 0-1
     */
    private final AmplifyInitialContainer initialSlots = new AmplifyInitialContainer(2) {
        public void setChanged() {
            super.setChanged();
            AmplificationMenu.this.slotsChanged(this);
        }
    };
    /**
     * 2-10
     */
    private final CraftingContainer craftingContainer = new TransientCraftingContainer(this, 3, 3);
    /**
     * 11
     */
    private final ResultContainer resultContainer = new ResultContainer();
    /**
     * player inventory is 12-47
     */


    private final AmplifyResultSlot amplifyResultSlot;
    protected final Level level;
    private AmplifyResource enoughWisdom = AmplifyResource.REGULAR;
    private AmplifyResource enoughSubstance = AmplifyResource.REGULAR;

    private final ContainerLevelAccess access;

    public AmplificationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public AmplificationMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(FTZMenuTypes.AMPLIFICATION.value(), containerId);
        this.player = playerInventory.player;
        this.level = player.level();
        TalentsHolder holder = PlayerAbilityHelper.takeHolder(player, TalentsHolder.class);
        if (holder == null) throw new IllegalStateException("Player's talent holder is null!");
        this.talents = holder;
        this.access = access;

        this.addSlot(new Slot(this.initialSlots, 0, 19, 23) {
            public int getMaxStackSize() {
                return 1;
            }
        });

        this.addSlot(new Slot(this.initialSlots, 1, 19, 41) {
            public boolean mayPlace(@NotNull ItemStack stack) {
                return stack.is(FTZItems.OBSCURE_SUBSTANCE);
            }

            public Pair<ResourceLocation, ResourceLocation> getNoItemIcon() {
                boolean flag = AmplificationMenu.this.enoughSubstance.notEnough();
                return Pair.of(InventoryMenu.BLOCK_ATLAS, flag ? EMPTY_SLOT_SUBSTANCE_RED : EMPTY_SLOT_SUBSTANCE);
            }
        });

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 3; ++j) {
                this.addSlot(new Slot(this.craftingContainer, j + i * 3, 55 + j * 18, 14 + i * 18));
            }
        }

        this.amplifyResultSlot = new AmplifyResultSlot(this.player, this.talents, this.craftingContainer, this.initialSlots, this.resultContainer, 0, 132, 32) {

            @Override
            public void onTake(@NotNull Player player, @NotNull ItemStack stack) {
                super.onTake(player, stack);
            }
        };

        this.addSlot(amplifyResultSlot);

        for(int i = 0; i < 3; ++i) {
            for(int j = 0; j < 9; ++j) {
                this.addSlot(new Slot(playerInventory, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
            }
        }

        for(int k = 0; k < 9; ++k) {
            this.addSlot(new Slot(playerInventory, k, 8 + k * 18, 142));
        }
    }

    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index) {
        ItemStack itemstack = ItemStack.EMPTY;
        Slot slot = this.slots.get(index);
        if (slot.hasItem()) {
            ItemStack itemStack1 = slot.getItem();
            itemstack = itemStack1.copy();
            if (index >= 0 && index <= 10) {
                if (!this.moveItemStackTo(itemStack1, 12, 48, true)) return ItemStack.EMPTY;
            } else if (index == 11) {
                this.access.execute((level, blockPos) -> itemStack1.getItem().onCraftedBy(itemStack1, level, player));
                if (!this.moveItemStackTo(itemStack1, 12, 48, true)) return ItemStack.EMPTY;
                slot.onQuickCraft(itemStack1, itemstack);
            } else if (itemStack1.is(FTZItems.OBSCURE_SUBSTANCE)) {
                if (!this.moveItemStackTo(itemStack1, 1, 2, true)) return ItemStack.EMPTY;
            } else if (RuneWielderItem.isEmptyRune(itemStack1)) {
                if (!this.moveItemStackTo(itemStack1, 0, 1, true)) return ItemStack.EMPTY;
            } else {
                if (this.slots.getFirst().hasItem() || !this.slots.getFirst().mayPlace(itemStack1)) {
                    if (!this.moveItemStackTo(itemStack1, 2, 11, false)) {
                        return ItemStack.EMPTY;
                    }
                } else {
                    ItemStack itemStack2 = itemStack1.copyWithCount(1);
                    itemStack1.shrink(1);
                    this.slots.getFirst().setByPlayer(itemStack2);
                }
            }

            if (itemStack1.isEmpty()) {
                slot.setByPlayer(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }

            if (itemStack1.getCount() == itemstack.getCount()) {
                return ItemStack.EMPTY;
            }

            slot.onTake(player, itemStack1);
        }

        return itemstack;
    }

    @Override
    public void slotsChanged(@NotNull Container inventory) {
        this.access.execute((level, blockPos) -> slotChangedCraftingGrid());
    }

    @Override
    public boolean clickMenuButton(@NotNull Player player, int id) {
        return true;
    }

    public void removed(@NotNull Player player) {
        super.removed(player);
        this.access.execute((level, blockPos) -> {
            this.clearContainer(player, this.initialSlots);
            this.clearContainer(player, this.craftingContainer);
        });
    }

    public boolean stillValid(@NotNull Player player) {
        return stillValid(this.access, player, FTZBlocks.AMPLIFICATION_BENCH.value());
    }

    public TalentsHolder getTalents() {
        return talents;
    }

    public AmplifyResource wisdomResource() {
        return this.enoughWisdom;
    }

    public AmplifyResource substanceResource() {
        return this.enoughSubstance;
    }

    public void setEnoughWisdom(AmplifyResource enoughWisdom) {
        this.enoughWisdom = enoughWisdom;
    }

    public void setEnoughSubstance(AmplifyResource enoughSubstance) {
        this.enoughSubstance = enoughSubstance;
    }

    protected void slotChangedCraftingGrid() {
        if (this.player instanceof ServerPlayer serverPlayer) {
            this.amplifyResultSlot.setRecipeType(null);
            ItemStack initial = initialSlots.getItem(0);

            ItemStack itemstack = ItemStack.EMPTY;

            itemstack = chooseCraft(initial, itemstack, serverPlayer);

            this.resultContainer.setItem(0, itemstack);
            this.setRemoteSlot(11, itemstack);
            serverPlayer.connection.send(new ClientboundContainerSetSlotPacket(this.containerId, this.incrementStateId(), 11, itemstack));
        }
    }

    private ItemStack chooseCraft(ItemStack initial, ItemStack itemstack, ServerPlayer serverPlayer) {
        ServerLevel serverLevel = serverPlayer.serverLevel();
        AmplificationInput amplificationInput = AmplificationInput.input(initial, this.craftingContainer.getItems(), this.initialSlots.getItem(1).getCount(), talents.getWisdom());

        AmplifyResource wisdom = AmplifyResource.REGULAR;
        AmplifyResource substance = AmplifyResource.REGULAR;

        if (selected() == AmplificationTab.RUNE_CARVING) {
            this.amplifyResultSlot.setRecipeType(FTZRecipeTypes.RUNE_CARVING.value());
            RuneCarvingInput runeCarvingInput = RuneCarvingInput.input(initial, this.craftingContainer.getItems(), this.initialSlots.getItem(1).getCount(), talents.getWisdom());
            Optional<RecipeHolder<RuneCarvingRecipe>> optional = serverLevel.getRecipeManager().getRecipeFor(FTZRecipeTypes.RUNE_CARVING.value(), runeCarvingInput, serverLevel, (RecipeHolder<RuneCarvingRecipe>) null);
            if (optional.isPresent()) {
                RecipeHolder<RuneCarvingRecipe> recipeholder = optional.get();
                RuneCarvingRecipe runeCarvingRecipe = recipeholder.value();
                wisdom = runeCarvingRecipe.wisdom() <= talents.getWisdom() ? AmplifyResource.ENOUGH : AmplifyResource.NOT_ENOUGH;
                substance = runeCarvingRecipe.fee() <= this.initialSlots.getItem(1).getCount() ? AmplifyResource.ENOUGH : AmplifyResource.NOT_ENOUGH;
                if (wisdom.isEnough() && substance.isEnough() && this.resultContainer.setRecipeUsed(serverLevel, serverPlayer, recipeholder)) {
                    ItemStack itemStack1 = runeCarvingRecipe.assemble(runeCarvingInput, serverLevel.registryAccess());
                    if (itemStack1.isItemEnabled(serverLevel.enabledFeatures())) {
                        itemstack = itemStack1;
                    }
                }
            }
        }

        if (selected() == AmplificationTab.AMPLIFICATION) {
            Optional<RecipeHolder<AmplificationRecipe>> amplificationRecipeOptional = serverLevel.getRecipeManager().getRecipeFor(FTZRecipeTypes.AMPLIFICATION.value(), amplificationInput, serverLevel, (RecipeHolder<AmplificationRecipe>) null);
            if (amplificationRecipeOptional.isPresent()) {
                this.amplifyResultSlot.setRecipeType(FTZRecipeTypes.AMPLIFICATION.value());
                RecipeHolder<AmplificationRecipe> amplificationRecipeHolder = amplificationRecipeOptional.get();
                AmplificationRecipe amplificationRecipe = amplificationRecipeHolder.value();
                wisdom = amplificationRecipe.wisdom() <= talents.getWisdom() ? AmplifyResource.ENOUGH : AmplifyResource.NOT_ENOUGH;
                substance = amplificationRecipe.fee() <= this.initialSlots.getItem(1).getCount() ? AmplifyResource.ENOUGH : AmplifyResource.NOT_ENOUGH;
                if (wisdom.isEnough() && substance.isEnough() && this.resultContainer.setRecipeUsed(serverLevel, serverPlayer, amplificationRecipeHolder)) {
                    ItemStack itemStack1 = amplificationRecipe.assemble(amplificationInput, serverLevel.registryAccess());
                    if (itemStack1.isItemEnabled(serverLevel.enabledFeatures())) {
                        itemstack = itemStack1;
                    }
                }
            }
        }

        if (selected() == AmplificationTab.ENCHANTMENT_REPLACE) {
            Optional<RecipeHolder<EnchantmentReplaceRecipe>> enchantmentReplaceRecipeOptional = serverLevel.getRecipeManager().getRecipeFor(FTZRecipeTypes.ENCHANTMENT_REPLACE.value(), amplificationInput, serverLevel, (RecipeHolder<EnchantmentReplaceRecipe>) null);
            if (enchantmentReplaceRecipeOptional.isPresent()) {
                this.amplifyResultSlot.setRecipeType(FTZRecipeTypes.ENCHANTMENT_REPLACE.value());
                RecipeHolder<EnchantmentReplaceRecipe> enchantmentReplaceRecipeHolder = enchantmentReplaceRecipeOptional.get();
                EnchantmentReplaceRecipe enchantmentReplace = enchantmentReplaceRecipeHolder.value();
                wisdom = enchantmentReplace.wisdom() <= talents.getWisdom() ? AmplifyResource.ENOUGH : AmplifyResource.NOT_ENOUGH;
                substance = enchantmentReplace.fee() <= this.initialSlots.getItem(1).getCount() ? AmplifyResource.ENOUGH : AmplifyResource.NOT_ENOUGH;
                if (wisdom.isEnough() && substance.isEnough() && this.resultContainer.setRecipeUsed(serverLevel, serverPlayer, enchantmentReplaceRecipeHolder)) {
                    ItemStack itemStack1 = enchantmentReplace.assemble(amplificationInput, serverLevel.registryAccess());
                    if (itemStack1.isItemEnabled(serverLevel.enabledFeatures())) {
                        itemstack = itemStack1;
                    }
                }
            }
        }

        IPacket.amplificationMenuEnoughResources(serverPlayer, wisdom, substance);
        return itemstack;
    }

    public void setTab(AmplificationTab tab) {
        this.player.setData(FTZAttachmentTypes.LAST_SELECTED_AMPLIFICATION_TAB, tab);

        if (player.level().isClientSide()) {
            IPacket.setAmplificationTab(tab);
            return;
        }
        slotChangedCraftingGrid();
    }

    public AmplificationTab selected() {
        return player.getData(FTZAttachmentTypes.LAST_SELECTED_AMPLIFICATION_TAB);
    }
}
