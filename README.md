# Vivid Cherimoya

[![License](https://img.shields.io/badge/license-Apache%202-blue.svg?style=flat-square)](LICENSE.txt)
[![Current version](https://img.shields.io/badge/JCenter-v1.0-239922.svg?style=flat-square)](https://bintray.com/vivid/vivid/vivid%3Acherimoya)

Automated verification of Java field value constancy across release versions.


For when you look at a constant field in Java and think to yourself: "The value of this field *must not change*, even in successive versions."
Appropriate for values that are exposed to and relied upon by software outside of your realm of concern or with whom you have a standing promise to keep keywords stable, such as API clients and database values.

Cherimoya is comprised of a feather-weight Java annotation and a Maven plugin that breaks the build in cases of violations.
Developed, tested, and relied upon with Java JDK version 1.8 and Apache Maven 3.3.

**Note:** _Cherimoya in aggregate has not reached a 1.0 release. It still needs to be vetted in wide-spread usage before we can commit ourselves to its final, blessed form of version 1.0._


## Usage

The distributable JAR comprising the `@Constant` annotation is lightweight, and is labeled as version 1.0. Add a dependency to Cherimoya in your Maven POM:

```xml
<dependency>
  <groupId>vivid.cherimoya</groupId>
  <artifactId>cherimoya</artifactId>
  <version>1.0</version>
  <scope>compile</scope>
</dependency>
```

In your Java code, go through your source code and annotate the appropriate fields with the `@Constant` annotation:

```java
@Constant
static final String DEFAULT_FLOW_CONTROL_MAX_WINDOW_BYTES = 10 * 1024;
```

Cherimoya depends on the field value's `equals` method for determining constancy between versions.

Include Cherimoya's verification step in your Maven build by adding the following segment to your Maven `pom.xml`:
The following example specifies 3 versions: `1.0`, `1.1`, and `1.2`, in that order. The current project's version is appended to the end.
The order of appearance is important, at least for the production of sensible error messages.

Adding the project's current version (from its GAV) into the listing is optional.
Adding the current version can be used to set the location of the version within the sequence of versions.

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
                       <version>1.0</version>
                       <version>1.1</version>
                       <version>1.2</version>
                   </versions>
               </configuration>
           </execution>
       </executions>
   </plugin>
   ...
</build>
```

and then run a Maven build. Observe Cherimoya's output:

```bash
$ mvn install
...
[INFO] --- cherimoya-maven-plugin:1.0:verify (default) @ spyra-levorg ---
[INFO] Verifying constancy of @Constant field values in 4 versions of com.spyra:levorg  1.4.2  1.4.3  1.4.4  2020.1-SNAPSHOT
[INFO]
[ERROR] @Constant field value violation:  com.spyra.levorg.internal.db.payment_request_timeout
[ERROR]   In versions  0.2 ~ 2.6.5     its value is:  30
[ERROR]   In versions  3.0.0-SNAPSHOT  its value is:  5
...
```


__Skip execution__ by setting the `cherimoya.constant.skip` property to `true` within plugin's `configuration`:
```xml
<configuration>
    <cherimoya.constant.skip>true</cherimoya.constant.skip>
</configuration>
```


## Hacking

Run Maven to run the tests and build the deliverables:
```bash
mvn clean package
```


## Links

- [Motivating question on StackOverflow](https://stackoverflow.com/questions/41393794/good-practices-for-breaking-maven-build-when-specific-class-members-change-val)


## TODO

- Document instructions for incorporating (including fetching the JARs of) and using both the annotation JAR and the Maven plugin. Specify when the annotation should be used. Ensure that the JCenter Maven repository is included in your Maven configuration.
- Cherimoya applies to a single artifact version. When the next version of the product in which Cherimoya is readying for release, write code to identify all available versions of the artifact in Maven's local repositories, scan those as well, and then verify constancy. Create tests and usage documentation.
- Bake motivation, principles, and design decisions into the documentation and the code.
- Ensure that Maven can access all indicated versions
- Ignores the absolute value of and changes to field visibility and other modifiers modifiers (public, package, protected, private, static, final, etc.). Optionally check these.
- The only requirement of the build verification step is that there are Java .class files in the build output directory, and at least one other build artifact to compare against. The type of the Maven project is irrelevant.
- The impact that using Cherimoya has on your deliverables is that select classes are annotated with a single new class: the `@Constant` annotation. The annotation is included in the JAR and made available on the class path, and its reference is retained by the annotated class files.
- Publish to JCenter. Maven Central won't accept our Maven G:A because we don't control the "vivid" TLD.
- Set up a build on CI, integrate with SonarQube.
- Expect results after two different versions are in play. You can back-implement `@Constant` by releasing for example `1.3.1-1`.
- If `target/classes` is missing or there are no jars, the verify goal silently does nothing.


## License

© Copyright Vivid Inc.
[Apache 2](LICENSE.txt) licensed.
