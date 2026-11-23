package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.UserRefDTO;
import ro.tuc.ds2020.dtos.UserSyncEvent;
import ro.tuc.ds2020.services.UserRefService;

@Component
public class SyncEventListener {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncEventListener.class);

    private final UserRefService userRefService;

    public SyncEventListener(UserRefService userRefService) {
        this.userRefService = userRefService;
    }

    @RabbitListener(queues = RabbitConfig.SYNC_QUEUE)
    public void onSyncEvent(UserSyncEvent event) {
        LOGGER.info("Received from {}: {}", RabbitConfig.SYNC_QUEUE, event);

        switch (event.getType()) {
            case "USER_CREATED", "USER_UPDATED" -> userRefService.upsert(new UserRefDTO(
                    event.getId(), event.getName(), event.getUsername(), event.getRole()));
            case "USER_DELETED" -> userRefService.deleteIfPresent(event.getId());
            default -> LOGGER.warn("Unknown event type '{}', ignored", event.getType());
        }
    }
}