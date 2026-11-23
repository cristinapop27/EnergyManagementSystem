package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.DeviceSyncEvent;

import java.util.UUID;

@Component
public class DeviceEventPublisher {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceEventPublisher.class);

    private final RabbitTemplate rabbitTemplate;

    public DeviceEventPublisher(RabbitTemplate rabbitTemplate) {
        this.rabbitTemplate = rabbitTemplate;
    }

    public void publishCreated(UUID id, String name) {
        send(RabbitConfig.DEVICE_CREATED_KEY, new DeviceSyncEvent("DEVICE_CREATED", id, name, null));
    }

    public void publishUpdated(UUID id, String name, UUID userId) {
        send(RabbitConfig.DEVICE_UPDATED_KEY, new DeviceSyncEvent("DEVICE_UPDATED", id, name, userId));
    }

    public void publishDeleted(UUID id) {
        send(RabbitConfig.DEVICE_DELETED_KEY, new DeviceSyncEvent("DEVICE_DELETED", id, null, null));
    }

    private void send(String routingKey, DeviceSyncEvent event) {
        rabbitTemplate.convertAndSend(RabbitConfig.SYNC_EXCHANGE, routingKey, event);
        LOGGER.info("Published to {} with key {}: {}", RabbitConfig.SYNC_EXCHANGE, routingKey, event);
    }
}