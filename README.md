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

# Building
Build it with `mvn clean install`. It will produce a shaded 'uber JAR' which
contains the agent code, plus the [javassist](http://www.javassist.org) code
dependency.

# Running

 You can put the created JAR as the Java agent in your program as follows:

    java -javaagent:<path to shaded jar>=<path to config file> [...]

where `<path to shaded jar>` is... the path to the Delver jar file and `<path to config file>`
is the path to the configuration file. For example:

    java -javaagent:/tmp/delver-1.0-snapshot.jar=/tmp/delver-config.xml -jar myapp.jar

If the configuration file cannot be found, it will report an error to stderr, and no
instrumentation will be done. Same applies if the file is found, but cannot be read
for whatever reason.

# Configuration file

The agent requires an XML configuration file to read the black- and whitelist
from. The file takes the following format:

```xml
<delver>
    <!-- to start a HTTP server: -->
    <http>
        <enabled>true</enabled>
        <port>8081</port>
    </http>
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

The configuration file will include and exclude certain packages which match a regex.
Note that if you want to include a package for monitoring, you **MUST** provide an explicit
inclusion. Also, exclusions take precedence over inclusions, so if an exclusion is evaluated,
an inclusion matching the same regex will not have any effect.

# Gathering results via MBeans

The Instrumentation agent will expose functionality through the MBeanServer of the JVM
its running in. The object name is `nl.omgwtfbbq.delver:type=MethodUsageSampler`. Currently
the MXBean exposes the following functions:

1. `Map<String, Integer> getCallMap()`: containing the method signature, plus the amount of calls to that method.
1. `void writeToFile(String file)`: Attempts to write the statistics in CSV form to the specified
filename. Will throw an MBeanException if that failed. Currently, this will overwrite any existing file which you
have the ownership/rights to, so **BE CAREFUL**.
1. `int getMethodCount()`: gets the amount of methods which have been found for instrumentation (i.e. map size).
1. `int getTotalMethodUsageCount()`: the amount of calls of every method, cumulatively.

You can use `JConsole` or `VisualVM` to make a connection to an MBeanServer to view the stats.
If you try monitoring a JVM with for example VisualVM, and the message

> Data not available because JMX connection to the JMX agent could not be established

is shown, try running the JVM with the `-Dcom.sun.management.jmxremote` parameter.

The `writeToFile` function will generate a semicolon separated file which has the following format:

    2;public;java.lang.String;nl.rivium.breakdown.core.jms.JMSConnection;getPassword()
    2;public;void;nl.rivium.breakdown.core.jms.JMSConnection;setQueueConnectionFactory(java.lang.String)
    4;public;void;nl.rivium.breakdown.ui.tab.TestSuiteTab;focusLost(org.eclipse.swt.events.FocusEvent)
    0;public;void;nl.rivium.breakdown.core.jms.JMSConnection;removeFromParent()

Columns:

1. Amount of calls to this method.
1. Modifiers (public, private, static, final, synchronized, etc)
1. Return type.
1. Fully qualified class name, dot separated.
1. Method name + signature.

The contents can be easily parsed using any other programs, since it's (semicolon) separated, e.g.

    sort -rn file.csv | column -t -s ";"

which will output something like:

    468  public         void                                            nl.rivium.breakdown.ui.ProjectTree$2          handleEvent(org.eclipse.swt.widgets.Event)
    20   public static  org.eclipse.swt.graphics.Image                  nl.rivium.breakdown.ui.ImageCache             getImage(nl.rivium.breakdown.ui.ImageCache$Icon)
    19   public static  nl.rivium.breakdown.core.jms.DestinationType[]  nl.rivium.breakdown.core.jms.DestinationType  values()

# Gathering results via HTTP

If an HTTP server is started via the configuration file, the results can be viewed
using any HTTP client by simply browsing to `http://thehost:${port}`. The results displayed are
in the following format:

    Call count;Max (ms);Average (ms);Total (ms);Modifiers;Returntype;Classname;Methodname
    2;0;0;0;protected;void;nl.omgwtfbbq.delver.TestRunner$TheClass;abstractMethod()
    2;0;0;0;public;void;nl.omgwtfbbq.delver.TestRunner$BaseClass;concreteMethodInBaseClass()
    1;0;0;0;public;void;nl.omgwtfbbq.delver.TestRunner$TheClass;concreteMethod()
    0;0;0;0;public;java.util.Map;nl.omgwtfbbq.delver.mbeans.MethodUsageSampler;getCallMap()
    0;0;0;0;public;void;nl.omgwtfbbq.delver.http.TotalsHttpHandler;handle(com.sun.net.httpserver.HttpExchange)
    0;0;0;0;public;void;nl.omgwtfbbq.delver.http.DelverHttpHandler;handle(com.sun.net.httpserver.HttpExchange)
    0;0;0;0;public;int;nl.omgwtfbbq.delver.mbeans.MethodUsageSampler;getMethodCount()
    0;0;0;0;public static;void;nl.omgwtfbbq.delver.TestRunner;main(java.lang.String[])
    0;0;0;0;public;void;nl.omgwtfbbq.delver.mbeans.MethodUsageSampler;writeToFile(java.lang.String)
    0;0;0;0;public;void;nl.omgwtfbbq.delver.TestRunner;run()
    0;0;0;0;public;int;nl.omgwtfbbq.delver.mbeans.MethodUsageSampler;getTotalMethodUsageCount()

Using `awk` you can be a bit flexible in the desired output:

    curl http://localhost:8081 | gawk -F";" '{ print $1 ";" $7 ";" $8 }' | column -s ";" -t

Output:

    Average (ms)  Classname                                      Methodname
    2             nl.omgwtfbbq.delver.http.DelverHttpHandler     handle(com.sun.net.httpserver.HttpExchange)
    0             nl.omgwtfbbq.delver.TestRunner$TheClass        abstractMethod()
    0             nl.omgwtfbbq.delver.TestRunner$BaseClass       concreteMethodInBaseClass()
    0             nl.omgwtfbbq.delver.TestRunner$TheClass        concreteMethod()
    0             nl.omgwtfbbq.delver.mbeans.MethodUsageSampler  getCallMap()
    0             nl.omgwtfbbq.delver.http.TotalsHttpHandler     handle(com.sun.net.httpserver.HttpExchange)
    0             nl.omgwtfbbq.delver.mbeans.MethodUsageSampler  getMethodCount()
    0             nl.omgwtfbbq.delver.TestRunner                 main(java.lang.String[])
    0             nl.omgwtfbbq.delver.mbeans.MethodUsageSampler  writeToFile(java.lang.String)
    0             nl.omgwtfbbq.delver.TestRunner                 run()
    0             nl.omgwtfbbq.delver.mbeans.MethodUsageSampler  getTotalMethodUsageCount()
