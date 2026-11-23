package ro.tuc.ds2020.controllers;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.HourlyConsumptionDTO;
import ro.tuc.ds2020.services.ConsumptionService;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/monitoring")
public class ConsumptionController {

    private final ConsumptionService consumptionService;

    public ConsumptionController(ConsumptionService consumptionService) {
        this.consumptionService = consumptionService;
    }

    @GetMapping("/devices/{id}/consumption")
    public ResponseEntity<List<HourlyConsumptionDTO>> getDaily(
            @PathVariable("id") UUID id,
            @RequestParam("date") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestHeader("X-User-Id") UUID userId,
            @RequestHeader("X-User-Role") String role) {
        return ResponseEntity.ok(consumptionService.getDaily(id, date, userId, role));
    }
}