package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.UserSyncEvent;
import ro.tuc.ds2020.services.CredentialService;

@Component
public class UserSyncListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncListener.class);

    private final CredentialService credentialService;

    public UserSyncListener(CredentialService credentialService) {
        this.credentialService = credentialService;
    }

    @RabbitListener(queues = RabbitConfig.SYNC_QUEUE)
    public void onUserEvent(UserSyncEvent event) {
        LOGGER.info("Received from {}: {}", RabbitConfig.SYNC_QUEUE, event);
        switch (event.getType()) {
            case "USER_UPDATED" -> credentialService.update(event.getId(), event.getUsername(), event.getRole());
            case "USER_DELETED" -> credentialService.deleteIfPresent(event.getId());
            default -> LOGGER.warn("Unknown event type '{}', ignored", event.getType());
        }
    }
}
