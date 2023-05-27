package ru.karaban.currency_rate_bot.service;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.karaban.currency_rate_bot.converter.DtoEntityConverter;
import ru.karaban.currency_rate_bot.dto.CurrencyRateOrderDto;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.CurrencyRateOrder;
import ru.karaban.currency_rate_bot.repository.CurrencyRateOrderRepository;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Data
@RequiredArgsConstructor
@Slf4j
public class CurrencyRateOrderService {

    private final CurrencyRateOrderRepository repository;
    private final DtoEntityConverter converter;

    public void createCurrencyRateOrders(List<CurrencyRateOrderDto> currencyRateOrderDtoList){
        List<CurrencyRateOrder> currencyRateOrderList = currencyRateOrderDtoList.stream()
                .map(converter::dtoToEntityCurrencyRateOrder)
                .collect(Collectors.toList());
        repository.saveAll(currencyRateOrderList);
    }

    public List<CurrencyRateOrderDto> getAllOrders(UserDto userDto){
      return repository.findAllByUser(converter.dtoToEntityUser(userDto)).stream()
              .map(converter::entityToDtoCurrencyRateOrder).collect(Collectors.toList());
    }

    public void deleteOrder(Long id) {
        if(repository.findById(id).isPresent()){
            repository.deleteById(id);
        }
        else {
            log.error("Order with id " + id + " not found");
        }
    }

    public void deleteAllOrdersByUser(UserDto userDto) {
        if(!repository.findAllByUser(converter.dtoToEntityUser(userDto)).isEmpty()){
            repository.deleteAllByUser(converter.dtoToEntityUser(userDto));
        }
        else {
            log.error("Order with user_id " + userDto.getId() + " not found");
        }
    }
}
