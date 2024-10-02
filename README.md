# shade-enforcer

Enforce that jars have a configurable string in their name at shading time.

To implement this we add an additional Filter implementation at the end of the user supplied list.

To use in your project (you would have to build the plugin yourself, it's not in maven central):

```xml
<plugin>
  <groupId>org.apache.maven.plugins</groupId>
  <artifactId>maven-shade-plugin</artifactId>
  <version>3.5.1</version>
  <dependencies>
    <dependency>
      <groupId>com.github.robobario</groupId>
      <artifactId>shade-enforcer</artifactId>
      <version>2.0.0-SNAPSHOT</version>
    </dependency>
  </dependencies>
  <configuration>
    <shaderHint>enforceJarName</shaderHint>
  </configuration>
</plugin>
```

and when running the `mvn` command to build your project add system properties:

- `-DenforceShadedJarNameContains=your_string` (required). enforce that all shaded jar names contain this string
- `-DenforceShadedJarNameFailOnViolation=true` (optional). if set to true, then the build fails immediately on the first violation
