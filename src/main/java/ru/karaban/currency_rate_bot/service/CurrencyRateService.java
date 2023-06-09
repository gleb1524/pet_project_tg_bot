package ru.karaban.currency_rate_bot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.karaban.currency_rate_bot.client.CurrencyRatesClient;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.Currency;
import ru.karaban.currency_rate_bot.entity.CurrencyRate;
import ru.karaban.currency_rate_bot.entity.Options;
import ru.karaban.currency_rate_bot.model.CurrencyResponse;
import ru.karaban.currency_rate_bot.repository.CurrencyRateRepository;
import ru.karaban.currency_rate_bot.repository.CurrencyRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@EnableScheduling
@EnableSpringDataWebSupport
public class CurrencyRateService {

    private final CurrencyRateRepository currencyRateRepository;
    private final CurrencyRepository currencyRepository;
    private final CurrencyRatesClient currencyRatesClient;
    private final OptionsService optionsService;


    Pageable secondPageWithFiveElements = PageRequest.of(1, 5);
    @Transactional
    @Scheduled(fixedDelay = 1000 * 3600 * 6)
    public void updateData() {
        createOrUpdateCurrency();
        createOrUpdateCurrencyRate();
    }

    @Transactional
    public void forceUpdateData() {
        createOrUpdateCurrency();
        createOrUpdateCurrencyRate();
    }

    public void createOrUpdateCurrency() {
        CurrencyResponse response = currencyRatesClient.getCurrenciesRates("");
        List<Currency> currencies = response.getRate().values().stream()
                .map(rate -> Currency.builder()
                        .code(rate.getCode())
                        .isoCode(rate.getIsoCode())
                        .rateToRUB(rate.getRate())
                        .build()).collect(Collectors.toList());
        currencyRepository.saveAll(currencies);
    }

    public void createOrUpdateCurrencyRate() {
        List<CurrencyRate> currencyRates = new ArrayList<>();
        CurrencyResponse response = currencyRatesClient.getCurrenciesRates("");
        List<Currency> currencies = response.getRate().values().stream()
                .map(rate -> Currency.builder()
                        .code(rate.getCode())
                        .isoCode(rate.getIsoCode())
                        .rateToRUB(rate.getRate())
                        .build()).collect(Collectors.toList());
        for (int i = 0; i < currencies.size() - 1; i++) {
            for (int j = 0; j < currencies.size() - 1; j++) {
                if (i != j) {
                    currencyRates.add(CurrencyRate.builder()
                            .id(currencies.get(i).getIsoCode() + "_" + currencies.get(j).getIsoCode())
                            .targetCurrency(currencies.get(i))
                            .sourceCurrency(currencies.get(j))
                            .rate(calculateRate(currencies.get(i), currencies.get(j)))
                            .build());
                }
            }
        }

        currencyRateRepository.saveAll(currencyRates);
    }

    public BigDecimal getDifferentCurrencyRate(String target, String source) {
       Currency targetCurrency = currencyRepository.findByCode(target).orElseThrow();
       Currency sourceCurrency = currencyRepository.findByCode(source).orElseThrow();
       return currencyRateRepository.findById(targetCurrency.getIsoCode() + "_" + sourceCurrency.getIsoCode()).orElseThrow().getRate();
    }

    public Page<Currency> getAllCurrencyRateToRub(UserDto userDto, int pageNumber) {
        List<Options> optionsList = optionsService.findOptionByUser(userDto);
        if (optionsList.isEmpty()) {
            return currencyRepository.findAll(PageRequest.of(pageNumber, 5));
        }
        else {
            return currencyRepository.findAllByIsoCodeIn((optionsList.stream()
                    .map(options -> options.getCurrency().getIsoCode())
                    .collect(Collectors.toList())), secondPageWithFiveElements);
        }
    }

    public BigDecimal getRateToRub(String code) {
        Currency currency = currencyRepository.findByCode(code).orElseThrow();
        return currency.getRateToRUB();
    }

    private BigDecimal calculateRate(Currency target, Currency source) {
        return target.getRateToRUB().divide(source.getRateToRUB(), 4, RoundingMode.HALF_UP);
    }
}
