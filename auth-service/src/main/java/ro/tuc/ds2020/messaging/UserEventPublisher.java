package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.UserSyncEvent;
import ro.tuc.ds2020.entities.Role;

import java.util.UUID;

@Component
public class UserEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public UserEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserCreated(UUID id, String name, String username, Role role) {
        UserSyncEvent event = new UserSyncEvent("USER_CREATED", id, name, username, role);
        rabbitTemplate.convertAndSend(RabbitConfig.SYNC_EXCHANGE, RabbitConfig.USER_CREATED_KEY, event);
        LOGGER.info("Published to {} with key {}: {}", RabbitConfig.SYNC_EXCHANGE, RabbitConfig.USER_CREATED_KEY, event);
    }
}
