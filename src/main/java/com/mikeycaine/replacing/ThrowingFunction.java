package com.mikeycaine.replacing;

@FunctionalInterface
public interface ThrowingFunction<T, R> {
   R apply(T t) throws Exception;
}
