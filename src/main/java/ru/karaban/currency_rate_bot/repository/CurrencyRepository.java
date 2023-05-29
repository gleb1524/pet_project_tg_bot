package ru.karaban.currency_rate_bot.repository;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import ru.karaban.currency_rate_bot.entity.Currency;

import java.util.List;

public interface CurrencyRepository extends JpaRepository<Currency, Long> {

    Page<Currency> findAllByIsoCodeIn(List<Long> longs, Pageable page);

}
