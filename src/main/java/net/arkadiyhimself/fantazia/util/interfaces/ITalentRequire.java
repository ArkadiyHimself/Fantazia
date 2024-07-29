package net.arkadiyhimself.fantazia.util.interfaces;

import net.arkadiyhimself.fantazia.advanced.capacity.abilityproviding.Talent;

public interface ITalentRequire {
    Talent required();
    void onTalentUnlock(Talent talent);
}
