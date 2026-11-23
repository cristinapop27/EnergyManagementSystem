package ro.tuc.ds2020.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.DeviceDTO;
import ro.tuc.ds2020.dtos.DeviceDetailsDTO;
import ro.tuc.ds2020.services.DeviceService;

import java.util.List;
import java.util.UUID;

@RestController
@CrossOrigin
@RequestMapping("/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @GetMapping
    public ResponseEntity<List<DeviceDTO>> getDevices() {
        return ResponseEntity.ok(deviceService.findDevices());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DeviceDTO> getDevice(@PathVariable("id") UUID id) {
        return ResponseEntity.ok(deviceService.findDeviceById(id));
    }

    @GetMapping("/user/{userId}")
    public ResponseEntity<List<DeviceDTO>> getDevicesByUser(@PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(deviceService.findDevicesByUser(userId));
    }

    @PostMapping
    public ResponseEntity<UUID> createDevice(@Valid @RequestBody DeviceDetailsDTO dto) {
        return new ResponseEntity<>(deviceService.insert(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<DeviceDTO> updateDevice(@PathVariable("id") UUID id,
                                                  @Valid @RequestBody DeviceDetailsDTO dto) {
        return ResponseEntity.ok(deviceService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable("id") UUID id) {
        deviceService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PutMapping("/{deviceId}/user/{userId}")
    public ResponseEntity<DeviceDTO> assignDevice(@PathVariable("deviceId") UUID deviceId,
                                                  @PathVariable("userId") UUID userId) {
        return ResponseEntity.ok(deviceService.assignToUser(deviceId, userId));
    }

    @DeleteMapping("/{deviceId}/user")
    public ResponseEntity<DeviceDTO> unassignDevice(@PathVariable("deviceId") UUID deviceId) {
        return ResponseEntity.ok(deviceService.unassign(deviceId));
    }
}