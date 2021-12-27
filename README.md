[[_TOC_]]

# Overview

This is a SpringBoot gradle RestApi project written in java 11 and contains the basic modules and services for
small to medium production apis

# Purpose

This template is a foundation for a SpringBoot Rest api and will meet
Platform 1 CtF criteria

# Features

## Currently integrated

- Mysql 8 db
- flyway db migration mgmt
- ANTLR for supporting search in endpoints ex `/api/users?search=id:1`
- a user slice with a controller, service, dao, and DTOs
- Custom Exceptions and ExceptionHandling
- junit5 tests for the slice
- custom validation
- swagger api via SpringFox
- keycloak identity integration
- secure configuration for web security
- custom method security for securing endpoints
- Gitlab4j 
- websockets

## Awaiting integration

- CDS (Cross Domain Solution)

# Setting Up IntelliJ
- `⌘` + `,` select Editor | Code Style | Java
- Click the cog
- Import MidasCodeStyle-IntelliJ.xml from localRun folder
- select Editor | General | Auto-Import
- Check `Optimize imports on the fly` 

# Running the API

1. Have [Docker](https://docs.docker.com/get-docker/) installed  
1. Have minimum of [Java 11](https://adoptopenjdk.net/) (Recommend [SDKMan](https://sdkman.io/) for Java Ver Manager)
1. Navigate to the `localRun` folder
1. Follow steps in `.example_envrc`
1. Execute `docker-compose up -d`.
1. Execute `./run.sh`.

# Understanding Search with ANTLR

## Overview

Using Antlr with Spring Data features allows us to send dynamic queries via rest request.
This requires the use of some advanced features but is well worth the effort as it can
speed response time and reduce total lines of code.

## Core concepts

### [ANTLR](https://www.antlr.org/)

Another Tool for Language Recognition is a powerful tool for lexing and parsing.
New languages can be created using ANTLR which is what we are going to use it for.
We'll create a language that can be passed via http request and parsed into tokens that
can be used to generate custom queries to the DB.

```java
String url = "https://my/api/users?search=displayname:JDavis AND callsign:Smack"
// Spring resolver config will see ?search and see it as a param and pass the
// param value in this case displayname:JDavis, and the type User to the ANTLR query visitor
// (we'll get to the visitor later) which will turn the following tokens
"(input " +
    "(query " +
        "(query (criteria (key displayname) (op :) (value "JDavis")))" +
        " AND " +
        "(query (criteria (key callsign) (op :) (value Smack)))) <EOF>)";

// A series of operations turn this into a Specification that will be run like so
List<User> users = repository.findAll(specification)
// repo runs the following query
// SELECT * FROM users WHERE displayname="JDavis" AND callsign="Smack")
```

The big benefits are we can reduce the number of endpoints and custom queries needed to support
a frontend or some other client. In addition to reducing endpoints, it also customizes the request
to the exact data needed reducing the response size, which is helpful in slower networks especially
when combined with pagination.

Limitations. While ANTLR with Spring Data does give some GraphQl like behavior it is limited to returning data of an
entity type specific to the endpoint. Whereas in GraphQl one endpoint servers and filters all data.

### SearchSpecResolver

A custom resolver in the form of an annotation needs registered with the Spring ResolverConfiguration.
This is the mechanism that sees `?search=displayname:foo` and passes the value `display:name`
as a string to the query parser.

### [JPA Criteria API](https://dev.to/igagrock/programmatic-criteria-queries-using-jpa-criteria-api-1h65)

Spring will take care of this work for us, however it is helpful to understand as this what is lies
under the hood of Spring Data Specifications.

### [Spring Data JPA Specifications](https://spring.io/blog/2011/04/26/advanced-spring-data-jpa-specifications-and-querydsl/)

ANTLR will build our Specifications which will be consumed by the JpaRepository interface when extended
with JpaSpecificationExecutor<Entity Type>. The executor will build the specification into a query
when passed to methods such as `repository.findall(specifications)`

# [Gitlab4J](https://github.com/gitlab4j/gitlab4j-api)

GitLab4J™ API (gitlab4j-api) provides a full featured and easy to consume Java library for working with GitLab 
repositories via the GitLab REST API. Additionally, full support for working with GitLab webhooks and system hooks 
is also provided.

# Security Considerations

- Midas is configured to use PlatformOne SSO, Keycloak, and JWT.  P1 provides an Istio Envoy sidecar that provides AuthN and logging.
- Other considerations.  midas-api uses an AttributeEncryptor for storing and retrieving encrypted strings from the DB.  Midas encrypts with NIST approved AES 256 GCM with random Initialization Vector.  
A `key` and a `salt` must be provided in the container environment.  The key and salt provide a seed for the java key generator.  

# Gradle commands
* ```bash
  ./gradlew checkstyleMain # Checkstyle analysis on src/main
  ./gradlew checkstyleTest # Checkstyle analysis on src/test
  ./gradlew jacocoTestReport # Generates a JACOCO test report
  ./gradlew lintGradle # Wraps checkstyle main and test to lint your code
  ```


# Reference Documentation

For further reference, please consider the following sections:

- [Official Gradle documentation](https://docs.gradle.org)
- [Spring Boot Gradle Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.6.2/gradle-plugin/reference/html/)
- [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.6.2/gradle-plugin/reference/html/#build-image)
- [Spring Boot DevTools](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#using-boot-devtools)
- [Spring Configuration Processor](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#configuration-metadata-annotation-processor)
- [Rest Repositories](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#howto-use-exposing-spring-data-repositories-rest-endpoint)
- [Spring Security](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#boot-features-security)
- [Spring Data JPA](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#boot-features-jpa-and-spring-data)
- [Flyway Migration](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#howto-execute-flyway-database-migrations-on-startup)
- [Flyway Documentation](https://flywaydb.org/documentation/)
- [WebSocket](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#boot-features-websockets)
- [Spring Boot Actuator](https://docs.spring.io/spring-boot/docs/2.6.2/reference/htmlsingle/#production-ready)
- [Gitlab4J](https://github.com/gitlab4j/gitlab4j-api)

### Guides

The following guides illustrate how to use some features concretely:

- [Accessing JPA Data with REST](https://spring.io/guides/gs/accessing-data-rest/)
- [Accessing Neo4j Data with REST](https://spring.io/guides/gs/accessing-neo4j-data-rest/)
- [Accessing MongoDB Data with REST](https://spring.io/guides/gs/accessing-mongodb-data-rest/)
- [Securing a Web Application](https://spring.io/guides/gs/securing-web/)
- [Spring Boot and OAuth2](https://spring.io/guides/tutorials/spring-boot-oauth2/)
- [Authenticating a User with LDAP](https://spring.io/guides/gs/authenticating-ldap/)
- [Accessing Data with JPA](https://spring.io/guides/gs/accessing-data-jpa/)
- [Accessing data with MySQL](https://spring.io/guides/gs/accessing-data-mysql/)
- [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)
- [Building a RESTful Web Service with Spring Boot Actuator](https://spring.io/guides/gs/actuator-service/)

### Additional Links

These additional references should also help you:

- [Gradle Build Scans – insights for your project's build](https://scans.gradle.com#gradle)

### Code Coverage

To access the code coverage report go to:

build -> jacoco -> test -> html -> index.html