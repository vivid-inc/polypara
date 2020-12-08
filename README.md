# Vivid Polypara

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](LICENSE.txt)
[![Current version](https://img.shields.io/clojars/v/vivid.polypara/polypara-maven-plugin?color=blue&style=flat-square)](https://clojars.org/search?q=vivid.polypara)
[![CircleCI build status](https://circleci.com/gh/vivid-inc/polypara/tree/release-0.4.0.svg)](https://circleci.com/gh/vivid-inc/polypara)
[![SonarCloud](https://sonarcloud.io/api/project_badges/measure?project=vivid-inc_polypara&metric=alert_status)](https://sonarcloud.io/dashboard?id=vivid-inc_polypara)


ポリパラ   Automated verification of Java field value constancy across release versions.


For when you look at a constant field in Java and think to yourself: "The value of this field *must not change*, even in successive versions."
Appropriate for values that are exposed to and relied upon by software outside of your realm of concern or with whom you have a standing promise to keep keywords stable, such as API clients and database values.
Its intentionalist approach adds rigor to your engineering process and enhances your pull-request code reviews and build log reviews.

Polypara is comprised of a feather-weight Java annotation retained only in class files and a Maven plugin that optionally breaks the build in cases of violations.
Developed, tested, and relied upon with Java JDK version 1.8+ and Apache Maven 3.3+.


## Using Polypara in your project

First, ensure your Maven build uses the Clojars repository for dependency and plugin resolution with this snippet:
```xml
<repository>
    <id>clojars.org</id>
    <url>https://repo.clojars.org/</url>
</repository>
```
in each of the `<repositories>` and `<pluginRepositories>` sections in the appropriate Maven
configuration, such as your `pom.xml`.

In your Maven `pom.xml`, add a dependency to Polypara's lightweight library containing the annotations:

```xml
<dependency>
    <groupId>vivid.polypara</groupId>
    <artifactId>polypara-annotations</artifactId>
    <version>1.0.0</version>
    <scope>compile</scope>
</dependency>
```

In your Java code, annotate the appropriate fields with the `@Constant` annotation:

```java
@Constant
static final int PaymentProcessingTimeoutSecs = 30;
```

The annotation itself is retained in Java class files but not during runtime.
Polypara depends on the field value's `equals` method for determining value constancy between versions.

Include Polypara's verification step in your Maven build by adding the following segment to your Maven `pom.xml`.
List each version of your project in the order they were released.
The order of appearance is important because it sets the sequential progression of release version; the ordering directly affects how field value changes are detected and reported.

The current project's version (from its GAV in the POM) is automatically appended to the end of this list.
Alternatively, it can be explicitly added to the version list to control ordering of versions.

```xml
<build>
    ...
    <plugin>
        <groupId>vivid.polypara</groupId>
        <artifactId>polypara-maven-plugin</artifactId>
        <version>0.4.0</version>
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
[INFO] --- polypara-maven-plugin:0.4.0:verify (default) @ spyra-levorg ---
[INFO] Verifying constancy of @Constant field values in 17 versions of com.spyra:levorg  0.2  0.2.1  ...
[INFO]
[ERROR] @Constant field value violation:  com.spyra.levorg.internal.db.PaymentProcessingTimeoutSecs
[ERROR]   In versions  0.2 ~ 2.6.5     its value is:  30
[ERROR]   In versions  3.0.0-SNAPSHOT  its value is:  5
...
```

### Options

__Record the rationale__ for specifying a field as `@Constant` using the annotation's optional `rationale` parameter:

```java
@Constant(rationale = "Customers have already deployed production code that relies on this key")
public static final String CRITICAL_API_PROPERTY_KEY = "accountID";
```

__Record a change of fully-qualified name (FQN)__ by adding an `@Alteration` record to its history:
```java
    @Constant(history = {@Alteration(version = "2", fromFQN = "previous.java.package.of.CRITICAL_API_PROPERTY_KEY")},
             explanation = "")
public static final String CRITICAL_API_PROPERTY_KEY = "accountID";
```

__Record a change of a `@Constant` field's value__
```java
    @Constant(history = {@Alteration(version = "2", valueChanged = true,
             explanation = "")})
public static final String CRITICAL_API_PROPERTY_KEY = "account-id";
```

__Don't break the build__ by changing the `reportingLevel` configuration parameter from its default of `ERROR` to a `WARNING` instead:

```xml
<configuration>
    <reportingLevel>WARNING</reportingLevel>
</configuration>
```

__Skip execution__ by setting the `skip` configuration property to `true` within the plugin's `configuration`:

```xml
<configuration>
    <skip>true</skip>
</configuration>
```

or by defining the `polypara.skip` system property as an argument to `mvn` at the CLI:

```bash
mvn ... -Dpolypara.skip ...
```



## Contributing

Run the tests and build the deliverables:

```bash
bin/test.sh
```

The annotation source code can be quickly perused; it's brief and instructive. 


## TODO

Document:
- Polypara applies to a single artifact version.
- The only requirement of the build verification step is that there are Java .class files in the build output directory, and at least one other build artifact to compare against. The type of the Maven project is irrelevant. If `target/classes` is missing or there are no jars, the verify goal silently does nothing.
- The impact that using Polypara has on your deliverables is that select classes are annotated with a single new class: the `@Constant` annotation. The annotation is included in the JAR and made available on the class path, and its reference is retained by the annotated class files.
- Expect results after two different versions of your project are in play. You can back-implement `@Constant` by releasing for example `1.3.1-1`.

Do:
- Improve test coverage.
- Bake motivation, principles, and design decisions into the documentation and the code.



© Copyright Vivid Inc.
