package net.arkadiyhimself.fantazia.common.advanced.spell;

import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.Nullable;

public record SpellCastResult(
        boolean wasteMana,
        boolean recharge,
        boolean success,
        @Nullable Entity target
) {

    public static final SpellCastResult DEFAULT = new SpellCastResult(true, true, true, null);

    public static final SpellCastResult NO_RECHARGE = new SpellCastResult(true, false, true, null);

    public static final SpellCastResult KEEP_MANA = new SpellCastResult(false, true, true, null);

    public static final SpellCastResult FREE = new SpellCastResult(false, false, true, null);

    public static final SpellCastResult FAIL = new SpellCastResult(false, false, false, null);

    public static SpellCastResult blocked(boolean wasteMana, boolean recharge, boolean success, @Nullable Entity target) {
        return new SpellCastResult(wasteMana, recharge, success, target);
    }

    public SpellCastResult withTarget(@Nullable Entity target) {
        return new SpellCastResult(this.wasteMana, this.recharge, this.success, target);
    }
}
