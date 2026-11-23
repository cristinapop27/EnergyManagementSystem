package ro.tuc.ds2020.controllers;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ro.tuc.ds2020.dtos.UserRefDTO;
import ro.tuc.ds2020.services.UserRefService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/user-refs")
public class UserRefController {

    private final UserRefService userRefService;

    public UserRefController(UserRefService userRefService) {
        this.userRefService = userRefService;
    }

    @GetMapping
    public ResponseEntity<List<UserRefDTO>> getAll() {
        return ResponseEntity.ok(userRefService.findAll());
    }

    @PostMapping
    public ResponseEntity<UUID> create(@Valid @RequestBody UserRefDTO dto) {
        return new ResponseEntity<>(userRefService.create(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<UserRefDTO> update(@PathVariable("id") UUID id,
                                             @Valid @RequestBody UserRefDTO dto) {
        return ResponseEntity.ok(userRefService.update(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable("id") UUID id) {
        userRefService.delete(id);
        return ResponseEntity.noContent().build();
    }
}