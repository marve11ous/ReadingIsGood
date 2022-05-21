## Getting Started

#### Build

`mvn clean package -DskipTests`

#### Run

`docker-compose up`

#### Swagger

http://localhost:8080/swagger-ui/index.html

#### Auth

*Note:* Can be configured by `security.users`

* Admin credentials: admin/admin
* User credentials: user/password

## Info

* Java 17
* [Spring Boot](https://spring.io/)
* [PostgreSQL](https://www.postgresql.org/): data storage
* [Springdoc](https://springdoc.org/): OpenApi 3
* [Liquibase](https://www.liquibase.org/): DB version control
* [Lombok](https://projectlombok.org/): get rid of boilerplate
* [Mapstruct](https://mapstruct.org/): mappings
* [Problem](https://github.com/zalando/problem): exceptions handling
* [Logbook](https://github.com/zalando/logbook): logging
* [Junit5](https://junit.org/junit5/): testing
* [Testcontainers](https://www.testcontainers.org/): integration testing