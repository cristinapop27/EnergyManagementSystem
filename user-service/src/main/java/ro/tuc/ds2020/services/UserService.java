package ro.tuc.ds2020.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.amqp.AmqpException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.DuplicateResourceException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.ResourceNotFoundException;
import ro.tuc.ds2020.controllers.handlers.exceptions.model.SyncFailedException;
import ro.tuc.ds2020.dtos.UserDTO;
import ro.tuc.ds2020.dtos.UserDetailsDTO;
import ro.tuc.ds2020.dtos.UserSyncEvent;
import ro.tuc.ds2020.dtos.builders.UserBuilder;
import ro.tuc.ds2020.entities.User;
import ro.tuc.ds2020.messaging.SyncEventPublisher;
import ro.tuc.ds2020.repositories.UserRepository;

import java.util.List;
import java.util.UUID;

@Service
public class UserService {
    private static final Logger LOGGER = LoggerFactory.getLogger(UserService.class);

    private final UserRepository userRepository;
    private final SyncEventPublisher syncEventPublisher;

    public UserService(UserRepository userRepository, SyncEventPublisher syncEventPublisher) {
        this.userRepository = userRepository;
        this.syncEventPublisher = syncEventPublisher;
    }

    public List<UserDTO> getAllUsers() {
        return userRepository.findAll().stream()
                .map(UserBuilder::toUserDTO)
                .toList();
    }

    public UserDTO findUserById(UUID id) {
        return UserBuilder.toUserDTO(getUserOrThrow(id));
    }

    @Transactional
    public void createIfAbsent(UserSyncEvent event) {
        if (userRepository.existsById(event.getId())) {
            return;
        }
        User user = new User(event.getName(), event.getUsername(), event.getRole());
        user.setId(event.getId());
        userRepository.save(user);
    }

    @Transactional
    public UserDTO update(UUID id, UserDetailsDTO dto) {
        User user = getUserOrThrow(id);

        if (!user.getUsername().equals(dto.getUsername())
                && userRepository.existsByUsername(dto.getUsername())) {
            throw new DuplicateResourceException("User with username: " + dto.getUsername());
        }

        user.setName(dto.getName());
        user.setUsername(dto.getUsername());
        user.setRole(dto.getRole());
        UserDTO updated = UserBuilder.toUserDTO(userRepository.save(user));

        publish(id, () -> syncEventPublisher.publishUserUpdated(updated));
        return updated;
    }

    @Transactional
    public void delete(UUID id) {
        userRepository.delete(getUserOrThrow(id));
        publish(id, () -> syncEventPublisher.publishUserDeleted(id));
    }

    private void publish(UUID id, Runnable action) {
        try {
            action.run();
        } catch (AmqpException e) {
            LOGGER.error("Could not publish sync event for user {}: {}", id, e.getMessage());
            throw new SyncFailedException("User");
        }
    }

    private User getUserOrThrow(UUID id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("User with id: " + id));
    }
}
