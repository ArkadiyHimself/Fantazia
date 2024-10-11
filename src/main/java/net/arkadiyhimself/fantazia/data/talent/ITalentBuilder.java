package net.arkadiyhimself.fantazia.data.talent;

import net.arkadiyhimself.fantazia.data.talent.types.BasicTalent;

public interface ITalentBuilder<T extends BasicTalent> {
    T build();
}
