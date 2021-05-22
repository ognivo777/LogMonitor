package ru.lanit.dibr.utils.core;

/**
 * Created by Vova on 18.12.2015.
 */
public interface Filter {

    Source apply(Source source);

    void disable();

    boolean isActive();
}
