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

1. File -> Import -> Maven -> Existing maven project
2. Select your code folder
3. Finish

![Import project](https://media.giphy.com/media/l0Iy6gQxboYSaCw3m/giphy.gif)

**1. Install any dependencies**

1. Right click on the root project
2. Select maven -> Update Project

![Install dependencies](https://media.giphy.com/media/3og0IvfO0V3XmQmaEo/giphy.gif)


### Running The App
**0. Compile/Build the application**

Using command line run this command in the root folder
```bash
mvn package
```

After you run this command, the jar file will be available in `target/` folder.

**1. Run the application**

You need to run the client and server in the separate command line/terminal

Available command line arguments for Server

```
 -advertisedhostname <arg>        advertised hostname
 -connectionintervallimit <arg>   connection interval limit in seconds
 -debug                           Print debug information
 -exchangeinterval <arg>          exchange interval in seconds
 -port <arg>                      Server port, an integer
 -secret <arg>                    Secret
```

#### Server
```bash
java -cp target/ezshare-1.0-jar-with-dependencies.jar EZShare.Server <command line arguments>
```

Available command line arguments for Client

```
 -channel <arg>       channel
 -debug               print debug information
 -description <arg>   resource description
 -exchange            exchange server list with server
 -fetch               fetch resources from server
 -host <arg>          server host, a domain name or IP Address
 -name <arg>          resource name
 -owner <arg>         owner
 -port <arg>          server port, an integer
 -publish             publish resource on server
 -query               query for resources from server
 -remove              remove resource from server
 -secret <arg>        secret
 -servers <arg>       server list
 -share               share resource on server
 -tags <arg>          resource tag
 -uri <arg>           resource uri
```


### Client
```bash
java -cp target/ezshare-1.0-jar-with-dependencies.jar EZShare.Client <command line arguments>
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



