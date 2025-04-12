package net.arkadiyhimself.fantazia.api.attachment.entity.player_ability;

import net.arkadiyhimself.fantazia.data.talent.types.ITalent;

public interface ITalentListener {
    void onTalentUnlock(ITalent talent);
    void onTalentRevoke(ITalent talent);
}
