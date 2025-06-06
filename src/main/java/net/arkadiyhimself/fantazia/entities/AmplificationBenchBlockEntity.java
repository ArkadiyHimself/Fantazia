package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.registries.FTZBlockEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Nameable;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class AmplificationBenchBlockEntity extends BlockEntity implements Nameable {

    @Nullable
    private Component name;

    public AmplificationBenchBlockEntity(BlockPos pos, BlockState blockState) {
        super(FTZBlockEntityTypes.AMPLIFICATION_BENCH.value(), pos, blockState);
    }

    protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.saveAdditional(tag, registries);
        if (name != null) {
            tag.putString("CustomName", Component.Serializer.toJson(name, registries));
        }
    }

    protected void loadAdditional(@NotNull CompoundTag tag, HolderLookup.@NotNull Provider registries) {
        super.loadAdditional(tag, registries);
        if (tag.contains("CustomName", 8)) {
            name = parseCustomNameSafe(tag.getString("CustomName"), registries);
        }
    }

    @Override
    public @NotNull Component getName() {
        return name == null ? Component.translatable("container.fantazia.amplification") : name;
    }

    public void setCustomName(@Nullable Component customName) {
        this.name = customName;
    }

    public @Nullable Component getCustomName() {
        return this.name;
    }

    @Override
    public @NotNull BlockEntityType<?> getType() {
        return FTZBlockEntityTypes.AMPLIFICATION_BENCH.value();
    }
}
