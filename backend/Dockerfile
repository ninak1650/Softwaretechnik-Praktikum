# build jar and extract dependencies
FROM eclipse-temurin:21-jdk-alpine AS app-builder
WORKDIR /app
RUN apk update && apk add unzip
COPY .mvn .mvn
COPY mvnw pom.xml ./
RUN ./mvnw dependency:go-offline
COPY src src
RUN ./mvnw package -DskipTests
RUN mkdir -p unpacked && unzip -d unpacked target/*.jar
RUN $JAVA_HOME/bin/jdeps --ignore-missing-deps --print-module-deps -q --recursive --multi-release 21 --class-path="./unpacked/BOOT-INF/lib/*" --module-path="./unpacked/BOOT-INF/lib/*" ./target/*.jar > /deps.info

# build custom JRE with only required modules
FROM eclipse-temurin:21-jdk-alpine AS jre-builder
COPY --from=app-builder /deps.info /deps.info
RUN $JAVA_HOME/bin/jlink \
    --add-modules $(cat /deps.info)\
    --strip-debug \
    --no-man-pages \
    --no-header-files \
    --output /javaruntime

# build final image
FROM alpine:3.21.0 AS final
EXPOSE 8080
ENV JAVA_HOME=/jre
ENV PATH="${JAVA_HOME}/bin:${PATH}"
RUN adduser -u 1000 -D bornalecker
USER 1000
WORKDIR /home/bornalecker

COPY --chown=1000:1000 --from=app-builder /app/target/*.jar /home/bornalecker/app.jar
COPY --from=jre-builder /javaruntime $JAVA_HOME
CMD [ "java" , "-jar", "/home/bornalecker/app.jar" ]
