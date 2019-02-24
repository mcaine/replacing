package com.mikeycaine.replacing;

import org.hamcrest.CoreMatchers;
import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mikeycaine.replacing.Replacement.replacing;


public class ReplacerTest {

	final static List<Replacement<Person>> listOfPersonReplacements = Arrays.asList(
			replacing("{name}", p -> p.getName()),
			replacing("{age}", p -> String.valueOf(p.getAge()))
	);

	final static Replacements<Person> replacements = new Replacements<>(listOfPersonReplacements);


	final static Replacements<Person> replacementsOf = new Replacements<>(
			replacing("{name}", p -> p.getName()),
			replacing("{age}", p -> String.valueOf(p.getAge()))
	);
	
	
	final static Replacements<Person> replacementsThrowing = new Replacements<>(Arrays.asList(
		replacing("{name}", p -> {throw new Exception("OOPS");}),
		replacing("{age}", p -> String.valueOf(p.getAge()))
		)
	);
	
	final static Replacements<Person> redactedReplacements = replacementsThrowing
																.and(replacing("{name}", p -> "REDACTED"));
	
	final static Replacements<Person> composedReplacements = replacements
																.and(replacing("{name}", p -> "WRONG"))
																.and(replacing("{name}", p -> "WRONG"))
																.and(replacing("{name}", p -> "WRONG"))
																.and(replacing("{name}", p -> "WRONG"))
																.and(replacing("{name}", p -> "WRONG"))
																.and(replacing("{name}", p -> "WRONG"))
																.and(replacing("{name}", p -> "CORRECT"));
	
	final static Replacements<Person> composed2 = redactedReplacements.and(replacements);
	
	final static Replacements<Person> capitalisedReplacements = new Replacements<>(Arrays.asList(
		replacing("{name}", p -> p.getName().toUpperCase()),
		replacing("{age}", p -> String.valueOf(p.getAge()))
		)
	);
	
	final static Replacements<Animal> animalReplacements = new Replacements<>(Arrays.asList(
		replacing("{name}", (Animal a) -> a.getAnimalName())
	)
	);

	@Test
	public void testReplacer() {
		Person mike = new Person("Mike", 51);

		String result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is Mike and my age is 51")));

		// won't compile
		//result = Replacer.using(mike).replaceText("Some text", animalReplacements);

		result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", capitalisedReplacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is MIKE and my age is 51")));

		result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", redactedReplacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is REDACTED and my age is 51")));

		result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", composedReplacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is CORRECT and my age is 51")));

		result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", replacementsThrowing);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is  and my age is 51")));

		result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", composed2);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is Mike and my age is 51")));


		Person fran = new Person("Fran", 21);
		result = Replacer.using(fran).replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is Fran and my age is 21")));

		result = Replacer.using(fran).replaceText("My name is {name} and my age is {age}", capitalisedReplacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is FRAN and my age is 21")));
	}

	@Test
	public void testReplacer_RemovesTagsIfValueIsNullByDefault() {
		Person anonymous = new Person(null, 21);
		String result = Replacer.using(anonymous).replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is  and my age is 21")));
	}

	@Test
	public void testReplacer_RemovesTagsIfValueIsNull() {
		Person anonymous = new Person(null, 21);
		String result = Replacer.using(anonymous)
							.replaceTagsIfValueNull()
							.replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is  and my age is 21")));
	}

	@Test
	public void testReplacer_LeavesTagsIfValueIsNull() {
		Person anonymous = new Person(null, 21);
		String result = Replacer.using(anonymous)
							.dontReplaceTagsIfValueNull()
							.replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is {name} and my age is 21")));
	}

	@Test
	public void testReplacer_RemovesTagsIfValueBlankByDefault() {
		Person blankName = new Person("    ", 69);
		String result = Replacer.using(blankName).replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is      and my age is 69")));
	}

	@Test
	public void testReplacer_RemovesTagsIfValueBlank() {
		Person blankName = new Person("    ", 69);
		String result = Replacer.using(blankName).replaceTagsIfValueBlank().replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is      and my age is 69")));
	}

	@Test
	public void testReplacer_LeavesTagsIfValueBlank() {
		Person blankName = new Person("    ", 69);
		String result = Replacer.using(blankName).dontReplaceTagsIfValueBlank().replaceText("My name is {name} and my age is {age}", replacements);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is {name} and my age is 69")));
	}

	@Test
	public void testReplacer_RemovesTagsIfThrowingByDefault() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
							.replaceText("My name is {name} and my age is {age}", replacementsThrowing);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is  and my age is 69")));
	}

	@Test
	public void testReplacer_RemovesTagsIfThrowing() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
				.replaceTagsIfValueNull()
				.replaceText("My name is {name} and my age is {age}", replacementsThrowing);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is  and my age is 69")));
	}

	@Test
	public void testReplacer_LeavesTagsIfThrowing() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
							.dontReplaceTagsIfValueNull()
							.replaceText("My name is {name} and my age is {age}", replacementsThrowing);
		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is {name} and my age is 69")));
	}

	@Test(expected = RuntimeException.class)
	public void testReplacer_CanThrow() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
							.notCatchingExceptions()
							.replaceText("My name is {name} and my age is {age}", replacementsThrowing);
	}


	public void testReplacer_CatchingExceptions() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
				.catchingExceptions()
				.replaceText("My name is {name} and my age is {age}", replacementsThrowing);

		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is {name} and my age is 69")));
	}

	public void testReplacer_CatchingExceptions_NotReplacingTags() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
							.catchingExceptions()
							.dontReplaceTagsIfValueNull()
							.replaceText("My name is {name} and my age is {age}", replacementsThrowing);

		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is {name} and my age is 69")));
	}

	public void testReplacer_CatchingExceptions_ReplaceTagsIfValueNull() {
		Person bobby = new Person("Bobby", 69);
		String result = Replacer.using(bobby)
							.catchingExceptions()
							.replaceTagsIfValueNull()
							.replaceText("My name is and my age is ", replacementsThrowing);

		Assert.assertThat(result, CoreMatchers.is(CoreMatchers.equalTo("My name is and my age is 69")));
	}
}

class Animal {
	final String name;

	Animal(String name) {
		this.name = name;
	}

	String getAnimalName() {
		return name;
	}
}

class Person {
	final String name;
	final int age;

	Person(String name, int age) {
		this.name = name;
		this.age = age;
	}

	String getName() {
		return name;
	}

	int getAge() {
		return age;
	}
}

