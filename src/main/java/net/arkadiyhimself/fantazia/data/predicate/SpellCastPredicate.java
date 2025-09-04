package net.arkadiyhimself.fantazia.data.predicate;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.arkadiyhimself.fantazia.common.advanced.spell.SpellCastResult;
import net.minecraft.advancements.critereon.EntityPredicate;
import net.minecraft.server.level.ServerPlayer;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public record SpellCastPredicate(Optional<Boolean> wasteMana, Optional<Boolean> recharge, Optional<Boolean> success, Optional<EntityPredicate> targetPredicate) {

    public static final Codec<SpellCastPredicate> CODEC = RecordCodecBuilder.create(instance -> instance.group(
            Codec.BOOL.optionalFieldOf("waste_mana").forGetter(SpellCastPredicate::wasteMana),
            Codec.BOOL.optionalFieldOf("recharge").forGetter(SpellCastPredicate::recharge),
            Codec.BOOL.optionalFieldOf("success").forGetter(SpellCastPredicate::success),
            EntityPredicate.CODEC.optionalFieldOf("target").forGetter(SpellCastPredicate::targetPredicate)
    ).apply(instance, SpellCastPredicate::new));

    public boolean matches(ServerPlayer serverPlayer, SpellCastResult result) {
        if (wasteMana.isPresent() && wasteMana.get() != result.wasteMana()) return false;
        if (recharge.isPresent() && recharge.get() != result.recharge()) return false;
        if (success.isPresent() && success.get() != result.success()) return false;
        if (targetPredicate.isPresent() && !targetPredicate.get().matches(serverPlayer, result.target())) return false;

        return true;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {

        private @Nullable Boolean wasteMana = null;
        private @Nullable Boolean recharge = null;
        private @Nullable Boolean success = null;
        private @Nullable EntityPredicate targetPredicate = null;

        public Builder wasteMana(boolean wasteMana) {
            this.wasteMana = wasteMana;
            return this;
        }

        public Builder recharge(boolean recharge) {
            this.recharge = recharge;
            return this;
        }

        public Builder success(boolean success) {
            this.success = success;
            return this;
        }

        public Builder entity(EntityPredicate predicate) {
            this.targetPredicate = predicate;
            return this;
        }

        public SpellCastPredicate build() {
            return new SpellCastPredicate(
                    Optional.ofNullable(wasteMana),
                    Optional.ofNullable(recharge),
                    Optional.ofNullable(success),
                    Optional.ofNullable(targetPredicate)
            );
        }
    }
}
