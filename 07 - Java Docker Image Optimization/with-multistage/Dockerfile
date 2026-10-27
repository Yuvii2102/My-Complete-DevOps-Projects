FROM eclipse-temurin:21-jdk AS build

WORKDIR /app
COPY Main.java .
RUN javac Main.java

FROM gcr.io/distroless/java21-debian12:nonroot

WORKDIR /app
COPY --from=build /app/Main.class .
COPY --from=build /app/Food.class .

ENTRYPOINT ["java", "-cp", "/app", "Main"]
