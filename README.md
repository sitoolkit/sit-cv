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

Running java command with --report option, static report files(html, css, js) are generated to docs/designdoc directory.

```
java -jar sit-cv-app-1.0.0-beta.3-exec.jar --cv.report
```

You can see diagrams by opening docs/designdoc/index.html with browser.


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


### Gradle Project

If your project uses Gradle, add plugin to build.gradle of your project.

* Using the plugins DSL:

```groovy
plugins {
  id "io.sitoolkit.cv.sit-cv-gradle-plugin" version "1.0.0-beta.3"
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

## Generate CRUD Matrix

If your project meets the following conditions, you can generate a CRUD matrix based on the log output by test.

- Is a maven project
- Repository class is annotated @org.springframework.stereotype.Repository
- Using the Hibernate ORM

The first time you generate a CRUD matrix, run the test automatically to analyze the log by doing the following at startup:

```sh
# Specify cv.analyze-sql option in java command
java -jar sit-cv-app-1.0.0-beta.3-exec.jar --cv.analyze-sql

# or Execute analyze-sql goal of maven plugin
mvn sit-cv:analyze-sql sit-cv:run
```

## Configuration for Your Project

If you want to customize filter condition to draw classes on diagrams, put sit-cv-config.json in your project root directory.
It's JSON structure is as folows.

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
    ],
    "exclude":[]
  },
  "sequenceDiagramFilter": {
    "include": [],
    "exclude": []
  }
}
```

|          Key          |                                   Description                                    |
| --------------------- | -------------------------------------------------------------------------------- |
| entoryPointFilter     | Filter rule to recognize as entry point i.e. left end class of sequence diagram. |
| include               | Classes which matches one of these rules are included to sequence diagram.       |
| exclude               | Classes which matches one of these rules are excluded to sequence diagram.       |
| name                  | Pattern to match class qualified name.                                           |
| annotation            | Pattern to match qualified annotation name of class.                             |
| sequenceDiagramFilter | Filter rule to draw sequence diagram.                                            |