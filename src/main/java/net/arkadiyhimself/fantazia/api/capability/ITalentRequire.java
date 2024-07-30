package net.arkadiyhimself.fantazia.api.capability;

import net.arkadiyhimself.fantazia.advanced.capacity.abilityproviding.Talent;

public interface ITalentRequire {
    Talent required();
    void onTalentUnlock(Talent talent);
}
