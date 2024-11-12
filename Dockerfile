FROM eclipse-temurin:17-jdk-alpine as build
WORKDIR /workspace/app
 
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
COPY ./certs/* .
 
RUN dos2unix mvnw
RUN chmod +x ./mvnw
RUN ./mvnw install -DskipTests
RUN mkdir -p target/dependency && (cd target/dependency; jar -xf ../*.jar)
 
FROM eclipse-temurin:17-jdk-alpine
VOLUME /tmp
ARG DEPENDENCY=/workspace/app/target/dependency
ARG CERTS=/workspace/app
COPY --from=build ${DEPENDENCY}/BOOT-INF/lib /app/lib
COPY --from=build ${DEPENDENCY}/META-INF /app/META-INF
COPY --from=build ${DEPENDENCY}/BOOT-INF/classes /app
COPY --from=build ${CERTS}/JWT* /etc/ca-certificates
COPY --from=build ${CERTS}/JKS* /etc/ca-certificates

EXPOSE 8085

ENTRYPOINT ["java","-cp","app:app/lib/*","mx.gob.banobras.apitokenizer.ApiTokenizerApplication"]
