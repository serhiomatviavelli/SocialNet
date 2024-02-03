package ru.socialnet.team43.dto.enums;

import lombok.Getter;
import lombok.ToString;

@ToString
public enum FriendshipStatus {

    REQUEST_FROM("Входящий запрос на подписку"),
    REQUEST_TO("Исходящий запрос на добавление в друзья"),
    FRIEND("Друг"),
    BLOCKED("Пользователь в черном списке"),
    SUBSCRIBED("Подписчик"),
    WATCHING("Подписка"),
    NONE("Нет статуса");

    @Getter
    private final String description;

    FriendshipStatus(String description) {
        this.description = description;
    }
}
