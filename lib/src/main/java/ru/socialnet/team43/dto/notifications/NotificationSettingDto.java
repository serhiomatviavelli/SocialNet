package ru.socialnet.team43.dto.notifications;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NotificationSettingDto {
    private String id;
    private boolean enableLike;
    private boolean enablePost;
    private boolean enablePostComment;
    private boolean enableCommentComment;
    private boolean enableMessage;
    private boolean enableFriendRequest;
    private boolean enableFriendBirthday;
    private boolean enableSendEmailMessage;
}
