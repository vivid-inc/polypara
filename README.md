# Vivid Cherimoya

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](LICENSE.txt)
[![Current version](https://img.shields.io/badge/JCenter-v1.0-239922.svg?style=flat-square)](https://bintray.com/vivid/vivid/vivid%3Acherimoya)

Automated verification of Java field value constancy across release versions.


For when you look at a constant field in Java and think to yourself: "The value of this field *must not change*, even in successive versions."
Appropriate for values that are exposed to and relied upon by software outside of your realm of concern or with whom you have a standing promise to keep keywords stable, such as API clients and database values.
Its intentionalist approach enhances your pull-request code reviews and build log reviews.

Cherimoya is comprised of a feather-weight Java annotation and a Maven plugin that optionally breaks the build in cases of violations.
Developed, tested, and relied upon with Java JDK version 1.8+ and Apache Maven 3.3+.


## Using Cherimoya in your project

In your Maven `pom.xml`, add a dependency to Cherimoya's lightweight library containing the `@Constant` annotation:

```xml
<dependency>
    <groupId>vivid.cherimoya</groupId>
    <artifactId>cherimoya</artifactId>
    <version>1.0</version>
    <scope>compile</scope>
</dependency>
```

In your Java code, annotate the appropriate fields with the `@Constant` annotation:

```java
@Constant
static final int PaymentProcessingTimeoutSecs = 30;
```

The annotation itself is retained in Java class files but not during runtime.
Cherimoya depends on the field value's `equals` method for determining constancy between versions.

Include Cherimoya's verification step in your Maven build by adding the following segment to your Maven `pom.xml`.
List each version of your project in the order they were released.
The order of appearance is important because it sets the sequential progression of release version; the ordering directly affects how field value changes are detected and reported.

The current project's version (from its GAV in the POM) is automatically appended to the end of this list.
Alternatively, it can be explicitly added to the version list to control ordering of versions.

```xml
<build>
    ...
    <plugin>
        <groupId>vivid.cherimoya</groupId>
        <artifactId>cherimoya-maven-plugin</artifactId>
        <version>1.0</version>
        <executions>
            <execution>
                <goals>
                    <goal>verify</goal>
                </goals>
                <configuration>
                    <versions>
                        <version>0.2</version>
                        <version>0.2.1</version>
                        ...
                        <version>2.6.5</version>
                    </versions>
                </configuration>
            </execution>
        </executions>
    </plugin>
    ...
</build>
```

Run the build to confirm whether your `@Constant` field values are indeed constant over the set of releases.

```bash
$ mvn install
...
[INFO] --- cherimoya-maven-plugin:1.0:verify (default) @ spyra-levorg ---
[INFO] Verifying constancy of @Constant field values in 17 versions of com.spyra:levorg  0.2  0.2.1  ...
[INFO]
[ERROR] @Constant field value violation:  com.spyra.levorg.internal.db.PaymentProcessingTimeoutSecs
[ERROR]   In versions  0.2 ~ 2.6.5     its value is:  30
[ERROR]   In versions  3.0.0-SNAPSHOT  its value is:  5
...
```

__Don't break the build__ by changing the `reportingLevel` configuration parameter from its default of `ERROR` to a `WARNING` instead:

```xml
<configuration>
    <reportingLevel>WARNING</reportingLevel>
</configuration>
```

__Skip execution__ by setting the `skip` configuration property to `true` within plugin's `configuration`:

```xml
<configuration>
    <skip>true</skip>
</configuration>
```

or by defining the `cherimoya.skip` system property to `mvn` at the CLI:

```bash
mvn ... -Dcherimoya.skip ...
```



## Development

Run the tests and build the deliverables:

```bash
bin/test.sh
```



## TODO

Document:
- Cherimoya applies to a single artifact version.
- The only requirement of the build verification step is that there are Java .class files in the build output directory, and at least one other build artifact to compare against. The type of the Maven project is irrelevant. If `target/classes` is missing or there are no jars, the verify goal silently does nothing.
- The impact that using Cherimoya has on your deliverables is that select classes are annotated with a single new class: the `@Constant` annotation. The annotation is included in the JAR and made available on the class path, and its reference is retained by the annotated class files.
- Expect results after two different versions are in play. You can back-implement `@Constant` by releasing for example `1.3.1-1`.
- Document instructions for incorporating (including fetching the JARs of) and using both the annotation JAR and the Maven plugin. Specify when the annotation should be used. Ensure that the JCenter Maven repository is included in your Maven configuration.

Do:
- Code cleanup
- Tests
- Support the refactoring of a `@Constant` field's FQN or type.
- Bake motivation, principles, and design decisions into the documentation and the code.
- Ensure that Maven can access all indicated versions.
- Publish to JCenter. Maven Central won't accept our Maven G:A because we don't control the "vivid" TLD.
- Set up a build on CI, integrate with SonarQube.


© Copyright Vivid Inc.
