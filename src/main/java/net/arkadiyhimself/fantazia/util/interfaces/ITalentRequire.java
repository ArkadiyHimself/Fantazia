package net.arkadiyhimself.fantazia.util.interfaces;

import net.arkadiyhimself.fantazia.advanced.capacity.AbilityProviding.Talent;

public interface ITalentRequire {
    Talent required();
    void onTalentUnlock(Talent talent);
}
