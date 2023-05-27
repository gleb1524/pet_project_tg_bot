package ru.karaban.currency_rate_bot.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import ru.karaban.currency_rate_bot.entity.Currency;
import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    List<Currency> findAllByIsoCodeIn(List<Long> longs);
}
