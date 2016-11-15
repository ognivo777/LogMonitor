package ru.lanit.dibr.utils.core;

/**
 * Created by Vova on 18.12.2015.
 */
public class NoFilterImpl implements Filter {
    @Override
    public Source apply(Source source) {
        return source;
    }

    @Override
    public void disable() {
    }

    @Override
    public boolean isActive() {
        return true;
    }
}
