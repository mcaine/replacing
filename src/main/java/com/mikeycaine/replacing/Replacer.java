package com.mikeycaine.replacing;

public final class Replacer<T> {
	
	final T source;
	boolean replaceTagIfValueNull;
	boolean replaceTagIfValueBlank;
	boolean catchExceptions;
	
	public static <S> Replacer<S> using(S s) {
		return new Replacer<>(s);
	}
	
	public Replacer(T t) {
		this(t, true, true, true);
	}
	
	private Replacer(T t, boolean replaceTagIfValueNull, boolean replaceTagIfValueBlank, boolean catchExceptions) {
		this.source = t;
		this.replaceTagIfValueNull = replaceTagIfValueNull;
		this.replaceTagIfValueBlank = replaceTagIfValueBlank;
		this.catchExceptions = catchExceptions;
	}
	
	public Replacer<T> replaceTagsIfValueNull() {
		return new Replacer<T>(source, true, replaceTagIfValueBlank, catchExceptions);
	}
	
	public Replacer<T> dontReplaceTagsIfValueNull() {
		return new Replacer<T>(source, false, replaceTagIfValueBlank, catchExceptions);
	}
	
	public Replacer<T> replaceTagsIfValueBlank() {
		return new Replacer<T>(source, replaceTagIfValueNull, true, catchExceptions);
	}
	
	public Replacer<T> dontReplaceTagsIfValueBlank() {
		return new Replacer<T>(source, replaceTagIfValueNull, false, catchExceptions);
	}
	
	public Replacer<T> catchingExceptions() {
		return new Replacer<T>(source, replaceTagIfValueNull, replaceTagIfValueBlank, true);
	}
	
	public Replacer<T> notCatchingExceptions() {
		return new Replacer<T>(source, replaceTagIfValueNull, replaceTagIfValueBlank, false);
	}

	public String replaceText(String text, Replacements<T> replacements) {
		return replacements.replaceUsing(source, text, replaceTagIfValueNull, replaceTagIfValueBlank, catchExceptions);
	}
}
