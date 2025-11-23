package net.arkadiyhimself.fantazia.data.talent;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.data.FTZCodecs;
import net.arkadiyhimself.fantazia.data.datagen.talent_reload.hierarchy.TalentHierarchyHolder;
import net.arkadiyhimself.fantazia.data.talent.reload.ServerTalentManager;
import net.arkadiyhimself.fantazia.util.library.hierarchy.ChainHierarchy;
import net.arkadiyhimself.fantazia.util.library.hierarchy.IHierarchy;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.compress.utils.Lists;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;

public class TalentHierarchyBuilder {

    private static final MapCodec<IHierarchy<ResourceLocation>> HIERARCHY_CODEC = FTZCodecs.hierarchyMapCodec("type", "elements", ResourceLocation.CODEC);

    public static final Codec<TalentHierarchyBuilder> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            ResourceLocation.CODEC.fieldOf("tab").forGetter(TalentHierarchyBuilder::getTab),
            HIERARCHY_CODEC.codec().optionalFieldOf("hierarchy").forGetter(builder -> Optional.ofNullable(builder.hierarchy)),
            Codec.INT.optionalFieldOf("simple_chain", 0).forGetter(TalentHierarchyBuilder::getChain),
            Talent.Builder.CODEC.optionalFieldOf("chain_element").forGetter(builder -> Optional.ofNullable(builder.simpleElement))
    ).apply(instance, TalentHierarchyBuilder::builder));

    private static TalentHierarchyBuilder builder(ResourceLocation tab, Optional<IHierarchy<ResourceLocation>> optionalHierarchy, int amount, Optional<Talent.Builder> talentOptional) {
        TalentHierarchyBuilder builder = builder(tab);
        optionalHierarchy.ifPresent(builder::setHierarchy);
        talentOptional.ifPresent(talent -> builder.makeSimpleChain(amount, talent));
        return builder;
    }

    public static TalentHierarchyBuilder builder(ResourceLocation tag) {
        return new TalentHierarchyBuilder().setTab(tag);
    }

    private ResourceLocation tab = null;
    private IHierarchy<ResourceLocation> hierarchy = null;
    private int chain = 0;
    private Talent.Builder simpleElement = null;

    public TalentHierarchyBuilder setTab(ResourceLocation tab) {
        this.tab = tab;
        return this;
    }

    public TalentHierarchyBuilder setHierarchy(IHierarchy<ResourceLocation> hierarchy) {
        this.hierarchy = hierarchy;
        return this;
    }

    public TalentHierarchyBuilder makeSimpleChain(int amount, Talent.Builder talent) throws TalentDataException {
        if (amount <= 1) throw new TalentDataException("Can not make a chain of 1 or less elements");
        this.chain = amount;
        this.simpleElement = talent;
        return this;
    }

    public ResourceLocation getTab() {
        return tab;
    }

    public IHierarchy<ResourceLocation> getHierarchy() {
        return hierarchy;
    }

    public int getChain() {
        return chain;
    }

    public TalentHierarchyHolder holder(ResourceLocation id) {
        return new TalentHierarchyHolder(id, this);
    }

    public void save(Consumer<TalentHierarchyHolder> consumer, ResourceLocation id) {
        consumer.accept(holder(id));
    }

    public @NotNull IHierarchy<ResourceLocation> build(ResourceLocation id) throws TalentDataException {
        if (simpleElement != null) {
            if (chain <= 1) throw new TalentDataException("Can not make a chain of 1 or less elements");
            hierarchy = simpleChain(id, chain);
            for (ResourceLocation chain : hierarchy.getElements()) ServerTalentManager.addSimpleChain(chain, simpleElement.build(chain));
            return hierarchy;
        }
        if (hierarchy == null) throw new TalentDataException("The builder contains neither an hierarchy nor a simple chain!");
        return hierarchy;
    }

    private ChainHierarchy<ResourceLocation> simpleChain(ResourceLocation basic, int amount) {
        List<ResourceLocation> list = Lists.newArrayList();
        for (int i = 1; i <= amount; i++) list.add(basic.withSuffix(String.valueOf(i)));
        return ChainHierarchy.of(list);
    }
}
