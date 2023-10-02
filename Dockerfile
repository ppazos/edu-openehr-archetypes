FROM eclipse-temurin:11.0.20.1_1-jdk-alpine
RUN apk update && apk add wget unzip git

RUN wget https://services.gradle.org/distributions/gradle-7.6-bin.zip
RUN mkdir /opt/gradle
RUN unzip -d /opt gradle-7.6-bin.zip

ENV GRADLE_HOME=/opt/gradle-7.6
ENV PATH=$PATH:$GRADLE_HOME/bin

RUN git clone https://github.com/ppazos/edu-openehr-archetypes.git archetypes
RUN cd archetypes && gradle fatJar