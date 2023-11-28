A fullstack app base on Quarkus Reactive App With React to maintain project task

Download source code !

1. ./mvnw -Pfrontend,native clean package
2. docker build -f src/main/docker/Dockerfile.native -t quarkus/quarkus-react-reactive .
3. docker run --rm --name postgresql -p 5432:5432 -e POSTGRES_PASSWORD=pgpass -d postgres
4. docker run -d -p 8080:8080 --link postgresql --name reactive-native
   quarkus/quarkus-react-reactive -e -Dquarkus.datasource.username=postgres -Dquarkus.datasource.password=pgpass -Dquarkus.datasource.reactive.url=postgresql://host.docker.internal:5432/postgres -Dquarkus.hibernate-orm.database.generation=create


http://localhost:8080/

Test user : admin / admin
Enjoy it, have a fun !
