A fullstack app base on Quarkus Reactive App With React to maintain project task

Download source code !

1. mvnw -Pfrontend,native clean package
2. docker build -f src/main/docker/Dockerfile.native -t quarkus/quarkus-react-reactive .
3. docker run --rm --name postgresql -p 5432:5432 -e POSTGRES_PASSWORD=pgpass -d postgres
4. docker run -d -p 8080:8080 --link postgresql --name reactive-native
   quarkus/quarkus-react-reactive -e -Dquarkus.datasource.username=postgres -Dquarkus.datasource.password=pgpass -Dquarkus.datasource.reactive.url=postgresql://host.docker.internal:5432/postgres -Dquarkus.hibernate-orm.database.generation=create

-- There is a script that will insert several tables and with dummy user and roles

http://localhost:8080/

Test user : admin / admin
Enjoy it, have a fun !


![Monolit2](https://github.com/kg3orgiev/quarkus-react/assets/93709100/d68a80a9-ff44-4c24-bf96-c4a3845f60b7)
![Monolit1](https://github.com/kg3orgiev/quarkus-react/assets/93709100/fac01262-5597-4a0a-af78-e9aa8882703f)
