
=  README Spring Boot Rest API


Comments to the implementation

At this stage has been already implemented:

- Required endpoints:
    * */api/users/ (GET)*: return the list of all users.
    * */api/users/{username}/ (GET)*: return a single user.
    * */api/users/ (POST)*: create a user.
    * */api/users/{username}/ (PUT)*: update the information of a single user.
    * */api/users/{username}/ (DELETE)*: delete a single user.
    * */api/users/generate/{number}/ (GET)*: generates a number, provided as a parameter, of random users using https://randomuser.me[Random User Generator] service, and saves those users into the H2 db.
    * */api/users/tree/ (GET)*: *not yet implemented as planned*.

- H2 in memory database:
    * data.sql and schema.sql are provided in resources folder for schema initialization and for sample data saving.
    * H2 database console is available at http://localhost:8080/h2-console/login.jsp
    * Username and password are set in application.properties file.

- Services:
    * UserService: manages all logic related to calls to DB.
    * UserGeneratorService: manages logic and call to external API (Random User Generator).

- UserMapper: used to transform (map) between User entity, UserDTO and RandomUser (user object recieved from Random User Generator).

- JSONschema2Pojo: used to generate Java classes according to the JSON payload received from Random User Generator.
  For this purpose, a JSON schema was generated with this tool: https://codebeautify.org/json-to-json-schema-generator ,  using an actual payload from Random User Generator.

- Logging: still contains logs used for development, not yet to be considered as a finished feature.

- Basic Swagger, not yet customized, so not yet to be considered as a finished feature. Swagger UI is available at: http://localhost:8080/swagger-ui/index.html#/

- Dockerfile and Docker compose file.

- Tests: a couple of tests for UserController.

=== How to run

Even though the project contains Dockerfile and Docker compose file running on Docker is not needed, because a H2 in memory database is used.
Therefore, there are two available ways of running the app locally.
Both of them require this step:

In project root directory:  'mvn clean compile'    (this command will also make JSONschema2Pojo create the generated sources required for running the app)

- Running as Spring Boot .jar file: in project root directory 'java -jar targe/challenge-0.0.1-SNAPSHOT.jar'
- Running in Docker:
    * create Docker image: 'docker build -t challenge .'
    * run docker-compose file: 'docker-compose up'

Application is listening on port 8080, so base path for all endpoints is: localhost:8080/
