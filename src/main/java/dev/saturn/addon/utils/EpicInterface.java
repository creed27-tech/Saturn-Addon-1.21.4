package dev.saturn.addon.utils;

@FunctionalInterface
public interface EpicInterface<T, E> {
    E get(T t);
}