package net.arkadiyhimself.fantazia.entities;

import net.arkadiyhimself.fantazia.registries.FTZEntityTypes;
import net.arkadiyhimself.fantazia.registries.FTZItems;
import net.arkadiyhimself.fantazia.tags.FTZPaintingTags;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.Painting;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.GameRules;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FantazicPainting extends Painting {

    public FantazicPainting(EntityType<? extends FantazicPainting> entityType, Level level) {
        super(entityType, level);
    }

    public FantazicPainting(Level level, BlockPos pos, Direction direction) {
        this(FTZEntityTypes.FANTAZIC_PAINTING.get(), level);
        this.pos = pos;
        setDirection(direction);

        List<Holder<PaintingVariant>> paintings = new ArrayList<>();
        int maxSurfaceArea = 0;

        for (Holder<PaintingVariant> variant : registryAccess().registryOrThrow(Registries.PAINTING_VARIANT).getOrCreateTag(FTZPaintingTags.FANTAZIC_PLACABLE)) {
            setVariant(variant);

            if (survives()) {
                paintings.add(variant);
                int surfaceArea = variant.value().width() * variant.value().height();
                if (surfaceArea > maxSurfaceArea) maxSurfaceArea = surfaceArea;
            }
        }

        if (!paintings.isEmpty()) {
            Iterator<Holder<PaintingVariant>> iterator = paintings.iterator();

            while (iterator.hasNext()) {
                PaintingVariant paintingType = iterator.next().value();
                if (paintingType.width() * paintingType.height() < maxSurfaceArea) iterator.remove();
            }

            setVariant(paintings.get(this.random.nextInt(paintings.size())));
        }
    }

    @Override
    public void dropItem(@Nullable Entity brokenEntity) {
        if (this.level().getGameRules().getBoolean(GameRules.RULE_DOENTITYDROPS)) {
            this.playSound(SoundEvents.PAINTING_BREAK, 1.0F, 1.0F);
            if (brokenEntity instanceof Player player && player.hasInfiniteMaterials()) return;

            this.spawnAtLocation(FTZItems.FANTAZIC_PAINTING);
        }
    }

    @Override
    public @NotNull ItemStack getPickResult() {
        return new ItemStack(FTZItems.FANTAZIC_PAINTING.get());
    }
}
