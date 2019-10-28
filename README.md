# Code Visualizer

Code Visualizer is a development support tool to generate sequence diagram and class diagram from java source code.
Sequence diagrams are generated for each public method which belong to specified class.
Class diagrams are generated for each sequence diagram and include classes which appeare in it as parameters and return types.

This (https://sitoolkit.github.io/sit-cv/designdocs/) is generated diagrams from this project.

Diagrams are provided as static web contents so you can see them with web browser.
You can generate diagrams with two ways.

1. Server mode
1. Report mode

## Server mode

Code Visualizer can be run as an application server.
In this mode, you can see diagrams via http://localhost:8080/ and the diagram will be automatically re-generated after source code modification.

## Report mode

Code Visualizer can be run as an batch application.
After execution of report mode, diagrams are generated in project_root/docs/designdocs directory.
These report files can be accessed with browser as local files and also published with web server or GitHub Pages.

## Required Software

* JDK v11
* Graphviz(https://www.graphviz.org/) 
  * For Windows, Graphviz is installed automatically
  * For macOS or Linux, you need to install manually with following commands.

* macOS

```
brew install graphviz
```

* Ubuntu

```
sudo apt update
sudo apt install graphviz
```

## Demo

If you want to know how Code Visualizer works, try to run it for DDD Sample app (https://github.com/citerus/dddsample-core).
You can run Code Visualizer on Windows and macOS using following commands of each OS. Before you try it, install JDK8 for DDD Sample app.

* Windows

```
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
git checkout f01db3d2d8be14233403f363f128645b633d2952
1>.env echo JAVA_HOME=C:\\path\u0020to\\jdk1.8
start http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.5/sit-cv-app-1.0.0-beta.5-exec.jar
move %USERPROFILE%\Downloads\sit-cv-app-1.0.0-beta.5-exec.jar .
java -jar sit-cv-app-1.0.0-beta.5-exec.jar --cv.analyze-sql
```

* macOS, Linux

```
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
git checkout f01db3d2d8be14233403f363f128645b633d2952
echo JAVA_HOME=/path/to/jdk1.8 > .env
curl -o sit-cv-app-1.0.0-beta.5-exec.jar -G http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.5/sit-cv-app-1.0.0-beta.5-exec.jar
java -jar sit-cv-app-1.0.0-beta.5-exec.jar --cv.analyze-sql
```

Exclude methods owned by the repository's parent class (HibernateRepository) from the CRUD matrix, because all repository methods converge to the same CRUD.

Download the configuration file to project root.

- Windows

```
git clone https://github.com/Xenuzever/sit-cv-config-dddsample-core
move sit-cv-config-dddsample-core\sit-cv-config-dddsample-core.json .\sit-cv-config.json
rd /s /q sit-cv-config-dddsample-core
```

- macOS, Linux

```
curl -o sit-cv-config.json -G https://raw.githubusercontent.com/Xenuzever/sit-cv-config-dddsample-core/master/sit-cv-config-dddsample-core.json
```

After running last java command, you can see following log on your console.

```
Started SitCvApplication in 00.000 seconds (JVM running for 00.000)
```

Then you can access http://localhost:8080 with browser and see UML diagrams of DDD Sample app.

* Report Mode

Running java command with --cv.report option, static report files(html, css, js) are generated to docs/designdoc directory.

```
java -jar sit-cv-app-1.0.0-beta.5-exec.jar --cv.report
```

You can see diagrams by opening docs/designdoc/index.html with browser.

* Generate CRUD matrix

Run with the --cv.analyze-sql option to generate a CRUD matrix.
Use this option to automatically run tests and analyze logs to get SQL.

```sh
# Server Mode
java -jar sit-cv-app-1.0.0-beta.5-exec.jar --cv.analyze-sql

# or Report Mode
java -jar sit-cv-app-1.0.0-beta.5-exec.jar --cv.analyze-sql --cv.report
```

## How to Use in Your Project

### Maven Project

If your project uses Maven, add plugin to pom.xml of your project.

```xml
<project>
  ...
  <build>
    <plugins>
        <plugin>
            <groupId>io.sitoolkit.cv</groupId>
            <artifactId>sit-cv-maven-plugin</artifactId>
            <version>1.0.0-beta.5</version>
        </plugin>
    </plugins>
  </build>
  ...
</project>
```

Then you can use following commands in your project directory.

* Server mode

```
mvn sit-cv:run
```

* Report mode

```
mvn sit-cv:report
```

* Generate CRUD matrix

```
# Server Mode
mvn sit-cv:run -Danalyze-sql=true

# or Report Mode
mvn sit-cv:report -Danalyze-sql=true
```

### Gradle Project

If your project uses Gradle, add plugin to build.gradle of your project.

* Using the plugins DSL:

```groovy
plugins {
  id "io.sitoolkit.cv.sit-cv-gradle-plugin" version "1.0.0-beta.5"
}

test {
  testLogging {
    showStandardStreams = true
  }
}
```

* Using legacy plugin application:

```groovy
buildscript {
  repositories {
    maven {
      url "https://plugins.gradle.org/m2/"
    }
  }
  dependencies {
    classpath group: 'io.sitoolkit.cv', name: 'sit-cv-gradle-plugin', version:'1.0.0-beta.5'
  }
}

apply plugin: 'sit-cv-gradle-plugin'
```

Then you can use following commands in your project directory.

* Server mode

```
gradlew cvRun
```

* Report mode

```
gradlew cvReport
```

* Generate CRUD matrix

```
# Server Mode
gradlew cvRun --analyze-sql

# or Report Mode
gradlew cvReport --analyze-sql
```

## Configuration for Your Project

If you want to customize filter condition to draw classes on diagrams, put sit-cv-config.json in your project root directory.
It's JSON structure is as follows.

* sit-cv-config.json
```json
{
  "override": false,
  "lifelines": [
    {
      "name": ".*Controller.*",
      "annotation": ".*Controller",
      "entryPoint": true
    },
    {
      "name": ".*Service.*"
    },
    {
      "name": ".*Repository.*",
      "annotation": ".*(Repository|Named)",
      "dbAccess": true
    },
    {
      "name": ".*Factory.*",
      "lifelineOnly": true
    },
    {
      "name": ".*Specification.*",
      "lifelineOnly": true
    }
  ],
  "asyncAnnotations": [
    "Async",
    "Asynchronous"
  ],
  "sqlLogPattern": {
    "start": ".*Pattern before SQL starts.*",
    "end": ".*Pattern after SQL ends.*"
  }
}
```
| Key                 | Description                                                                        | Default value |
|---------------------|------------------------------------------------------------------------------------|---------------|
| override            | Ignore [default configuration](sit-cv-core/src/main/resources/io/sitoolkit/cv/core/infra/config/sit-cv-config.json). | false         |
| lifelines           | Specify classes to draw as a lifeline in the sequence diagram.                                                       |               |
| &emsp; name         | Pattern to match class qualified name.                                                                               |               |
| &emsp; annotation   | Pattern to match qualified annotation name of class.                                                                 |               |
| &emsp; entryPoint   | Set true to recognize as a entry point, i.e. left end class of sequence diagram.                                     | false         |
| &emsp; lifelineOnly | Set true to hide internal processing, i.e. messages to itself.                                                       | false         |
| &emsp; dbAccess     | Set true to recognize as a repository class. This is used to generate CRUD matrix.                                   | false         |
| &emsp; exclude      | Excludes methods owned by classes matching the pattern from the CRUD matrix.                                         | false         |
| asyncAnnotations    | Annotaion names to recognize as asynchronous method.                                                                 |               |
| sqlLogPattern       | Filter rule to find SQL from test log. This is used to generate CRUD matrix.                                         |               |
| &emsp; start        | Pattern to match the line just before SQL starts.                                                                    |               |
| &emsp; end          | Pattern to match the line just after SQL ends.                                                                       |               |
