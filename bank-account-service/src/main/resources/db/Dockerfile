FROM egovio/flyway:10.7.1

COPY ./migration/main /flyway/sql

WORKDIR /flyway/sql

CMD ["sh", "-c", "flyway -url=$DB_URL -user=$DB_USER -password=$DB_PASSWORD -locations=/flyway/sql migrate"]