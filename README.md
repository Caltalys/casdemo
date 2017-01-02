# CAS authentication for JHipster generated application

I am testing CAS with JHipster so I have created this repository to keep track what I have done. You can view formatted
pages at https://rohajda.github.io/casdemo/ as well


## Building steps

Here is summary of steps

1. Create JHipster application
    - Add entity CITY and configure sample data
    - Start docker container as postgres development database
    - Run initial application with development profile
2. CASifying application
    - Enable SSL for JHipster application
    - Adjust JHipster to use CAS for authentication
    - Configure `cas.properties` and start CAS docker container
    - 


### 1. Create JHipster application

    mkdir casdemo
    cd casdemo
    yo jhipster

- Monolithic application
- PostgreSQL as production and development database
- Maven

#### Add entity CITY and configure sample data

    yo jhipster:entity city

2 attributes:

- name
- country


Modify liquibase script to prepare initial sample data. Create file `src/main/resources/config/liquibase/cities.csv` with
sample data. Create new changeset in `src/main/resources/config/liquibase/changelog/*added_entity_City.xml` to load data.


#### Start docker container as postgres development database

As I ma using few docker postgres containers I am running this one with port 5732

    docker run --name pgdev -p 5732:5432 -e POSTGRES_USER=casdemo postgres:9.5.4

in order to work with application there is need to change `src/main/resources/config/application-dev.yml`

from:

    datasource:
        type: com.zaxxer.hikari.HikariDataSource
        url: jdbc:postgresql://localhost:5432/casdemo
        name:
        username: casdemo
        password:

to:

    datasource:
        type: com.zaxxer.hikari.HikariDataSource
        url: jdbc:postgresql://localhost:5732/casdemo
        name:
        username: casdemo
        password: casdemo

#### Run initial application with development profile

Use **IntelliJ** run option or use command line from terminal `./mvnw`

### 2. CASifying application

Here we modify application from step 1. to use CAS for SSO. This part I have done based on various examples from Github.
For development purpose we will use docker container for CAS server and modify JHipster to use it.

#### Enable SSL for JHipster application

    keytool -genkey -noprompt\
     -alias localhost\
     -keyalg RSA\
     -validity 999\
     -dname "CN=localhost, OU=Test, O=Test, L=Test, S=Test, C=SK"\
     -keystore src/main/docker/keystore\
     -storepass changeit\
     -keypass changeit

- change port in config file. I will set it to 88155. In addition I will set context path to **demo** for JHipster
  application.

Modify server section in `src/main/resources/config/application-dev.yml`

From:

    server:
        port: 8080

To:

    server:
        contextPath: /demo
        port: 8815
        ssl:
            key-store: src/main/docker/keystore
            key-alias: localhost
            key-password: changeit
            key-store-password: changeit
            enabled: true

#### Adjust JHipster to use CAS for authentication

Register new configration properties for CAS integration and create CasProperties class to load them

    cas:
        service:
            security: APP_SERVER_URL/login/cas
            home:  APP_SERVER_URL
        url:
            prefix:  CAS_SERVER_URL
            login:   CAS_SERVER_URL/login
            logout:  CAS_SERVER_URL/logout


Add dependency into `pom.xml` required for cas integration

    <dependency>
        <groupId>org.springframework.security</groupId>
        <artifactId>spring-security-cas</artifactId>
    </dependency>

Add `cas` package under `security` package with following classes that will take care of CAS integration (thanks to
[Julien Gribonvald](https://github.com/jgribonvald) examples)

- CustomSessionFixationProtectionStrategy
- CustomSingleSignOutFilter
- CustomSingleSignOutHandler
- RememberCasAuthenticationEntryPoint
- RememberCasAuthenticationProvider
- RememberWebAuthenticationDetails
- RememberWebAuthenticationDetailsSource

add to `web` package

- SimpleController

Modify existing classes

- AjaxAuthenticationSuccessHandler
- SecurityConfiguration
- UserDetailsService

Modify java scripts sources to use `app/login?postMessage` for login to application

- login.controller.js
- login.service.js
- auth.service.js
- auth.session.service.js

#### Configure `cas.properties` and start CAS docker container

For demo purpose CAS server will be modified to use JHipster database as user repository so we can keep using JHipster for
user management and CAS for authentication. As I want to use database authentication I need to change `pom.xml` in overlay
project and build it. As it take time I am creating `src/main/docker-cas` where I will prebuild image for this demo.

Build docker image locally for cas (as of 5.0.1 there is still bug using BCrypt and database authentication so fix is done
localy)

    cd src/main/docker-cas
    docker build -t rohajda/cas:v5.0.1 .
    cd ../../..

Create thekeystore for CAS

    keytool -genkey -noprompt\
        -alias cas\
        -keyalg RSA\
        -validity 999\
        -dname "CN=cas.ohajda.com, OU=Test, O=Test, L=Test, S=Test, C=SK"\
        -keystore src/main/docker/thekeystore \
        -storepass changeit\
        -keypass changeit

Register certificate as trusted in java

    keytool -export -alias cas -storepass changeit -file src/main/docker/cas.cer -keystore  src/main/docker/thekeystore
    keytool -import -keystore $JAVA_HOME/jre/lib/security/cacerts -file src/main/docker/cas.cer 

Add alias for localhost to hosts file



Run development CAS server

    docker run --name cas -p 8443:8443 --link pgdev:pgdev\
     -h cas.ohajda.com\
     -v $(pwd)/src/main/docker/thekeystore:/etc/cas/thekeystore \
     -v $(pwd)/src/main/docker/dev/cas/config/cas.properties:/cas-overlay/etc/cas/config/cas.properties\
     -v $(pwd)/src/main/docker/dev/cas/config/log4j2.xml:/cas-overlay/etc/cas/config/log4j2.xml\
     -v $(pwd)/src/main/docker/dev/cas/services:/etc/cas/services\
     rohajda/cas:v5.0.1

Run production setup

 - build first

    mvn clean 
    ./mvnw package -Pprod docker:build
    
 
 - start docker compose

    docker-compose -f src/main/docker/app.yml up

 - connect to application as **admin/admin**

    https://casdemo.ohajda.com:8815/demo/


## JHipster configuration

Before starting using JHipster follow these steps recommended by JHipster

Before you can build this project, you must install and configure the following dependencies on your machine: 1.
[Node.js][]: We use Node to run a development web server and build the project. Depending on your system, you can install
Node either from source or as a pre-packaged bundle.

After installing Node, you should be able to run the following command to install development tools (like [Bower][] and
[BrowserSync][]). You will only need to run this command when dependencies change in package.json.

     npm install

We use [Gulp][] as our build system. Install the Gulp command-line tool globally with:

     npm install -g gulp-cli

Run the following commands in two separate terminals to create a blissful development experience where your browser
auto-refreshes when files change on your hard drive.

    ./mvnw
    gulp

Bower is used to manage CSS and JavaScript dependencies used in this application. You can upgrade dependencies by
specifying a newer version in `bower.json`. You can also run `bower update` and `bower install` to manage dependencies.
Add the `-h` flag on any command to see how you can use it. For example, `bower update -h`.

For further instructions on how to develop with JHipster, have a look at [Using JHipster in development][].
