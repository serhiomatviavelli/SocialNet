package ru.socialnet.team43.websocket;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.CloseStatus;
import org.springframework.web.socket.TextMessage;
import org.springframework.web.socket.WebSocketSession;
import org.springframework.web.socket.handler.AbstractWebSocketHandler;

import ru.socialnet.team43.client.CommunicationClient;
import ru.socialnet.team43.client.ProfileClient;
import ru.socialnet.team43.dto.PersonDto;
import ru.socialnet.team43.dto.dialogs.MessageDto;
import ru.socialnet.team43.dto.enums.ReadStatus;
import ru.socialnet.team43.security.jwt.JwtUtils;

import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebSocketHandler extends AbstractWebSocketHandler {

    private static final Map<Long, WebSocketSession> sessions = new ConcurrentHashMap<>();
    private static final ObjectMapper objectMapper;

    private final JwtUtils jwtUtils;
    private final ProfileClient profileClient;
    private final CommunicationClient communicationClient;

    static {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void handleTextMessage(@NonNull WebSocketSession session, @NonNull TextMessage message) {
        // Обработка входящего текстового сообщения
        log.debug("Message was received from session:{}", session.getId());

        try {
            MessageWS messageWS = objectMapper.readValue(message.getPayload(), MessageWS.class);
            if (messageWS == null) throw new IOException("MessageWS is null.");

            MessageDto newMessage = messageWS.getData();
            newMessage.setReadStatus(ReadStatus.SENT);
            newMessage.setTime(ZonedDateTime.now());
            messageWS.setData(newMessage);

            // Сохраняем полученное сообщение в БД
            if (communicationClient.saveMessage(newMessage)) {
                log.debug("Message was saved in the database. session:{}", session.getId());
            }

            String jsonMessage = objectMapper.writeValueAsString(messageWS);
            // Отправляем сообщение автору, чтобы фронт отправил запрос
            // на обновление списка сообщений, после сохранения сообщения в БД
            session.sendMessage((new TextMessage(jsonMessage)));

            WebSocketSession recipientSession = sessions.get(messageWS.getRecipientId());
            if (recipientSession == null) {
                // Если сессия для пользователя не найдена значит пользователь не в сети
                // Отправлять сообщение по WS сотвственно не нужно, только сохранить в БД как выше.
                return;
            }

            // Если же получатель подключен к WS отправляем ему сообщение
            recipientSession.sendMessage((new TextMessage(jsonMessage)));

            log.info("Message has been sent to recipient:{}.session:{}", messageWS.getRecipientId(), session.getId());

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void afterConnectionEstablished(@NonNull WebSocketSession session) {
        // Логика после установления соединения

        PersonDto person = getPersonBySession(session);
        if (person == null) return;

        sessions.put(person.getId(), session);
        log.info("{} - Connection established. session:{}", person.getEmail(), session.getId());
    }

    @Override
    public void afterConnectionClosed(
            @NonNull WebSocketSession session, @NonNull CloseStatus closeStatus) {
        // Логика после закрытия соединения

        PersonDto person = getPersonBySession(session);
        if (person == null) return;

        sessions.remove(person.getId());
        log.info("{} : Connection closed. session:{}", person.getEmail(), session.getId());

        // Если все сессии закрылись
        if (sessions.get(person.getId()) == null) {
            profileClient.updateIsOnlineForAccount(person.getEmail(), false);
        }
    }

    private PersonDto getPersonBySession(WebSocketSession session) {

        String token = session.getAttributes().get("token").toString();
        if (token == null) {
            log.warn("Failed to get token from cookies. session:{}", session.getId());
            return null;
        }

        String userName = jwtUtils.getUserNameFromAccessToken(token);

        if (userName == null) {
            log.warn("Failed to get userName from jwt token. session:{}", session.getId());
            return null;
        }

        ResponseEntity<PersonDto> response = profileClient.getMyProfile(userName);
        if (response.getStatusCode().equals(HttpStatus.OK) && response.getBody() != null) {
            return response.getBody();
        } else {
            log.warn(
                    "Failed to get PersonDto by email. profileClient.getMyProfile({}) StatusCode[{}]. session:{}",
                    session.getId(),
                    userName,
                    response.getStatusCode());
            return null;
        }
    }
}
