package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.UserSyncEvent;
import ro.tuc.ds2020.services.UserService;

@Component
public class UserSyncListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(UserSyncListener.class);

    private final UserService userService;

    public UserSyncListener(UserService userService) {
        this.userService = userService;
    }

    @RabbitListener(queues = RabbitConfig.SYNC_QUEUE)
    public void onUserEvent(UserSyncEvent event) {
        LOGGER.info("Received from {}: {}", RabbitConfig.SYNC_QUEUE, event);
        if ("USER_CREATED".equals(event.getType())) {
            userService.createIfAbsent(event);
        } else {
            LOGGER.warn("Unknown event type '{}', ignored", event.getType());
        }
    }
}
