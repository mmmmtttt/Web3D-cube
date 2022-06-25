# base image
FROM openjdk:8-jdk-alpine
# 定义变量JAR_FILE，值为target/*.jar
ARG JAR_FILE=target/*.jar
# 把build context的JAR_FILE目录下的文件复制到container目录下的demo.jar
COPY ${JAR_FILE} demo.jar
# 在container执行java -jar /demo.jar。
ENTRYPOINT ["java","-jar","/demo.jar"]
