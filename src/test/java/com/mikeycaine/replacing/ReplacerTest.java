package com.mikeycaine.replacing;

import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static com.mikeycaine.replacing.Replacement.replacing;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;


public class ReplacerTest {

    final Person mike = new Person("Mike", 51);
    final Person fran = new Person("Fran", 21);
    final Person blankName = new Person("    ", 69);
    final Person bobby = new Person("Bobby", 69);
    final Person anonymous = new Person(null, 21);

    final static Replacements<Person> replacements = new Replacements<>(
        replacing("{name}", p -> p.getName()),
        replacing("{age}",  p -> String.valueOf(p.getAge()))
    );

    final static Replacements<Animal> animalReplacements = new Replacements<>(
        replacing("{name}", (Animal a) -> a.getAnimalName())
    );

    final static Replacements<Person> replacementsThrowing = new Replacements<>(
        replacing("{name}", p -> {throw new Exception("OOPS");}),
        replacing("{age}", p -> String.valueOf(p.getAge()))
    );

    @Test
    public void testReplacer_Using() {
        String result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is Mike and my age is 51")));

        result = Replacer.using(fran).replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is Fran and my age is 21")));
    }

    @Test
    public void testReplacer_CanConstructFromList() {

        List<Replacement<Person>> listOfPersonReplacements = Arrays.asList(
            replacing("{name}", p -> p.getName()),
            replacing("{age}", p -> String.valueOf(p.getAge()))
        );

        Replacements<Person> replacementsFromList = new Replacements<>(listOfPersonReplacements);

        String result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", replacementsFromList);
        Assert.assertThat(result, is(equalTo("My name is Mike and my age is 51")));

        result = Replacer.using(fran).replaceText("My name is {name} and my age is {age}", replacementsFromList);
        Assert.assertThat(result, is(equalTo("My name is Fran and my age is 21")));
    }

    @Test
    public void test_CanComposeReplacements() {

        Replacements<Person> composedReplacements = replacements
            .and(replacing("{name}", p -> "WRONG"))
            .and(replacing("{name}", p -> "WRONG"))
            .and(replacing("{name}", p -> "WRONG"))
            .and(replacing("{name}", p -> "WRONG"))
            .and(replacing("{age}",  p -> "A SECRET"))
            .and(replacing("{name}", p -> "WRONG"))
            .and(replacing("{name}", p -> "WRONG"))
            .and(replacing("{name}", p -> "CORRECT"));

        String result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", composedReplacements);
        Assert.assertThat(result, is(equalTo("My name is CORRECT and my age is A SECRET")));

        Replacements<Person> composed2 = composedReplacements.and(replacements);
        result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", composed2);
        Assert.assertThat(result, is(equalTo("My name is Mike and my age is 51")));
    }


    @Test
    public void testReplacer_CatchesExceptions_ByDefault() {
        Replacements<Person> replacementsThrowing = new Replacements<>(Arrays.asList(
            replacing("{name}", p -> {throw new Exception("OOPS");}),
            replacing("{age}", p -> String.valueOf(p.getAge()))
        ));

        String result = Replacer.using(mike).replaceText("My name is {name} and my age is {age}", replacementsThrowing);
        Assert.assertThat(result, is(equalTo("My name is  and my age is 51")));
    }

    @Test
    public void testReplacer_RemovesTagsIfValueIsNullByDefault() {
        Replacements<Person> replacements = new Replacements<>(
            replacing("{name}", p -> p.getName()),
            replacing("{age}",  p -> String.valueOf(p.getAge()))
        );

        String result = Replacer.using(anonymous).replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is  and my age is 21")));
    }

    @Test
    public void testReplacer_RemovesTagsIfValueIsNull() {
        String result = Replacer.using(anonymous)
            .replaceTagsIfValueNull()
            .replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is  and my age is 21")));
    }

    @Test
    public void testReplacer_LeavesTagsIfValueIsNull() {
        String result = Replacer.using(anonymous)
            .dontReplaceTagsIfValueNull()
            .replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is {name} and my age is 21")));
    }

    @Test
    public void testReplacer_RemovesTagsIfValueBlankByDefault() {
        String result = Replacer.using(blankName).replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is      and my age is 69")));
    }

    @Test
    public void testReplacer_RemovesTagsIfValueBlank() {
        String result = Replacer.using(blankName).replaceTagsIfValueBlank().replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is      and my age is 69")));
    }

    @Test
    public void testReplacer_LeavesTagsIfValueBlank() {
        String result = Replacer.using(blankName).dontReplaceTagsIfValueBlank().replaceText("My name is {name} and my age is {age}", replacements);
        Assert.assertThat(result, is(equalTo("My name is {name} and my age is 69")));
    }

    @Test
    public void testReplacer_RemovesTagsIfThrowingByDefault() {
        String result = Replacer.using(bobby)
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);
        Assert.assertThat(result, is(equalTo("My name is  and my age is 69")));
    }

    @Test
    public void testReplacer_RemovesTagsIfThrowing() {
        String result = Replacer.using(bobby)
            .replaceTagsIfValueNull()
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);
        Assert.assertThat(result, is(equalTo("My name is  and my age is 69")));
    }

    @Test
    public void testReplacer_LeavesTagsIfThrowing() {
        String result = Replacer.using(bobby)
            .dontReplaceTagsIfValueNull()
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);
        Assert.assertThat(result, is(equalTo("My name is {name} and my age is 69")));
    }

    @Test
    public void testReplacer_CatchesExceptions() {
        Replacements<Person> replacementsThrowing = new Replacements<>(Arrays.asList(
            replacing("{name}", p -> {throw new Exception("OOPS");}),
            replacing("{age}", p -> String.valueOf(p.getAge()))
        ));

        String result = Replacer.using(mike)
            .catchingExceptions()
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);
        Assert.assertThat(result, is(equalTo("My name is  and my age is 51")));
    }

    @Test(expected = RuntimeException.class)
    public void testReplacer_CanThrow() {
        String result = Replacer.using(bobby)
            .notCatchingExceptions()
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);
    }

    public void testReplacer_CatchingExceptions() {
        String result = Replacer.using(bobby)
            .catchingExceptions()
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);

        Assert.assertThat(result, is(equalTo("My name is {name} and my age is 69")));
    }

    public void testReplacer_CatchingExceptions_NotReplacingTags() {
        String result = Replacer.using(bobby)
            .catchingExceptions()
            .dontReplaceTagsIfValueNull()
            .replaceText("My name is {name} and my age is {age}", replacementsThrowing);

        Assert.assertThat(result, is(equalTo("My name is {name} and my age is 69")));
    }

    public void testReplacer_CatchingExceptions_ReplaceTagsIfValueNull() {
        String result = Replacer.using(bobby)
            .catchingExceptions()
            .replaceTagsIfValueNull()
            .replaceText("My name is and my age is ", replacementsThrowing);

        Assert.assertThat(result, is(equalTo("My name is and my age is 69")));
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
}



