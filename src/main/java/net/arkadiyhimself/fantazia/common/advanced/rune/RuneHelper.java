package net.arkadiyhimself.fantazia.common.advanced.rune;

import net.arkadiyhimself.fantazia.common.api.curio.FTZSlots;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.LivingEntity;
import org.apache.commons.compress.utils.Lists;
import top.theillusivec4.curios.api.SlotResult;

import java.util.List;

public class RuneHelper {

    public static List<Holder<Rune>> findRunes(LivingEntity entity) {
        List<SlotResult> slotResults = FantazicUtil.findAllCurios(entity, FTZSlots.RUNE);
        if (slotResults.isEmpty()) return Lists.newArrayList();
        List<Holder<Rune>> runes = Lists.newArrayList();
        for (SlotResult slotResult : slotResults) {
            Holder<Rune> holder = slotResult.stack().get(FTZDataComponentTypes.RUNE);
            if (holder != null && !runes.contains(holder)) runes.add(holder);
        }
        return runes;
    }

    public static boolean hasRune(LivingEntity entity, Holder<Rune> rune) {
        return findRunes(entity).contains(rune);
    }

    public static void tickRunes(LivingEntity entity) {
        findRunes(entity).forEach(runeHolder -> runeHolder.value().onTick(entity));
    }
}
