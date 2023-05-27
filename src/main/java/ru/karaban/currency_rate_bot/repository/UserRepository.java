package ru.karaban.currency_rate_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.karaban.currency_rate_bot.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {
}
