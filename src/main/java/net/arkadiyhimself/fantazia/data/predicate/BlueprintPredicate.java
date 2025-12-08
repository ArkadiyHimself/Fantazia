package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.blueprint.Blueprint;
import net.arkadiyhimself.fantazia.common.api.custom_registry.FantazicRegistries;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryCodecs;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.tags.TagKey;

import java.util.Optional;

public record BlueprintPredicate(
        Optional<HolderSet<Blueprint>> blueprints
) {

    public static final Codec<BlueprintPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            RegistryCodecs.homogeneousList(FantazicRegistries.Keys.BLUEPRINT).optionalFieldOf("blueprints").forGetter(BlueprintPredicate::blueprints)
    ).apply(instance, BlueprintPredicate::new));

    public boolean test(Holder<Blueprint> holder) {
        return this.blueprints.isEmpty() || blueprints.get().contains(holder);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private HolderSet<Blueprint> blueprints = null;

        @SafeVarargs
        public final Builder of(Holder<Blueprint>... holders) {
            this.blueprints = HolderSet.direct(holder -> holder, holders);
            return this;
        }

        public Builder of(TagKey<Blueprint> tag) {
            this.blueprints = FantazicRegistries.BLUEPRINTS.getOrCreateTag(tag);
            return this;
        }

        public BlueprintPredicate build() {
            return new BlueprintPredicate(Optional.ofNullable(blueprints));
        }
    }
}
