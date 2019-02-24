package com.mikeycaine.replacing;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class Replacement<T> {
	final static Logger log = LoggerFactory.getLogger(Replacement.class);
	
	private final String tag;
	private final ThrowingFunction<T, String> replacement;
	
	private Replacement(String tag, ThrowingFunction<T, String> replacement) {
		if (tag == null) throw new IllegalArgumentException("Tag can't be null");
		if (replacement == null) throw new IllegalArgumentException("Replacement can't be null");
		
		this.tag = tag;
		this.replacement = replacement;
	}
	
	String getTag() {
		return tag;
	};
	
	String replaceUsing(T t, String text, boolean replaceTagIfValueNull, boolean replaceTagIfValueBlank, boolean catchExceptions) {
		if (text == null) {
			return text;
		}
		
		if (text.contains(tag)) {
			String value = null;
			try {
				value = replacement.apply(t);
			} catch (Exception ex) {
				log.warn(String.format(
						"Tag handler function threw exception when handling tag %s. Exception was: %s",
						tag,
						ex.getMessage())
				);
				
				if (!catchExceptions) {
					throw new RuntimeException(ex);
				}
			}
			
			if (null == value) {
				if (replaceTagIfValueNull) {
					text = text.replace(tag, "");
				}
			} else {
				if (value.trim().isEmpty()) {
					if (replaceTagIfValueBlank) {
						text = text.replace(tag, value);
					}
				} else {
					text = text.replace(tag, value);
				}
			}
		}
		
		return text;
	}
	
	public static <S> Replacement<S> replacing(String tag, ThrowingFunction<S, String> replacement) {
		return new Replacement<S>(tag, replacement);
	}
}


