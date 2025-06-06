package net.arkadiyhimself.fantazia.datagen.effect_from_damage;

import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.data.effect_from_damage.EffectFromDamage;
import net.arkadiyhimself.fantazia.data.predicate.DamageTypePredicate;
import net.arkadiyhimself.fantazia.datagen.SubProvider;
import net.arkadiyhimself.fantazia.registries.FTZMobEffects;
import net.arkadiyhimself.fantazia.tags.FTZDamageTypeTags;
import net.minecraft.core.HolderLookup;
import net.minecraft.world.damagesource.DamageTypes;

import java.util.function.Consumer;

public class DefaultEffectsFromDamage implements SubProvider<EffectFromDamageHolder> {

    public static DefaultEffectsFromDamage create() {
        return new DefaultEffectsFromDamage();
    }

    @Override
    public void generate(HolderLookup.Provider provider, Consumer<EffectFromDamageHolder> consumer) {
        EffectFromDamage.builder(DamageTypePredicate.builder().addDamageTypes(DamageTypes.FREEZE).build())
                .addMobEffect(FTZMobEffects.FROZEN, 100)
                .save(consumer, Fantazia.res("freeze"));

        EffectFromDamage.builder(DamageTypePredicate.builder().addDamageTypes(DamageTypes.LIGHTNING_BOLT).build())
                .addMobEffect(FTZMobEffects.ELECTROCUTED, 100)
                .save(consumer, Fantazia.res("electrocute"));

        EffectFromDamage.builder(DamageTypePredicate.builder().addTagPredicates(FTZDamageTypeTags.DEAFENING).build())
                .addMobEffect(FTZMobEffects.DEAFENED, 200)
                .save(consumer, Fantazia.res("deafen"));
    }
}
