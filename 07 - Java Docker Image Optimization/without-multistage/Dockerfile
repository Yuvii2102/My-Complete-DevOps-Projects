FROM ubuntu

RUN apt-get update && apt-get install -y openjdk-21-jdk

WORKDIR /app

COPY Main.java .

RUN javac Main.java

ENTRYPOINT ["java", "Main"]
