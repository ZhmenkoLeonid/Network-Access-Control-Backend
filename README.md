# Kurs_Work_NAC
Реализация системы Network Access Control в рамках дипломного проекта.
# Описание проекта
Созданная система NAC позволяет контролировать доступ к ресурсам в сети организации. Инструментом реализации контроля доступа является граничный маршрутизатор, а именно список доступа в нём. С помощью списка доступа ACL, настроенного в режиме белого списка, система может предоставлять доступ к ресурсам, добавляя для ip адреса клиента порт или порты, на которых ресурс или ресурсы работают или удаляя их. 

Процесс предоставления клиенту доступа состоит из двух этапов – pre-connection и post-connection. Pre-connection – аутентификация пользователя, которая реализуется с использованием JWT токена, и аутентификация устройства, при которой проверяется соответствие собранных программой-клиентом данных требованиям организации. Post-connection – периодическая аутентификация устройства после успешного выполнения процесса pre-connection. 

Помимо аутентификации клиента и его устройства был реализован модуль обнаружения вторжений, который на данный момент позволяет выявлять превышение количества трафика путём сравнения со средним значением у пользователя за определённый период. Мониторинг трафика осуществляется с помощью протокола NetFlow компании Cisco. В случае обнаружения нарушений со стороны клиента система NAC оповещает об этом администратора. Оповещения и другую информацию возможно просмотреть в панели администратора.

Стек:

1) Spring Boot,
2) Spring Jpa,
3) Spring Security,
4) Spring Web,
5) СУБД PostgreSQL (для данных о клиентах и их устрйствах),
6) СУБД ClickHouse (для данных о трафике клиентов),
7) Junit,
8) Testcontainers,
9) Mapstruct,
10) Lombok,
11) Jsch (взаимодействие с роутером по ssh),
12) Jflow (Сбор и десериализация пакетов протокола Netflow),
13) Swagger,
14) JJWT (работа с json web token'ами),
15) Maven.

Проект состоит из следующих модулей:

1) application - основной модуль, содержащий Main класс,
2) ids - модуль, содержащий классы, выполняющие "прослушивание" данный о трафике через протокол netflow, а также анализ этих данных,
3) router - модуль, содержащий классы, использующиеся для отправки команд маршрутизатору через ssh,
4) web - модуль, содержащий контроллеры и сервисы api (как для аутентификации, так и для получения данных, например, на фронтенде).
# Конфигурация
В модуле application в src/resources/application.yml необходимо указать следующие параметры:

Свойства, связанные с анализатором пакетов:

1) netflow.analyze.analyzeFrequencyMillis - частота запуска анализатора трафика в миллисекундах,
2) netflow.analyze.updateMeanValueTimeMillis - интервал подсчёта среднего значения пакета в миллисекундах (например, при числе 3.600.000 будет подсчёт среднего числа пакетов за последний час),
3) netflow.analyze.flowMultiplierLimitation - множитель, во сколько раз допустимо превышение числа пакетов по сравнению со средним,

Свойства, связанные с маршрутизатором:

1) router.ipAddress - ip адрес маршрутизатора,
2) router.username - имя пользователя для ssh соединения,
3) router.password - пароль для ssh соединения,
4) router.accessListName - имя списка доступа, который будет использоваться для контроля доступа в сеть,
5) router.collector.port - порт, на который будут приходить данные протокола Netflow.
# Сборка
    mvn clean package
# Запуск (Для запуска и корректной работы необходим установленный Docker и настроенный маршрутизатор)
Пример настройки маршрутизатора на примере маршрутизатора Keenetic показан в файле router-config.txt

    1. cd docker
    2. docker-compose up
Примечание: помимо приложения в докер-контейнерах поднимаются 2 базы данных: PostgreSQL и ClickHouse.

# Дополнительно
Доступ к swagger-ui:

    https://<ip_адрес>/swagger-ui/index.html 

Например, 

    https://localhost/swagger-ui/index.html
