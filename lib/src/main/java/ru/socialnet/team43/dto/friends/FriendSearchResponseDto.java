package ru.socialnet.team43.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.socialnet.team43.dto.enums.FriendshipStatus;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendSearchResponseDto {

    private FriendshipStatus statusCode;
    private Long id;
    private Long friendId;
}
