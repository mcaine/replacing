package com.mikeycaine.replacing;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class Replacements<T> {

    private List<Replacement<T>> myListOfReplacements = new ArrayList<>();

    @SafeVarargs
    public Replacements(Replacement<T>... replacementArray) {
        for (Replacement<T> replacement : replacementArray) {
            addReplacement(replacement);
        }
    }

    public Replacements(List<Replacement<T>> replacementList) {
        for (Replacement<T> replacement : replacementList) {
            addReplacement(replacement);
        }
    }

    public Replacements<T> and(Replacement<T> another) {
        List<Replacement<T>> newList = new ArrayList<>();
        newList.addAll(myListOfReplacements);
        newList.add(another);
        return new Replacements<T>(newList);
    }

    public Replacements<T> and(Replacements<T> more) {
        List<Replacement<T>> newList = new ArrayList<>();
        newList.addAll(myListOfReplacements);
        newList.addAll(more.getReplacements());
        return new Replacements<T>(newList);
    }

    public String replaceUsing(T t, String text, boolean replaceTagIfValueNull, boolean replaceTagIfValueBlank, boolean catchExceptions) {
        String result = text;
        for (Replacement<T> replacement : myListOfReplacements) {
            result = replacement.replaceUsing(t, result, replaceTagIfValueNull, replaceTagIfValueBlank, catchExceptions);
        }
        return result;
    }

    private List<Replacement<T>> getReplacements() {
        return myListOfReplacements;
    }

    private void addReplacement(Replacement<T> replacement) {
        myListOfReplacements = myListOfReplacements
            .stream()
            .filter(r -> r.getTag() != replacement.getTag())
            .collect(Collectors.toList());

        myListOfReplacements.add(replacement);
    }
}
