## Создание контейнера с базой данных PostgreSQL в Docker  

После установки приложения Docker в командной строке ввести команду для запуска контейнера с PostgreSQL (образ при необходимости загрузится автоматически) :  

    docker run -d --name db -p 5432:5432 -v data:/var/lib/postgresql/data -e POSTGRES_USER=skillbox43 -e POSTGRES_DB=team43 -e POSTGRES_PASSWORD=skillbox43 postgres:16.0-alpine3.18

где

   - `db` - имя контейнера;
   - `data` - имя volume.

Для проверки работы контейнера можно выполнить следующие команды:

   - переход к выполнению команд в запущенном контейнере:

    docker exec -it db bash

   - переход в консоль PostgreSQL:
  
    psql -h localhost -U skillbox43 team43

Далее можно выполнять команды PostgreSQL.  

   ![PostgreSQL](/images/Docker_PostgreSQL.png)