Локальный запуск
1 замена проперти
2 шаг. запуск докер композ (docker-compose up -d)
3 запуск приложения

(если не подключается к БД)
попробовать изменить файл pg_hba.conf
по такому пути 

Linux: /etc/postgresql/<версия>/main/pg_hba.conf или /var/lib/pgsql/data/pg_hba.conf
Windows: C:\Program Files\PostgreSQL\<версия>\data\pg_hba.conf

Убедись, что вот так:

local   all             all                                     md5
host    all             all             127.0.0.1/32            md5
host    all             all             ::1/128                 md5

проперти для локального запуска вне родной среды

spring.application.name=atelieBot
bot.name= atelie-shilova-dev-bot
bot.token = 7656137298:AAEBDQUFQBN6FC4kbRVSbI71rmwhA3dYd7Y
spring.datasource.url=jdbc:postgresql://localhost:5432/atelie
spring.datasource.username=user
spring.datasource.password=password
spring.jpa.properties.hibernate.dialect=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=update
encryption.aes.key = 123456789abcdef1
#logging.level.root = warn
logging.level.org.hibernate.SQL=DEBUG
logging.level.org.hibernate.type.descriptor.sql=TRACE


