После установки приложения Docker в командной строке ввести команду:

*docker run -d -p 8099:80 -v **path**:/etc/nginx/conf.d/ --name frontend skillbox43/social-net-frontend:latest*

где **path** - путь до файла server.conf, находящегося в директории .localdev\files проекта social-net-backend (например: *С\skillbox\social-net-backend\\.localdev\files*).

После этого, Docker развернет локально контейнер с данным образом на порту 8099 с именем frontend и он отобразится в интерфейсе приложения.

Если после этого ввести в адресной строке браузера *localhost:8099*, то будет открыта страница с нужным нам фронтом.