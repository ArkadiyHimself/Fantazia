package net.arkadiyhimself.fantazia.util.library;

import java.util.ArrayList;

public class SizedList<T> extends ArrayList<T> {
    private final int maxSize;
    public SizedList(int maxSize) {
        this.maxSize = maxSize;
    }
    public int getMaxSize() {
        return maxSize;
    }

    @Override
    public boolean add(T t) {
        super.add(t);
        if (this.size() > maxSize) this.remove(0);
        return true;
    }
}
