web: java $JAVA_OPTS -Dserver.port=$PORT
web: java -jar eureka/target/eureka-1.0-SNAPSHOT.jar --spring.profiles.active=peer
web: java -jar gate/target/gate-1.0-SNAPSHOT.jar
web: java -jar user/target/user-1.0-SNAPSHOT.jar