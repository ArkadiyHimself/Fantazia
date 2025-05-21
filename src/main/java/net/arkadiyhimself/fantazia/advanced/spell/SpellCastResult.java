package net.arkadiyhimself.fantazia.advanced.spell;

public record SpellCastResult(boolean wasteMana, boolean recharge, boolean success) {

    public static SpellCastResult defaultResult() {
        return new SpellCastResult(true, true, true);
    }

    public static SpellCastResult noRecharge() {
        return new SpellCastResult(true, false, true);
    }

    public static SpellCastResult keepMana() {
        return new SpellCastResult(false, true, true);
    }

    public static SpellCastResult free() {
        return new SpellCastResult(false, false, true);
    }

    public static SpellCastResult fail() {
        return new SpellCastResult(false, false, false);
    }

    public static SpellCastResult blocked(boolean wasteMana, boolean recharge, boolean success) {
        return new SpellCastResult(wasteMana, recharge,success);
    }
}
