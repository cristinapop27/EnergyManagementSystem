package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserSyncEvent;

import java.util.UUID;

@Component
public class SyncEventPublisher {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public SyncEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishUserUpdated(UserDTO user) {
        send(RabbitConfig.USER_UPDATED_KEY, new UserSyncEvent("USER_UPDATED",
                user.getId(), user.getName(), user.getUsername(), user.getRole()));
    }

    public void publishUserDeleted(UUID id) {
        send(RabbitConfig.USER_DELETED_KEY, new UserSyncEvent("USER_DELETED", id, null, null, null));
    }

    private void send(String routingKey, UserSyncEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.SYNC_EXCHANGE, routingKey, event);
        LOGGER.info("Published to {} with key {}: {}", RabbitConfig.SYNC_EXCHANGE, routingKey, event);
    }
}