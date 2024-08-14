package net.arkadiyhimself.fantazia.api.capability;

import net.arkadiyhimself.fantazia.data.talents.BasicTalent;

public interface ITalentRequire {
    void onTalentUnlock(BasicTalent talent);
    void onTalentRevoke(BasicTalent talent);
}
