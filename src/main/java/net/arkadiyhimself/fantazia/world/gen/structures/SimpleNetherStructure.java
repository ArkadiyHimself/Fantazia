package net.arkadiyhimself.fantazia.world.gen.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.registries.FTZStructureTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import org.jetbrains.annotations.NotNull;

import java.util.Optional;

public class SimpleNetherStructure extends Structure {

    public static final MapCodec<SimpleNetherStructure> CODEC = RecordCodecBuilder.mapCodec(instance -> instance.group(settingsCodec(instance), StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool), Codec.INT.fieldOf("size_to_check").forGetter(structure -> structure.sizeToCheck)).apply(instance, SimpleNetherStructure::new));

    private final Holder<StructureTemplatePool> startPool;

    private final int sizeToCheck;

    public SimpleNetherStructure(StructureSettings settings, Holder<StructureTemplatePool> startPool, int sizeToCheck) {
        super(settings);
        this.startPool = startPool;
        this.sizeToCheck = sizeToCheck;
    }

    @Override
    public @NotNull Optional<GenerationStub> findGenerationPoint(@NotNull GenerationContext context) {

        Optional<Integer> yLevel = StructureHelper.getSuitableNetherYLevel(context, context.chunkPos().getMiddleBlockPosition(0));

        if (yLevel.isEmpty()) return Optional.empty();

        BlockPos pos = context.chunkPos().getMiddleBlockPosition(yLevel.get());

        for (int x = pos.getX() - this.sizeToCheck; x <= pos.getX() + this.sizeToCheck; x += this.sizeToCheck) {
            for (int z = pos.getZ() - this.sizeToCheck; z <= pos.getZ() + this.sizeToCheck; z += this.sizeToCheck) {
                if (!StructureHelper.checkLandAtHeight(context, pos, 5))
                    return Optional.empty();
            }
        }

        return JigsawPlacement.addPieces(context, this.startPool, Optional.empty(), 1, pos, false, Optional.empty(), 1, PoolAliasLookup.EMPTY, DimensionPadding.ZERO, LiquidSettings.APPLY_WATERLOGGING);
    }
    @Override
    public @NotNull StructureType<?> type() {
        return FTZStructureTypes.SIMPLE_NETHER_STRUCTURE.get();
    }
}
