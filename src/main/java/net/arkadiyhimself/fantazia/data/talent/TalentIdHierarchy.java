package net.arkadiyhimself.fantazia.data.talent;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.FTZCodecs;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.datagen.talent_reload.hierarchy.TalentHierarchyHolder;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public record TalentIdHierarchy(
        IHierarchy<ResourceLocation> hierarchy,
        boolean drawLine,
        Optional<Pair<Integer, Integer>> lineColors
) {

    public static class Builder {

        private static final MapCodec<IHierarchy<ResourceLocation>> HIERARCHY_CODEC = FTZCodecs.hierarchyMapCodec("type", "elements", ResourceLocation.CODEC);

        public static final Codec<Builder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
                ResourceLocation.CODEC.fieldOf("tab").forGetter(builder -> builder.tab),
                HIERARCHY_CODEC.codec().optionalFieldOf("hierarchy").forGetter(builder -> Optional.ofNullable(builder.hierarchy)),
                Codec.INT.optionalFieldOf("simple_chain", 0).forGetter(builder -> builder.chain),
                Talent.Builder.CODEC.optionalFieldOf("chain_element").forGetter(builder -> Optional.ofNullable(builder.simpleElement)),
                FTZCodecs.pairCodec("outline","insides", Codec.INT, Codec.INT).optionalFieldOf("line_colors").forGetter(builder -> Optional.ofNullable(builder.lineColors)),
                Codec.BOOL.optionalFieldOf("draw_line",true).forGetter(builder -> builder.drawLine)
        ).apply(instance, Builder::builder));

        private static Builder builder(
                ResourceLocation tab,
                Optional<IHierarchy<ResourceLocation>> optionalHierarchy,
                int amount,
                Optional<Talent.Builder> talentOptional,
                Optional<Pair<Integer, Integer>> lineColors,
                boolean drawLine
        ) {
            Builder builder = builder(tab);
            if (!drawLine) builder.noLine();
            optionalHierarchy.ifPresent(builder::setHierarchy);
            talentOptional.ifPresent(talent -> builder.makeSimpleChain(amount, talent));
            lineColors.ifPresent(builder::lineColors);
            return builder;
        }

        public static Builder builder(ResourceLocation tag) {
            return new Builder().setTab(tag);
        }

        private ResourceLocation tab = null;
        private IHierarchy<ResourceLocation> hierarchy = null;
        private int chain = 0;
        private Talent.Builder simpleElement = null;
        private Pair<Integer, Integer> lineColors = null;
        private boolean drawLine = true;

        public Builder setTab(ResourceLocation tab) {
            this.tab = tab;
            return this;
        }

        public Builder setHierarchy(IHierarchy<ResourceLocation> hierarchy) {
            this.hierarchy = hierarchy;
            return this;
        }

        public Builder makeSimpleChain(int amount, Talent.Builder talent) throws TalentDataException {
            if (amount <= 1) throw new TalentDataException("Can not make a chain of 1 or less elements");
            this.chain = amount;
            this.simpleElement = talent;
            return this;
        }

        public Builder lineColors(Pair<Integer, Integer> lineColors) {
            this.lineColors = lineColors;
            return this;
        }

        public Builder lineColors(int outline, int insides) {
            return lineColors(new Pair<>(outline, insides));
        }

        public Builder noLine() {
            this.drawLine = false;
            return this;
        }

        public ResourceLocation getTab() {
            return tab;
        }

        public IHierarchy<ResourceLocation> getHierarchy() {
            return hierarchy;
        }

        public TalentHierarchyHolder holder(ResourceLocation id) {
            return new TalentHierarchyHolder(id, this);
        }

        public void save(Consumer<TalentHierarchyHolder> consumer, ResourceLocation id) {
            consumer.accept(holder(id));
        }

        public @NotNull TalentIdHierarchy build(ResourceLocation id) throws TalentDataException {
            if (simpleElement != null) {
                if (chain <= 1) throw new TalentDataException("Can not make a chain of 1 or less elements");
                hierarchy = simpleChain(id, chain);
                for (ResourceLocation chain : hierarchy.getElements()) ServerTalentManager.addSimpleChain(chain, simpleElement.build(chain));
            }
            if (hierarchy == null) throw new TalentDataException("The builder contains neither an hierarchy nor a simple chain!");
            return new TalentIdHierarchy(
                    hierarchy,
                    drawLine,
                    Optional.ofNullable(lineColors)
            );
        }

        private ChainHierarchy<ResourceLocation> simpleChain(ResourceLocation basic, int amount) {
            List<ResourceLocation> list = Lists.newArrayList();
            for (int i = 1; i <= amount; i++) list.add(basic.withSuffix(String.valueOf(i)));
            return ChainHierarchy.of(list);
        }
    }

}
