# Cherimoya: Automated verification that Java language constants are throughout a series of releases.

For when you look at a constant field and think to yourself: "The value of this field cannot change, even in successive versions."
Comprised of a Java annotation and a Maven plugin.
Requires JDK 1.7 or higher, and Maven 3.

_Cherimoya in aggregate has not reached a 1.0 release, and doesn't do what it says it does at the moment. It still needs to be vetted in real-world usage before we can finish writing the code and eventually commit ourselves to its final, blessed form of version 1.0._

The JAR comprising the `@Constant` annotation is lightweight, and is labeled as version 1.0. Add a Maven dependency to it as follows:

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

Include Cherimoya's verification in your Maven build by adding the following segment to your Maven `pom.xml`:

```xml
   <build>
       ...
       <plugin>
           <groupId>vivid.cherimoya</groupId>
           <artifactId>cherimoya-maven-plugin</artifactId>
           <version>1.0-SNAPSHOT</version>
           <executions>
               <execution>
                   <goals>
                       <goal>verify</goal>
                   </goals>
               </execution>
           </executions>
       </plugin>
       ...
   </build>
```

and then run a Maven build. Observe Cherimoya's output:

```
...
[INFO] --- cherimoya-maven-plugin:1.0-SNAPSHOT:verify (default) @ trace ---
[WARNING] Only one version (1.4.1) of vivid:trace is available for inter-version comparison; skipping execution
...
```

## Links

- [Motivating question on StackOverflow](https://stackoverflow.com/questions/41393794/good-practices-for-breaking-maven-build-when-specific-class-members-change-val)

## TODO

- Document instructions for incorporating (including fetching the JARs of) and using both the annotation JAR and the Maven plugin. Specify when the annotation should be used.
- The project is suitable for a single artifact version. When the next version of the product in which Cherimoya is readying for release, write code to identify all available versions of the artifact in Maven's local repositories, scan those as well, and then verify constancy. Create tests and usage documentation.
- Bake motivation, principles, and design decisions into the documentation and the code.

- Ensure that all desired versions are available in your local Maven repo.
- Ignores the absolute value of and changes to field visibility modifiers (public, package, protected, private).
- The only requirement is that there are Java .class files in the build output directory, and at least one other build artifact to compare against. The type of the Maven project is irrelevant.
- The impact of using Cherimoya in your project is that a single new class, the {@Constant} annotation, is added to your annotated classes and the annotation is retained in class files.
- Skip execution by setting the ``skip'' configuration parameter to ``true''.
