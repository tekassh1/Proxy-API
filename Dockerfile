FROM openjdk:latest
LABEL authors="geeks"

WORKDIR /usr/local/app
ADD ./target/ProxyAPI-3.2.3.jar ./app.jar
CMD java -jar ./app.jar