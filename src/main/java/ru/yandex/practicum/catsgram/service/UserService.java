package ru.yandex.practicum.catsgram.service;

import org.springframework.stereotype.Service;
import ru.yandex.practicum.catsgram.exception.ConditionsNotMetException;
import ru.yandex.practicum.catsgram.exception.DuplicatedDataException;
import ru.yandex.practicum.catsgram.model.User;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Service
public class UserService {
    private final Map<Long, User> users = new HashMap<>();

    public Collection<User> getUsers() {
        return users.values();
    }

    public User createUser(User user) {
        if (user.getEmail() == null || user.getEmail().isEmpty()){
            throw new ConditionsNotMetException("Имейл должен быть указан");
        }
        for (User existingUser : users.values()){
            if (existingUser.getEmail().equals(user.getEmail())){
                throw new ConditionsNotMetException("Этот имейл уже используется");
            }
        }

        user.setId(getNextId());
        user.setRegistrationDate(Instant.now());

        users.put(user.getId(), user);
        return user;
    }

    public User updateUser(User newUser) {
        if (newUser.getId() == null){
            throw new ConditionsNotMetException("Id должен быть указан");
        }

        User existingUser = users.get(newUser.getId());
        if (existingUser == null) {
            throw new ConditionsNotMetException("Пользователь не найден");
        }

        if (newUser.getEmail() != null && !newUser.getEmail().equals(existingUser.getEmail())) {
            for (User user : users.values()) {
                if (!user.getId().equals(newUser.getId()) && newUser.getEmail().equals(user.getEmail())) {
                    throw new DuplicatedDataException("Этот имейл уже используется");
                }
            }
        }

        User updatedUser = User.builder()
                .id(existingUser.getId())
                .username(newUser.getUsername() != null ? newUser.getUsername() : existingUser.getUsername())
                .email(newUser.getEmail() != null ? newUser.getEmail() : existingUser.getEmail())
                .password(newUser.getPassword() != null ? newUser.getPassword() : existingUser.getPassword())
                .registrationDate(existingUser.getRegistrationDate())
                .build();

        users.put(updatedUser.getId(), updatedUser);
        return updatedUser;
    }

    public Optional<User> findUserById(Long id) {
        return Optional.ofNullable(users.get(id));
    }

    private long getNextId() {
        long currentMaxId = users.keySet()
                .stream()
                .mapToLong(id -> id)
                .max()
                .orElse(0);
        return ++currentMaxId;
    }
}
