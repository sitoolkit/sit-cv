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

## Demo

If you want to know how Code Visualizer works, try to run it for DDD Sample app (https://github.com/citerus/dddsample-core).
JRE v1.8+ and Git are required.
For macOS and Linux, Graphviz(https://www.graphviz.org/) are required.
You can run Code Visualizer on Windows and macOS using following commands of each OS.

* Windows

```
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
start http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.3/sit-cv-app-1.0.0-beta.3-exec.jar
move %USERPROFILE%\Downloads\sit-cv-app-1.0.0-beta.3-exec.jar .
java -jar sit-cv-app-1.0.0-beta.3-exec.jar
```

* macOS

```
brew install graphviz
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
curl -o sit-cv-app-1.0.0-beta.3-exec.jar -G http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.3/sit-cv-app-1.0.0-beta.3-exec.jar
java -jar sit-cv-app-1.0.0-beta.3-exec.jar
```

* Ubuntu

```
sudo apt update
sudo apt install graphviz
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
curl -o sit-cv-app-1.0.0-beta.3-exec.jar -G http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-beta.3/sit-cv-app-1.0.0-beta.3-exec.jar
java -jar sit-cv-app-1.0.0-beta.3-exec.jar
```

After running last java command, you can see following log on your console.

```
Started SitCvApplication in 00.000 seconds (JVM running for 00.000)
```

Then you can access http://localhost:8080 with browser and see UML diagrams of DDD Sample app.

* Report Mode

Running java command with --cv.report option, static report files(html, css, js) are generated to docs/designdoc directory.

```
java -jar sit-cv-app-1.0.0-beta.3-exec.jar --cv.report
```

You can see diagrams by opening docs/designdoc/index.html with browser.

* Generate CRUD matrix

Run with the --cv.analyze-sql option to generate a CRUD matrix.
Use this option to automatically run tests and analyze logs to get SQL.

```sh
# Server Mode
java -jar sit-cv-app-1.0.0-beta.3-exec.jar --cv.analyze-sql

# or Report Mode
java -jar sit-cv-app-1.0.0-beta.3-exec.jar --cv.analyze-sql --cv.report
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
            <version>1.0.0-beta.3</version>
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
mvn sit-cv:run --Danalyze-sql=true

# or Report Mode
mvn sit-cv:report --Danalyze-sql=true
```

### Gradle Project

If your project uses Gradle, add plugin to build.gradle of your project.

* Using the plugins DSL:

```groovy
plugins {
  id "io.sitoolkit.cv.sit-cv-gradle-plugin" version "1.0.0-beta.3"
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
    classpath group: 'io.sitoolkit.cv', name: 'sit-cv-gradle-plugin', version:'1.0.0-beta.3'
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
  "entryPointFilter": {
    "include":[
      {
        "name": ".*Controller"
      },
      {
        "annotation": "*.Controller"
      },
      {
        "name": ".*Controller",
        "annotation": ""
      },
    ]
  },
  "sequenceDiagramFilter": {
    "include": []
  },
  "repositoryFilter": {
    "include": []
  },
  "sqlEnclosureFilter": {
    "start": ".*Pattern before SQL starts.*",
    "end": ".*Pattern after SQL ends.*"
  }
}
```

|          Key          |                                   Description                                    |
| --------------------- | -------------------------------------------------------------------------------- |
| entoryPointFilter     | Filter rule to recognize as entry point i.e. left end class of sequence diagram. |
| include               | Include classes that match one of these rules for processing.                    |
| name                  | Pattern to match class qualified name.                                           |
| annotation            | Pattern to match qualified annotation name of class.                             |
| sequenceDiagramFilter | Filter rule to draw sequence diagram.                                            |
| repositoryFilter      | Filter rule to find repository classes. This is used to generate CRUD matrix.    |
| sqlEnclosureFilter    | Filter rule to find SQL from test log. This is used to generate CRUD matrix.     |
| start                 | Pattern to match the line just before SQL starts.                                |
| end                   | Pattern to match the line just after SQL ends.                                   |