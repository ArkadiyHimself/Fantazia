package net.arkadiyhimself.fantazia.common.api.attachment.basis_attachments;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import com.natamus.collective.functions.BlockFunctions;
import com.natamus.collective.functions.BlockPosFunctions;
import com.natamus.collective.functions.CompareBlockFunctions;
import com.natamus.collective.functions.ItemFunctions;
import net.arkadiyhimself.fantazia.data.tags.FTZBlockTags;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.*;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.MapColor;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class LumberjackHolder {

    public static final Codec<LumberjackHolder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.fieldOf("unlocked").forGetter(LumberjackHolder::isUnlocked)
    ).apply(instance, LumberjackHolder::new));

    private boolean unlocked = false;
    private boolean buffer = false;

    public LumberjackHolder(boolean unlocked) {
        this.unlocked = unlocked;
    }

    public LumberjackHolder() {
    }

    public void unlock() {
        this.unlocked = true;
    }

    public void lock() {
        this.unlocked = false;
    }

    public boolean isUnlocked() {
        return unlocked;
    }

    public void setBuffer(boolean value) {
        this.buffer = value;
    }

    public boolean isBuffer() {
        return buffer;
    }

    public static boolean onTreeHarvest(Level level, Player player, BlockPos blockPos) {
        if (!(player instanceof ServerPlayer serverPlayer)) return false;

        BlockState blockState = level.getBlockState(blockPos);
        if (blockState.is(FTZBlockTags.NOT_TRIGGERING_LUMBERJACK_PERK)) return false;
        Block block = blockState.getBlock();
        if (!isTreeLog(block)) return false;

        ItemStack hand = player.getItemInHand(InteractionHand.MAIN_HAND);

        BlockPos tempPos = blockPos.immutable();
        while (level.getBlockState(tempPos.below()).getBlock().equals(block)) {
            tempPos = tempPos.below().immutable();
        }

        for (BlockPos belowPos : BlockPos.betweenClosed(tempPos.getX() - 1,tempPos.getY() - 1,tempPos.getZ()-1, tempPos.getX()+1, tempPos.getY()-1, tempPos.getZ()+1)) {
            if (level.getBlockState(belowPos).getBlock().equals(block)) {
                tempPos = belowPos.immutable();
                while (level.getBlockState(tempPos.below()).getBlock().equals(block)) {
                    tempPos = tempPos.below().immutable();
                }
                break;
            }
        }

        blockPos = tempPos.immutable();

        int logCount = isTreeAndReturnLogAmount(level, blockPos);
        if (logCount < 0) return false;

        int durabilityLoseCount = 1;
        int durabilityStartCount = -1;

        BlockPos highestLogPos = blockPos.immutable();
        List<BlockPos> logsToBreak = getAllLogsToBreak(level, blockPos, block);
        for (BlockPos logPos : logsToBreak) {
            if (logPos.getY() > highestLogPos.getY())
                highestLogPos = logPos.immutable();

            if (!level.getBlockState(logPos).is(FTZBlockTags.IGNORED_BY_LUMBERJACK_PERK)) BlockFunctions.dropBlock(level, logPos);

            if (!player.isCreative()) {
                if (durabilityStartCount == -1) {
                    durabilityStartCount = durabilityLoseCount;
                    ItemFunctions.itemHurtBreakAndEvent(hand, serverPlayer, InteractionHand.MAIN_HAND, 1);
                } else {
                    ItemFunctions.itemHurtBreakAndEvent(hand, serverPlayer, InteractionHand.MAIN_HAND, 1);
                }
                player.causeFoodExhaustion(0.025F);
            }
        }

        breakTreeLeaves(level, logsToBreak, blockPos, highestLogPos);
        return true;
    }

    // credit to Serilum's code
    public static boolean isTreeLog(Block block) {
        try {
            return
                    (CompareBlockFunctions.isTreeLog(block) ||
                            isGiantMushroomStemBlock(block) ||
                            isTreeRoot(block)) &&
                            !block.getName().getString().toLowerCase().contains("stripped");
        }
        catch (Exception ignored) {
            return false;
        }
    }
    public static boolean isTreeLeaf(Block block) {
        return CompareBlockFunctions.isTreeLeaf(block,true) || isGiantMushroomLeafBlock(block);
    }

    public static boolean isNetherTreeLeaf(Block block) {
        return block.equals(Blocks.NETHER_WART_BLOCK) || block.equals(Blocks.WARPED_WART_BLOCK) || block.equals(Blocks.SHROOMLIGHT);
    }

    public static boolean isTreeRoot(Block block) {
        return block instanceof MangroveRootsBlock;
    }

    public static boolean isGiantMushroomStemBlock(Block block) {
        MapColor materialColor = block.defaultMapColor();
        return block instanceof HugeMushroomBlock && materialColor.equals(MapColor.WOOL);
    }

    public static boolean isGiantMushroomLeafBlock(Block block) {
        MapColor materialColour = block.defaultMapColor();
        return block instanceof HugeMushroomBlock && (materialColour.equals(MapColor.COLOR_RED) || materialColour.equals(MapColor.DIRT));
    }

    public static boolean isMangroveRootOrLog(Block block) {
        return block instanceof MangroveRootsBlock || block.equals(Blocks.MANGROVE_LOG);
    }

    public static boolean isAzaleaLeaf(Block block) {
        return block.equals(Blocks.AZALEA_LEAVES) || block.equals(Blocks.FLOWERING_AZALEA_LEAVES);
    }

    public static int isTreeAndReturnLogAmount(Level level, BlockPos pos) {
        int leafCount = 8;
        int logCount = 0;
        int prevLeafCount = -1;
        int prevLogCount = -1;

        int highestY = 0;
        for (int y = 1; y<=30; y+=1) {
            if (prevLeafCount == leafCount && prevLogCount == logCount) {
                break;
            }
            prevLeafCount = leafCount;
            prevLogCount = logCount;

            for (BlockPos blockPos : BlockPos.betweenClosed(pos.getX()-2, pos.getY()+(y-1), pos.getZ()-2, pos.getX()+2, pos.getY()+(y-1), pos.getZ()+2)) {
                BlockState nblockState = level.getBlockState(blockPos);
                Block nblock = nblockState.getBlock();
                if (CompareBlockFunctions.isTreeLeaf(nblock, true) || isGiantMushroomLeafBlock(nblock) || isAzaleaLeaf(nblock)) {
                    if (nblockState.getOptionalValue(LeavesBlock.PERSISTENT).orElse(false)) return -1;

                    leafCount-=1;

                    if (blockPos.getY() > highestY) {
                        highestY = blockPos.getY();
                    }
                }
                else if (isTreeLog(nblock)) {
                    logCount+=1;
                }
            }
        }

        if (leafCount < 0) return logCount;
        return -1;
    }

    public static List<BlockPos> getAllLogsToBreak(Level level, BlockPos pos, Block logType) {
        return getLogsToBreak(level, pos, new ArrayList<>(), logType);
    }

    private static List<BlockPos> getLogsToBreak(Level level, BlockPos pos, List<BlockPos> logsToBreak, Block logType) {
        List<BlockPos> checkAround = new ArrayList<>();

        boolean isMangrove = isMangroveRootOrLog(logType);
        int downY = pos.getY()-1;

        List<BlockPos> aroundLogs = new ArrayList<>();
        for (BlockPos aL : BlockPos.betweenClosed(pos.getX() - 1, pos.getY() - 1, pos.getZ() - 1, pos.getX() + 1, pos.getY() + 1, pos.getZ() + 1)) {
            aroundLogs.add(aL.immutable());
        }

        for (BlockPos aroundLogPos : aroundLogs) {
            if (logsToBreak.contains(aroundLogPos)) {
                continue;
            }

            BlockState logState = level.getBlockState(aroundLogPos);
            Block logblock = logState.getBlock();
            if (logblock.equals(logType) || areEqualLogTypes(logType, logblock)) {
                if (!isMangrove || aroundLogPos.getY() != downY) {
                    checkAround.add(aroundLogPos);
                }
                logsToBreak.add(aroundLogPos);
            }
        }

        if (checkAround.isEmpty()) return logsToBreak;

        for (BlockPos capos : checkAround) {
            for (BlockPos logPos : getLogsToBreak(level, capos, logsToBreak, logType)) {
                if (!logsToBreak.contains(logPos)) {
                    logsToBreak.add(logPos.immutable());
                }
            }
        }

        BlockPos up = pos.above(2);
        return getLogsToBreak(level, up.immutable(), logsToBreak, logType);
    }

    public static boolean areEqualLogTypes(Block one, Block two) {
        if (!isTreeLog(one) || !isTreeLog(two)) {
            return false;
        }

        if (isMangroveRootOrLog(one) && isMangroveRootOrLog(two)) {
            return true;
        }

        String oneIdentifier = one.getName().getString().split(" ")[0];
        String twoIdentifier = two.getName().getString().split(" ")[0];

        return oneIdentifier.equals(twoIdentifier);
    }

    public static void breakTreeLeaves(Level level, List<BlockPos> logsToBreak, BlockPos lowestCenterLogPos, BlockPos highestLogPos) {
        if (level.isClientSide()) {
            return;
        }

        int highestX = Integer.MIN_VALUE; int highestZ = Integer.MIN_VALUE;
        int lowestX = Integer.MAX_VALUE; int lowestZ = Integer.MAX_VALUE;

        for (BlockPos brokenLogPos : logsToBreak) {
            int x = brokenLogPos.getX();
            int z = brokenLogPos.getZ();

            if (x > highestX) {
                highestX = x;
            }
            if (x < lowestX) {
                lowestX = x;
            }
            if (z > highestZ) {
                highestZ = z;
            }
            if (z < lowestZ) {
                lowestZ = z;
            }
        }

        BlockPos highestLeafPos = highestLogPos.above().immutable();
        Block highestLeafBlock = level.getBlockState(highestLeafPos).getBlock();
        if (!isTreeLeaf(highestLeafBlock)) {
            for (BlockPos aroundPos : BlockPosFunctions.getBlocksAround(highestLeafPos, false)) {
                Block aroundBlock = level.getBlockState(aroundPos).getBlock();
                if (isTreeLeaf(aroundBlock)) {
                    highestLeafBlock = aroundBlock;
                    break;
                }
            }
        }

        int distance = 3;
        if (isNetherTreeLeaf(highestLeafBlock)) {
            distance = 5;
        }

        List<BlockPos> treeBlocks = new ArrayList<>();
        for (BlockPos iPos : BlockPos.betweenClosed(lowestX-distance, lowestCenterLogPos.getY()-1, lowestZ-distance, highestX+distance, highestLogPos.getY()+5, highestZ+distance)) {
            treeBlocks.add(iPos.immutable());
        }
        Collections.shuffle(treeBlocks);
    }
}
