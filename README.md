# Cherimoya: Automated verification of continuity of Java language constants across release versions

For when you look at a constant field in Java and think to yourself: "The value of this field *must not change*, even in successive versions."
Appropriate for values that are exposed to and relied upon by software outside of your realm of concern or with whom you have a standing promise to keep keywords stable, such as API clients and database values.
Comprised of a feather-weight Java annotation and a Maven plugin that breaks the build in cases of violations.
Developed, tested, and relied upon with Java JDK versions 1.7 and 1.8, and Apache Maven 3.

**Note:** _Cherimoya in aggregate has not reached a 1.0 release, and doesn't do what it says it does at the moment. It still needs to be vetted in real-world usage before we can finish writing the code and eventually commit ourselves to its final, blessed form of version 1.0._

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

Include Cherimoya's verification step in your Maven build by adding the following segment to your Maven `pom.xml`:
The following example specifies 3 versions: `1.0`, `1.1`, and `1.2`. The current project's version is merged into this list.

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
[INFO] --- cherimoya-maven-plugin:1.0:verify (default) @ my-project ---
...
```


__Skip execution__ by setting the `cherimoya.constant.skip` property to `true` within plugin's `configuration`:
```xml
...
<configuration>
    <cherimoya.constant.skip>true</cherimoya.constant.skip>
</configuration>
```


## Links

- [Motivating question on StackOverflow](https://stackoverflow.com/questions/41393794/good-practices-for-breaking-maven-build-when-specific-class-members-change-val)

## TODO

- Document instructions for incorporating (including fetching the JARs of) and using both the annotation JAR and the Maven plugin. Specify when the annotation should be used.
- The project is suitable for a single artifact version. When the next version of the product in which Cherimoya is readying for release, write code to identify all available versions of the artifact in Maven's local repositories, scan those as well, and then verify constancy. Create tests and usage documentation.
- Bake motivation, principles, and design decisions into the documentation and the code.
- Ensure that Maven can access all indicated versions
- Ignores the absolute value of and changes to field visibility modifiers (public, package, protected, private).
- The only requirement of the build verification step is that there are Java .class files in the build output directory, and at least one other build artifact to compare against. The type of the Maven project is irrelevant.
- The impact that using Cherimoya has on your deliverables is that select classes are annotated with a single new class: the `@Constant` annotation. The annotation is included in the JAR and made available on the class path, and its reference is retained by the annotated class files.
- Publish to somewhere (Maven Central won't accept our GAV because we don't control the "vivid" TLD)
- Set up a build on Travis CI
- Integrate with SonarQube
- Indicate which Maven repositories it can be downloaded from.
- Expect results after two different versions are in play. You can back-implement `@Constant` by releasing for example `1.3.1-1`.
