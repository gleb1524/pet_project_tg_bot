package ru.karaban.currency_rate_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.User;
import ru.karaban.currency_rate_bot.repository.UserRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserService {

    private final UserRepository userRepository;

    public Optional<User> findByUserId(Long id) {
        return userRepository.findById(id);
    }

    public void createUser(UserDto userDto) {
        userRepository.save(User.builder()
                .id(userDto.getId())
                .name(userDto.getName())
                .surname(userDto.getSurname())
                .build());
    }

    public void deleteUser(UserDto userDto){
        if(findByUserId(userDto.getId()).isPresent()){
            userRepository.deleteById(userDto.getId());
        } else {
            log.error("User with id " + userDto.getId() + " not found");
        }
    }
}
