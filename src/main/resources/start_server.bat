
TITLE YJXT_8002

rem mode con cols=90 lines=30

java -jar -Dspring.config.location=application.yml,redis.properties -Dfile.encoding=utf-8 yjxt_server.jar > 1.log


