FROM openjdk:17-jdk-slim

LABEL maintainer="eGovernments Foundation"

# Set working directory
WORKDIR /opt/certificate-service

# Add application user
RUN groupadd -r certificate && useradd -r -g certificate certificate

# Copy JAR file
COPY target/certificate-service-*.jar certificate-service.jar

# Create logs directory
RUN mkdir -p /var/log/certificate-service && \
    chown -R certificate:certificate /opt/certificate-service /var/log/certificate-service

# Switch to application user
USER certificate

# Health check
HEALTHCHECK --interval=30s --timeout=10s --start-period=60s --retries=3 \
    CMD curl -f http://localhost:8039/certificate-service/health || exit 1

# Expose port
EXPOSE 8039

# Set JVM options
ENV JAVA_OPTS="-Xmx512m -Xms256m -XX:+UseG1GC -XX:MaxGCPauseMillis=200"

# Set logging configuration
ENV LOGGING_CONFIG="-Dlogging.config=classpath:logback-spring.xml"

# Run the application
ENTRYPOINT ["sh", "-c", "java $JAVA_OPTS $LOGGING_CONFIG -jar certificate-service.jar"]