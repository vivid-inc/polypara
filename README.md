# Cherimoya: Toolkit for software quality automation

Provides a mechanism to ensure that Java language constants are constant throughout a Java release.
Comprised of a Java annotation and a Maven plugin.
Requires JDK 1.7 or higher.

*Cherimoya has not reached a 1.0 release. It still needs to be vetted in real-world usage before we can commit ourselves to its final form.*

To add a dependency on Cherimoya using Maven, use the following:

```xml
<dependency>
  <groupId>vivid.cherimoya</groupId>
  <artifactId>cherimoya</artifactId>
  <version>1.0-SNAPSHOT</version>
  <scope>compile</scope>
</dependency>
```

Annotate fields with {@Constant} and then run the Maven plugin.
Ensure that all desired versions are available in your local Maven repo.
Ignores field visibility.

The impact of using Cherimoya in your project is that a single new class, the {@Constant} annotation,
is added to your annotated classes and the annotation is retained in class files.

Skip execution by setting the ``skip'' configuration parameter to ``true''.

## Links

- [Motivating question on StackOverflow](https://stackoverflow.com/questions/41393794/good-practices-for-breaking-maven-build-when-specific-class-members-change-val)

## TODO

- Create the Maven Plugin project, code, tests, and usage documentation.
- Document instructions for incorporating (including fetching the JARs of) and using both the annotation JAR and the Maven plugin. Specify when the annotation should be used.
- Bake the motivation and principles into the documentation.
