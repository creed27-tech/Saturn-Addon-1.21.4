package dev.saturn.addon.utils.lemon.misc;

@FunctionalInterface
public interface EpicInterface<T, E> {
    E get(T t);
}