FROM openjdk:11.0.1-jdk

COPY build/libs/diet-0.0.1-SNAPSHOT.jar /diet.jar

CMD ["java", "-jar", "diet.jar"]