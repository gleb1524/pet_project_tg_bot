package ru.karaban.currency_rate_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.karaban.currency_rate_bot.entity.Options;
import ru.karaban.currency_rate_bot.entity.User;

import java.util.List;
import java.util.Optional;

public interface OptionsRepository extends JpaRepository<Options, Long> {
    Optional<Options> findByUser(User user);
    List<Options> findAllByUser(User user);
}
