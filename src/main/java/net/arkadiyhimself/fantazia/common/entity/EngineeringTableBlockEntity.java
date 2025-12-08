package net.arkadiyhimself.fantazia.common.entity;

import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.item.EngineeringTableBlock;
import net.arkadiyhimself.fantazia.common.registries.FTZBlockEntityTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.common.registries.FTZSoundEvents;
import net.arkadiyhimself.fantazia.data.criterion.ReadBlueprintTrigger;
import net.arkadiyhimself.fantazia.util.wheremagichappens.RandomUtil;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.DoubleBlockHalf;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class EngineeringTableBlockEntity extends BlockEntity {

    private ItemStack stack = ItemStack.EMPTY;

    public EngineeringTableBlockEntity(BlockPos pos, BlockState blockState) {
        super(FTZBlockEntityTypes.ENGINEERING_TABLE.value(), pos, blockState);
    }

    public boolean isValid() {
        return getBlockState().getValue(EngineeringTableBlock.HALF) == DoubleBlockHalf.UPPER;
    }

    public @Nullable Holder<Blueprint> getBlueprint() {
        if (!isValid()) return null;
        return stack.get(FTZDataComponentTypes.BLUEPRINT);
    }

    public boolean returnBlueprint(Player player) {
        if (!this.stack.isEmpty()) {
            player.getInventory().placeItemBackInInventory(this.stack);
            return true;
        } else return false;
    }

    public boolean setBlueprint(Player player, ItemStack newStack) {
        if (!isValid()) return false;
        Holder<Blueprint> blueprint = newStack.get(FTZDataComponentTypes.BLUEPRINT);
        if (blueprint == null) return false;
        returnBlueprint(player);
        this.stack = newStack;
        this.setChanged();
        if (player instanceof ServerPlayer serverPlayer) ReadBlueprintTrigger.INSTANCE.trigger(serverPlayer, blueprint);
        if (level != null)
            level.playSound(null, getBlockPos(), FTZSoundEvents.ENGINEERING_TABLE_READ_PRINT.value(), SoundSource.BLOCKS);
        return true;
    }

    public void onDestroy() {
        if (level == null || stack.isEmpty()) return;

        double d0 = EntityType.ITEM.getWidth();
        double d1 = 1.0 - d0;
        double d2 = d0 / 2.0;
        double d3 = getBlockPos().getX() + RandomUtil.nextDouble() * d1 + d2;
        double d4 = getBlockPos().getY() + RandomUtil.nextDouble() * d1;
        double d5 = getBlockPos().getZ() + RandomUtil.nextDouble() * d1 + d2;

        ItemEntity itementity = new ItemEntity(level, d3, d4, d5, stack.split(level.random.nextInt(21) + 10));
        itementity.setDeltaMovement(level.random.triangle(0.0, 0.11485000171139836), level.random.triangle(0.2, 0.11485000171139836), level.random.triangle(0.0, 0.11485000171139836));
        level.addFreshEntity(itementity);
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (stack != null && !stack.isEmpty()) tag.put("stack", stack.save(registries));
    }

    @Override
    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("stack", Tag.TAG_COMPOUND)) {
            CompoundTag stackTag = tag.getCompound("stack");
            this.stack = ItemStack.parseOptional(registries, stackTag);
        }
    }

    @Override
    public @Nullable ClientboundBlockEntityDataPacket getUpdatePacket() {
        return ClientboundBlockEntityDataPacket.create(this);
    }

    @Override
    public @NotNull CompoundTag getUpdateTag(HolderLookup.@NotNull Provider registries) {
        return this.saveCustomOnly(registries);
    }
}
