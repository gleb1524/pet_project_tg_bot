package ru.karaban.currency_rate_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.karaban.currency_rate_bot.entity.CurrencyRateOrder;
import ru.karaban.currency_rate_bot.entity.User;

import java.util.List;
import java.util.Optional;

public interface CurrencyRateOrderRepository extends JpaRepository<CurrencyRateOrder, Long> {

    List<CurrencyRateOrder> findAllByUser(User user);
    void deleteAllByUser(User user);
}
