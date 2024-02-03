## Установка gitlab-runner в Docker  

После установки Docker приступаем к установке gitlab-runner, который будет крутиться в docker контейнере. Выполняем в терминале:  

    docker volume create gitlab-runner-config
    docker run -d --name gitlab-runner --restart always -v gitlab-runner-config:/etc/gitlab-runner -v /var/run/docker.sock:/var/run/docker.sock gitlab/gitlab-runner:latest

Как только процедура загрузки образа и запуска контейнера закончится, переходим к регистрации. Выполняем команду:

    docker exec -it gitlab-runner gitlab-runner register

Вы увидите приглашение для ввода URL gitlab инстанса:

    Register the runner with this URL:
        https://gitlab.skillbox.ru/

Далее по порядку вводим:
    
    Enter a description for the runner: social-net-backend;
    Enter tags for the runner (comma-separated): оставляем пустым;
    Enter optional maintenance note for the runner: оставляем пустым;
    Enter an executor: docker;
    Enter the default Docker image: docker:latest

В результате, вы должны увидеть сообщение об успешной регистрации gitlab-runner

Затем в конфиге по пути etc/gitlab-runner/config.toml нужно отредактировать:

    privileged = true
    volumes = ["gitlab-runner-builds:/builds", "gitlab-runner-cache:/cache","/var/run/docker.sock:/var/run/docker.sock"]