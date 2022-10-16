### Decisions:
- I left H2 database as the solution description asks for embedded one
- I left the hibernate auto schema generation enabled as this is demo and uses embedded database,
  for production version I would use separate database and for example liquibase to manage the changes, rather then depend on hibernate auto creation
- There was no specific requirement around security for this demo, so I did not add any spring security configuration
- I added lombok to the project as I feel it is slighty easier/faster to implement such challenges with it, but I know usage of lombok  can be controversial topic... :)

### Basic information:
- Swagger UI is located by default at: http://localhost:7070/swagger-ui/index.html
- You can run it from maven using `mvn spring-boot:run`
