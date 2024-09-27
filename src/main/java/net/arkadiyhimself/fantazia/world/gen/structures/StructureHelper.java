package net.arkadiyhimself.fantazia.world.gen.structures;

import com.mojang.datafixers.util.Pair;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.entities.DashStoneEntity;
import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.commands.ExecuteCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.targeting.TargetingConditions;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.structure.Structure;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class StructureHelper {
    private StructureHelper() {}
    public static @Nullable Pair<BlockPos, Holder<Structure>> findNearestStructure(ServerLevel serverLevel, BlockPos initial, ResourceLocation structureID) {
        Registry<Structure> registry = serverLevel.registryAccess().registryOrThrow(Registries.STRUCTURE);
        Structure altar = registry.get(structureID);
        if (altar == null) return null;
        Holder<Structure> holder = Holder.direct(altar);
        HolderSet<Structure> set = HolderSet.direct(holder);
        return serverLevel.getChunkSource().getGenerator().findNearestMapStructure(serverLevel, set, initial, 100, false);
    }
    public static Optional<Integer> getSuitableNetherYLevel(Structure.GenerationContext context, BlockPos pos) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());
        List<Integer> suitableYLevels = new ArrayList<>();

        for (int y = 127; y > context.chunkGenerator().getSeaLevel(); y--) {
            if (column.getBlock(y - 1).canOcclude() && column.getBlock(y).isAir() && column.getBlock(y + 4).isAir()) {
                suitableYLevels.add(y);
            }
        }

        if (suitableYLevels.isEmpty())
            return Optional.empty();

        return Optional.of(suitableYLevels.get(new Random(context.seed()).nextInt(suitableYLevels.size())));
    }
    public static boolean checkLandAtHeight(Structure.GenerationContext context, BlockPos pos, int heightTolerance) {
        NoiseColumn column = context.chunkGenerator().getBaseColumn(pos.getX(), pos.getZ(), context.heightAccessor(), context.randomState());

        for (int y = pos.getY() - heightTolerance; y <= pos.getZ() + heightTolerance; y++) {
            if (column.getBlock(y).canOcclude() && column.getBlock(y + 1).isAir())
                return true;
        }

        return false;
    }
}
