package ru.socialnet.team43.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import ru.socialnet.team43.client.NotificationsClient;
import ru.socialnet.team43.dto.CountDto;
import ru.socialnet.team43.dto.notifications.*;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.util.List;

@Service
@AllArgsConstructor
@Slf4j
public class NotificationPRServiceImpl implements NotificationPRService {
    private NotificationsClient client;

    @Override
    public NotificationSettingDto getSettings(String email) {
        log.info("setting up notifications for the user: {}", email);
        return client.getSettings(email);
    }

    @Override
    public NotificationSettingDto updateSetting(NotificationUpdateDto updateDto,
                                                String email) {
        log.info("Update notification settings for user: {}", email);
        return client.updateSetting(updateDto, email);
    }

    @Override
    public NotificationSettingDto createSetting(Long id) {
        log.info("create a notification setting for the user: {}", id);
        return client.createSettings(id);
    }

    @Override
    public Page<NotificationsDto> getAll(String email,
                                       String sort,
                                       int offset, int limit,
                                       Pageable pageable
    ) {
        Pageable pageRequest = PageRequest.of(offset, limit, Sort.by(Sort.Direction.ASC, sort));

        List<NotificationsDto> content = client.getNotifications(email, pageable);

        log.info("receiving all content for the user: {}; content: {}; request: {}",
                email, content, pageRequest);

        int start = (int) pageRequest.getOffset();
        int end = Math.min((start + pageRequest.getPageSize()), content.size());

        List<NotificationsDto> pageContent = content.subList(start, end);
        Page<NotificationsDto> dtoPage = new PageImpl<>(pageContent, pageRequest, content.size());

        log.info("receiving all content for the user: {} - pageContent: {};" +
                        " request: {}; TotalPages: {}; TotalElements: {};" +
                        " Number: {}; Size: {}; Content: {};" +
                        " Sort: {}; First: {}; Last: {};" +
                        " NumberOfElements: {}; Empty: {};",
                email, dtoPage.getContent().size(),
                dtoPage, dtoPage.getTotalPages(), dtoPage.getTotalElements(),
                dtoPage.getNumber(), dtoPage.getSize(), dtoPage.getContent(),
                dtoPage.getSort(), dtoPage.isFirst(), dtoPage.isLast(),
                dtoPage.getNumberOfElements(), dtoPage.isEmpty());
        return dtoPage;
    }

    @Override
    public CountDto getNotificationCount(String email) {
        log.info("Count notifications for the user: {}", email);
        return client.getNotificationCount(email);
    }

    @Override
    public void setIsRead(String email){
        client.setIsRead(email);
        log.info("all notifications for user: {} are marked as read", email);
    }
}
