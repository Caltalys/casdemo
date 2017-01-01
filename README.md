## CAS authentication for JHipster generated application

I am testing CAS with JHipster so I have created this repository to keep track what I have done. You can view formatted
pages at https://rohajda.github.io/casdemo/ as well


### Building steps

Here is summary of steps

1. Create JHipster application
    - Add entity CITY and configure sample data
    - Start docker container as postgres development database
    - Run initial application with development profile

#### 1. Create JHipster application

    mkdir casdemo
    cd casdemo
    yo jhipster

- Monolithic application
- PostgreSQL as production and development database
- Maven

##### Add entity CITY and configure sample data

    yo jhipster:entity city

2 attributes:

- name
- country


Modify liquibase script to prepare initial sample data. Create file `src/main/resources/config/liquibase/cities.csv` with
sample data. Create new changeset in `src/main/resources/config/liquibase/changelog/*added_entity_City.xml` to load data.


##### Start docker container as postgres development database

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

##### Run initial application with development profile

Use **IntelliJ** run option or use command line from terminal `./mvnw`

### JHipster configuration

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
