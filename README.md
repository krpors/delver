# delver
Java agent to inspect and count method calls.

This project is a sort of proof-of-concept to count how many times methods have
been called through a Java program. It comes with a blacklist and whitelist 
functionality to exclude or include certain packages explicitly. 

# Motivation

I created this to test out the Java Instrumentation API. My use case was mainly
the investigation of finding dead code throughout legacy code my team inherited.
Perhaps there are better ways (static analysis) but our project uses weird 
combinations of Struts 1.0, tag libraries, and finding methods through reflection
and what not. Many calls could not be checked using a static analysis.

# Running

Build it with `mvn clean install`. It will produce a shaded 'uber JAR' which 
contains the agent code, plus the [javassist](http://www.javassist.org) code 
dependency. You can put this JAR as the Java agent in your program as follows:

    java -javaagent:PATH_TO_SHADED_JAR [...]
    
The agent requires an XML configuration file to read the black- and whitelist
from. The file takes the following format:

```xml
<!-- Delver example XML config. -->
<delver verbose="true">
    <include>
        <pattern>nl/omgwtfbbq/delver/conf</pattern>
        <pattern>nl/omgwtfbbq/delver/conf/cruft/*$</pattern>
        <pattern>nl/omgwtfbbq/delver/other</pattern>
        <pattern>[z</pattern> <!-- this one should fail compiling. -->
        <pattern>org.example.*.anything.*</pattern>
    </include>
    <exclude>
        <pattern>nl/skipthis</pattern>
        <pattern>nl/andthis/skip/too</pattern>
    </exclude>
</delver>
```

The configuration file will include and exclude certain packages which are match a regex.
Note that if you want to include a package for monitoring, you **MUST** provide an explicit
inclusion. Also, exclusions take precedence over inclusions, so if an exclusion is evaluated,
an inclusion matching the same regex will not have any effect.

If you try monitoring a JVM with for example VisualVM, and the message

> 'Data not available because JMX connection to the JMX agent could not be established'

is shown, try running the JVM with the `-Dcom.sun.management.jmxremote` parameter.
