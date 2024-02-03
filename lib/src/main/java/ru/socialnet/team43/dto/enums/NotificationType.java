package ru.socialnet.team43.dto.enums;

import lombok.Getter;

@Getter
public enum NotificationType {
    LIKE(1, "LIKE", "Лайк"),
    POST(2, "POST", "Новый пост"),
    POST_COMMENT(3, "POST_COMMENT", "Комментарий к посту"),
    COMMENT_COMMENT(4, "COMMENT_COMMENT", "Ответ на комментарий"),
    MESSAGE(5, "MESSAGE", "Личное сообщение"),
    FRIEND_REQUEST(6, "FRIEND_REQUEST", "Запрос дружбы"),
    FRIEND_BIRTHDAY(7, "FRIEND_BIRTHDAY", "День рождения друга"),
    SEND_EMAIL_MESSAGE(8, "SEND_EMAIL_MESSAGE", "Отправка оповещений на email"),
    FRIEND_APPROVE(9, "FRIEND_APPROVE", ""),
    FRIEND_BLOCKED(10, "FRIEND_BLOCKED", ""),
    FRIEND_UNBLOCKED(11, "FRIEND_UNBLOCKED", ""),
    FRIEND_SUBSCRIBE(12, "FRIEND_SUBSCRIBE", ""),
    USER_BIRTHDAY(13, "USER_BIRTHDAY", "День рождения пользователя");

    private final int id;
    private final String code;
    private final String name;

    NotificationType(int id, String code, String name) {
        this.id = id;
        this.code= code;
        this.name = name;
    }
}
