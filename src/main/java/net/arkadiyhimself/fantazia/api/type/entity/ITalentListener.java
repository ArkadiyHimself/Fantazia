package net.arkadiyhimself.fantazia.api.type.entity;

import net.arkadiyhimself.fantazia.data.talent.types.BasicTalent;

public interface ITalentListener {
    void onTalentUnlock(BasicTalent talent);
    void onTalentRevoke(BasicTalent talent);
}
