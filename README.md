# Code Visualizer

## Quick Start

* Windows

```
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
start http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-appha.2/sit-cv-app-1.0.0-appha.2.jar
move %USERPROFILE%\Downloads\sit-cv-app-1.0.0-appha.2.jar
start java -jar sit-cv-app-1.0.0-appha.2.jar .
start http://localhost:8080
```

* macOS

```
brew install graphviz
git clone https://github.com/citerus/dddsample-core.git
cd dddsample-core
curl -o sit-cv-app-1.0.0-appha.2.jar -G http://repo1.maven.org/maven2/io/sitoolkit/cv/sit-cv-app/1.0.0-appha.2/sit-cv-app-1.0.0-appha.2.jar 
java -jar sit-cv-app-1.0.0-appha.2.jar . &
open http://localhost:8080/
```

* UML diagrams of the application in dddsample-core projedt is displayed.
* Changing Java files in the project automatically refreshes the diagram.

