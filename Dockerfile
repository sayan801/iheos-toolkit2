FROM tomcat:9-jdk11

COPY ./xdstools2/target/xdstools2-*.war /usr/local/tomcat/webapps/xdstools.war

EXPOSE 8080

CMD ["catalina.sh", "run"]
