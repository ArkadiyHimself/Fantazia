package net.arkadiyhimself.fantazia.data.talent;

import net.arkadiyhimself.fantazia.data.talent.types.*;

public enum TalentType {

    BASIC(BasicTalent.Builder.class, "basic"),
    ATTRIBUTE(AttributeTalent.Builder.class,"attribute_modifier"),
    CURIOS(CurioTalent.Builder.class, "curios_modifier");

    private final Class<? extends ITalentBuilder<? extends ITalent>> builderClass;
    private final String ident;

    TalentType(Class<? extends ITalentBuilder<?>> builderClass, String ident) {
        this.builderClass = builderClass;
        this.ident = ident;
    }

    public Class<? extends ITalentBuilder<?>> getBuilderClass() {
        return this.builderClass;
    }

    public static TalentType byId(String ident) {
        TalentType[] talentTypes = TalentType.values();
        for (TalentType type : talentTypes) if (type.ident.equals(ident)) return type;
        return BASIC;
    }
}
