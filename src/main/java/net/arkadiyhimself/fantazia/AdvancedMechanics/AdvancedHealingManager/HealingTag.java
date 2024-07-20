package net.arkadiyhimself.fantazia.AdvancedMechanics.AdvancedHealingManager;

public class HealingTag {
    public static HealingTag REGEN = new HealingTag("regen");
    public static HealingTag BYPASSES_INVULNERABILITY = new HealingTag("bypasses_invulnerability");
    public static HealingTag SCALES_FROM_SATURATION = new HealingTag("scales_from_saturation");
    public static HealingTag CANNOT_BE_CANCELLED = new HealingTag("cannot_be_cancelled");
    public static HealingTag SELF = new HealingTag("self");
    public static HealingTag UNHOLY = new HealingTag("unholy");
    private final String id;
    public HealingTag(String id) {
        this.id = id;
    }
    public String getId() {
        return id;
    }
}
