package com.mikeycaine.replacing;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

public class ReplacementTest {
	
	@Test
	public void test() {
		
		class SomeClass {};
		SomeClass someObj = new SomeClass();
		
		Replacement<SomeClass> replacement = Replacement.replacing("{greeting}", sc -> "hello");
				
		String result = replacement.replaceUsing(someObj, "{greeting}", true, true, true);
		
		Assert.assertThat(result, CoreMatchers.is("hello"));
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_NullTag() {
		
		class SomeClass {};
		
		@SuppressWarnings("unused")
		Replacement<SomeClass> replacement = Replacement.replacing(null, sc -> "hello");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void test_NullFunction() {
		
		class SomeClass {};
		
		@SuppressWarnings("unused")
		Replacement<SomeClass> replacement = Replacement.replacing("{greeting}", null);
	}
	
	@Test
	public void test_FunctionThrowsException() {
		
		class SomeClass {};
		SomeClass someObj = new SomeClass();
		
		Replacement<SomeClass> replacement = Replacement.replacing("{greeting}", sc -> {throw new RuntimeException("Wut?");});
		
		// If the replacement function throws an exception, the tag should be replaced
		String result = replacement.replaceUsing(someObj, "{greeting}", true, true, true);
		Assert.assertThat(result, CoreMatchers.is(""));
		
		// If the replacement function throws an exception, the tag should NOT be replaced
		result = replacement.replaceUsing(someObj, "{greeting}", false, false, true);
		Assert.assertThat(result, CoreMatchers.is("{greeting}"));
	}

	@Test
	public void test_TemplateIsNull() {
		class SomeClass {};
		SomeClass someObj = new SomeClass();

		Replacement<SomeClass> replacement = Replacement.replacing("{greeting}", sc -> {throw new RuntimeException("Wut?");});

		// If the replacement function throws an exception, the tag should be replaced
		String result = replacement.replaceUsing(someObj, null, true, true, true);
		Assert.assertTrue(null == result);

	}

	@Test
	public void test_ObjectIsNull() {
		class SomeClass {};

		Replacement<SomeClass> replacement = Replacement.replacing("{greeting}", sc -> {throw new RuntimeException("Wut?");});

		// If the replacement function throws an exception, the tag should be replaced
		String result = replacement.replaceUsing(null, "{greeting}", true, true, true);
		Assert.assertThat(result, CoreMatchers.is(""));

	}

	@Test
	public void test_ObjectIsNullNotReplacingNull() {
		class SomeClass {};

		Replacement<SomeClass> replacement = Replacement.replacing("{greeting}", sc -> {throw new RuntimeException("Wut?");});

		// If the replacement function throws an exception, the tag should be replaced
		String result = replacement.replaceUsing(null, "{greeting}", false, true, true);
		Assert.assertThat(result, CoreMatchers.is("{greeting}"));

	}
}
