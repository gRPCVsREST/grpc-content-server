FROM java:8
WORKDIR /
ADD ./build/libs/grpc-content-service-1.0-SNAPSHOT.jar grpc-content-service.jar
EXPOSE 8080
CMD java -jar grpc-content-service.jar
