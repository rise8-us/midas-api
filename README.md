[[_TOC_]]

# Overview

This is a SpringBoot gradle RestApi project written in java 11 and contains the basic modules and services for
small to medium production apis

# Purpose

This template is a foundation for a SpringBoot Rest api and will meet
Platform 1 CtF criteria

# Features

## Currently integrated
These are tools/features that are currently in use within the API.  
*It is not necessary to download/install anything for these features to operate.*

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

# Setting Up IntelliJ
- `⌘` + `,` select Editor | Code Style | Java
- Click the cog
- Import MidasCodeStyle-IntelliJ.xml from localRun folder
- select Editor | General | Auto-Import
- Check `Optimize imports on the fly`

# Running the API

1. Have [Docker](https://docs.docker.com/get-docker/) installed
2. Have minimum of [Java 11](https://adoptopenjdk.net/) (Recommend [SDKMan](https://sdkman.io/) for Java Ver Manager)
3. Navigate to the `localRun` folder
4. Follow steps in `.example_envrc`
5. Execute `docker-compose up -d`.
6. Execute `./run.sh`.

# Committing to GitLab
#### Notes:
- *These steps are in reference to creating and registering a GpG key*

#### Steps:
1. Install GPG with [Homebrew](https://brew.sh/)
  1. `brew install gpg`
2. Enter `gpg --full-gen-key`
3. Enter `1` for RSA and RSA (default)
4. Enter `4096`
5. Enter `0`
6. Enter `y`
7. Enter your full name
8. Enter your work email address that is associated with GitLab
9. Press Enter to skip the `Comment` portion
10. Verify your information.  If correct, enter `O`
11. Pick a strong password when asked and type it twice to confirm.
12. Enter `gpg --list-secret-keys --keyid-format LONG <your_email>`
13. Copy the GPG key ID that starts with sec. In the following example, that's `30F2B65B9246B6CA`
```
sec   rsa4096/30F2B65B9246B6CA 2017-08-18 [SC]
      D5E4F29F3275DC0CDA8FFC8730F2B65B9246B6CA
uid                   [ultimate] Mr. Robot <your_email>
ssb   rsa4096/B7ABC0813E4028C0 2017-08-18 [E]
```
14. Export the public key of that ID `gpg --armor --export 30F2B65B9246B6CA`
15. Within GitLab, select your avatar in the top-right corner
16. Select `Edit profile`
17. On the left sidebar, select `GPG Keys`
18. Paste your *public* key in the `Key` text box
19. Select `Add key` to add it to GitLab
20. List the private GPG key you just created `gpg --list-secret-keys --keyid-format LONG <your_email>`
21. Copy the GPG key ID that starts with sec. In the following example, that's `30F2B65B9246B6CA`
```
sec   rsa4096/30F2B65B9246B6CA 2017-08-18 [SC]
      D5E4F29F3275DC0CDA8FFC8730F2B65B9246B6CA
uid                   [ultimate] Mr. Robot <your_email>
ssb   rsa4096/B7ABC0813E4028C0 2017-08-18 [E]
```
22. Enter `git config --global user.signingkey 30F2B65B9246B6CA`
23. Enter `git config --global commit.gpgsign true`
24. Install [GpGSuite](https://gpgtools.org/)
25. Accept all defaults.
26. Enter `export GPG_TTY=$(tty)`


# Downloading Database Backup From Staging
#### Notes:
- *This is not required for running the API, just for getting a database copy*
- *This requires admin privileges and to be a part of the MIDAS Staging Application Group for P1*

#### Steps:
1. Navigate to `https://midas.staging.dso.mil/`
1. Log in to MIDAS with Administrative Permissions
1. Click on the Gavel Icon in the top right corner to navigate to the admin portal
1. Click on the `DATABASE BACKUP & RECOVERY` tab
1. To take backup perform one of the following steps:
  1. Enter a specific name of your choosing in the text field and click the back-up icon
  1. Accept the default naming convention and click the back-up icon
1. Once you have taken the back-up, select the back-up from the `DB Backups` dropdown
1. Click `DOWNLOAD` button

# Importing Downloaded Database Backup into Adminer
#### Notes:
- *You will have to complete [Running the API](#Running the API) prior to running this section*

#### Steps:
1. Navigate to `http://localhost:8181`
2. Log in to Adminer with,
```
UserName: localDBUser  
Password: This can be acquired from the `docker_compose.yml` file.
Database: midas_db
```
3. If your database is currently populated, select all tables and click `Drop`
  1. If you get dropped in on the select database name, select midas_db from the dropdown on the left
4. Click on the `Import` hyperlink in the left pane
5. Click on the `Choose Files` button and a window will open
6. Navigate to the back-up taken in the previous section and click `Open`
7. Inside Adminer, click on the `Execute` button

# Remove Source Control Tokens from Imported Backup
#### Notes:
- *This should be completed after importing a MySQL back-up from staging or prod*

#### Steps:
1. Navigate to `http://localhost:8181`
2. Log in to Adminer with `localDBUser`
3. Click on `select` that is to the left of `source_control` in the left column
4. For each source control listed
  1. Click `Edit`
  2. Clear the text in the token field
  3. Click `Save`

# Updating Manifest File Tags for Debugging
#### Notes:
- *These procedures are only required in the event you are restoring to a different database version*

#### Steps:
1. Navigate to the appropriate environment Kustomization file
  1. For Staging: [Kustomization Staging Manifest File](https://code.il2.dso.mil/abms/products/rise8/midas/midas-manifests/-/blob/master/il2/overlays/staging/kustomization.yaml)
  1. For Prod: [Kustomization Prod Manifest File](https://code.il2.dso.mil/abms/products/rise8/midas/midas-manifests/-/blob/master/il4/overlays/prod/kustomization.yaml)
1. Copy the tag nested under images -> newTag for the API and save it for future step.
1. Click the history button that is at the top right of the page.
1. Select an older version of the file that has the same database version as your desired restore and copy the tag nested under images -> newTag.
  1. **It should be different from the current tag.**
1. Return to the current file and edit it with the tag that was copied in the previous step.
1. Navigate to the appropriate ARGO application (Staging or Prod) and click refresh.

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
- [Maven Repository](https://mvnrepository.com/)

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