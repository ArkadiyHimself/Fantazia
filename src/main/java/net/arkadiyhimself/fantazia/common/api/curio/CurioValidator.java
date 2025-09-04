package net.arkadiyhimself.fantazia.common.api.curio;

import com.google.common.collect.Maps;
import net.arkadiyhimself.fantazia.Fantazia;
import net.arkadiyhimself.fantazia.common.item.casters.DashStoneItem;
import net.arkadiyhimself.fantazia.common.registries.FTZDataComponentTypes;
import net.arkadiyhimself.fantazia.util.wheremagichappens.FantazicUtil;
import net.minecraft.resources.ResourceLocation;
import top.theillusivec4.curios.api.SlotResult;

import java.util.Map;
import java.util.function.Predicate;

public record CurioValidator(ResourceLocation id, Predicate<SlotResult> function) {

    public static final Map<ResourceLocation, CurioValidator> VALIDATORS = Maps.newHashMap();

    public static final CurioValidator FOR_ACTIVECASTER = register(Fantazia.location("for_activecaster"), slotResult -> FantazicUtil.isActiveCaster(slotResult.stack().getItem()));
    public static final CurioValidator FOR_PASSIVECASTER = register(Fantazia.location("for_passivecaster"), slotResult -> FantazicUtil.isPassiveCaster(slotResult.stack().getItem()));
    public static final CurioValidator FOR_DASHSTONE = register(Fantazia.location("for_dashstone"), slotResult -> slotResult.stack().getItem() instanceof DashStoneItem);
    public static final CurioValidator FOR_RUNE = register(Fantazia.location("for_rune"), slotResult -> slotResult.stack().has(FTZDataComponentTypes.RUNE));

    private static CurioValidator register(ResourceLocation id, Predicate<SlotResult> function) {
        CurioValidator validator = new CurioValidator(id, function);
        VALIDATORS.put(id, validator);
        return validator;
    }
}
