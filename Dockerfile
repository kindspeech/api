# Use the official gradle image to create a build artifact.
# https://hub.docker.com/_/gradle
FROM openjdk:8 as builder

# Copy local code to the container image.
WORKDIR /app
COPY gradle ./gradle
COPY src ./src
COPY gradlew build.gradle ./

# Build a release artifact.
RUN ./gradlew installDist --no-daemon

# Use the Official OpenJDK image for a lean production stage of our multi-stage build.
# https://hub.docker.com/_/openjdk
# https://docs.docker.com/develop/develop-images/multistage-build/#use-multi-stage-builds
FROM openjdk:8-jre-alpine

# Copy the jar to the production image from the builder stage.
WORKDIR /app
COPY --from=builder /app/build/install/api .

ENV JAVA_OPTS -Djava.security.egd=file:/dev/./urandom

# Run the web service on container startup.
CMD [ "./bin/api" ]
