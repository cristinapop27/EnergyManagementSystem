package ro.tuc.ds2020.messaging;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import ro.tuc.ds2020.config.RabbitConfig;
import ro.tuc.ds2020.dtos.MeasurementMessage;
import ro.tuc.ds2020.entities.HourlyConsumption;
import ro.tuc.ds2020.services.MeasurementService;

@Component
public class MeasurementListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(MeasurementListener.class);

    private final MeasurementService measurementService;

    public MeasurementListener(MeasurementService measurementService) {
        this.measurementService = measurementService;
    }

    @RabbitListener(queues = RabbitConfig.MEASUREMENTS_QUEUE)
    public void onMeasurement(MeasurementMessage message) {
        LOGGER.info("Received from {}: {}", RabbitConfig.MEASUREMENTS_QUEUE, message);
        HourlyConsumption hour = measurementService.process(message);
        LOGGER.info("Device {} hour {} total = {} kWh", hour.getDeviceId(), hour.getHourStart(), hour.getTotalKwh());
    }
}
