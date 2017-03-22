COMP90015 EZShare Project
===========================
A resource sharing network that consists of servers, which can communicate with each other, and clients which can communicate with the servers.

#### Team:
* Juan Daniel Daza
* Haoran Sun
* Martin Valentino
* Sharon T

Getting Started
---------------
Make sure you have this librarys/software installed in your machine:
* [Java 1.8](http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html)
* [Eclipse IDE](https://eclipse.org/downloads/packages/eclipse-ide-java-developers/neon2)
* [Maven](https://maven.apache.org/install.html)
    * If you are using OSX you can just run below command from the terminal (assume you have `brew`)
    ```
    brew install maven
    ```

### Steps
**0. Import the project to eclipse**

![Import project](https://media.giphy.com/media/l0Iy6gQxboYSaCw3m/giphy.gif)

**1. Install any dependencies**

![Install dependencies](https://media.giphy.com/media/3og0IvfO0V3XmQmaEo/giphy.gif)

### Running The App
**0. Compile/Build the application**

Using command line run this command in the root folder
```bash
mvn package
```

**1. Run the application**

You need to run the client and server in the separate command line/terminal
#### Server
```bash
java -cp target/ezshare-1.0-jar-with-dependencies.jar com.ezshare.Server
```

### Client
```bash
java -cp target/ezshare-1.0-jar-with-dependencies.jar com.ezshare.Client
```

Code Structure
---------------
The code will be split into Server and Client. All of code will be under `src/main/java` folder.

    ├── src/main/java              
    ├──── com.ezshare               # entry point for the application
    ├──────── Client.java           # Main Client class
    ├──────── Server.java           # Main Server class
    ├──── com.ezshare.client        # Package for client code
    ├──── com.ezshare.server        # Package for server code
    ├── target                      # compilation result
    └── start-client.js     # script to running front-end code process



