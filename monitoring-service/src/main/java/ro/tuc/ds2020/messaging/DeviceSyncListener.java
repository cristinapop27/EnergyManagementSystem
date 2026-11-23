package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.DeviceSyncEvent;
import ro.tuc.ds2020.services.DeviceRefService;

@Component
public class DeviceSyncListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeviceSyncListener.class);

    private final DeviceRefService deviceRefService;

    public DeviceSyncListener(DeviceRefService deviceRefService) {
        this.deviceRefService = deviceRefService;
    }

    @RabbitListener(queues = RabbitConfig.SYNC_QUEUE)
    public void onDeviceEvent(DeviceSyncEvent event) {
        LOGGER.info("Received from {}: {}", RabbitConfig.SYNC_QUEUE, event);
        switch (event.getType()) {
            case "DEVICE_CREATED", "DEVICE_UPDATED" ->
                    deviceRefService.save(event.getId(), event.getName(), event.getUserId());
            case "DEVICE_DELETED" -> deviceRefService.delete(event.getId());
            default -> LOGGER.warn("Unknown event type '{}', ignored", event.getType());
        }
    }
}