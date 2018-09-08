# Code Visualizer

## Quick Start

* Windows

```
git clone https://github.com/sitoolkit/sit-cv.git
cd sit-cv
mvnw package
java -jar sit-cv-app/target/sit-cv-app-0.0.1-SNAPSHOT.jar sample
start http://localhost:8080/
```

* macOS

```
brew install graphviz
git clone https://github.com/sitoolkit/sit-cv.git
cd sit-cv
./mvnw package
java -jar sit-cv-app/target/sit-cv-app-0.0.1-SNAPSHOT.jar sample
open http://localhost:8080/
```

* UML diagrams of the application in sit-cv/sample projedt is displayed.
* Changing Java files in the project automatically refreshes the diagram.

