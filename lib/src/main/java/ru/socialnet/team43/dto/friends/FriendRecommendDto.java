package ru.socialnet.team43.dto.friends;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FriendRecommendDto {
    private Long friendId;
    private Integer rating;
}
