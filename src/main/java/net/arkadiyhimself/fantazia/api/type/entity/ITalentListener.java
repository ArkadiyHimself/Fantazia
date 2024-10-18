package net.arkadiyhimself.fantazia.api.type.entity;

import net.arkadiyhimself.fantazia.data.talent.types.ITalent;

public interface ITalentListener {
    void onTalentUnlock(ITalent talent);
    void onTalentRevoke(ITalent talent);
}
