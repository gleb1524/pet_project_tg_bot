package ru.karaban.currency_rate_bot.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.karaban.currency_rate_bot.converter.DtoEntityConverter;
import ru.karaban.currency_rate_bot.dto.OptionsDto;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.entity.Options;
import ru.karaban.currency_rate_bot.repository.OptionsRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class OptionsService {

    private final OptionsRepository optionsRepository;
    private final DtoEntityConverter converter;

    public List<Options> findOptionByUser(UserDto userDto) {
        return optionsRepository.findAllByUser(converter.dtoToEntityUser(userDto));
    }

    public void createOptions(OptionsDto optionsDto) {
        optionsRepository.save(converter.dtoToEntityOptions(optionsDto));
    }

    public void createAllOptions(List<OptionsDto> optionsDtoList) {
        List<Options> optionsList = optionsDtoList.stream()
                .map(converter::dtoToEntityOptions)
                .collect(Collectors.toList());
        optionsRepository.saveAll(optionsList);
    }

    public void deleteOptions(Long id) {
        optionsRepository.deleteById(id);
    }
//
//    private User userDtoToUser(UserDto userDto) {
//        return User.builder()
//                .id(userDto.getId())
//                .name(userDto.getName())
//                .surname(userDto.getSurname())
//                .build();
//    }
//
//    private Currency currencyDtoToCurrency(CurrencyDto currencyDto) {
//        return Currency.builder()
//                .isoCode(currencyDto.getIsoCode())
//                .code(currencyDto.getCode())
//                .rateToRUB(currencyDto.getRateToRUB())
//                .build();
//    }
//
//    private Options optionsDtoToOptions(OptionsDto optionsDto) {
//        return Options.builder()
//                .currency(currencyDtoToCurrency(optionsDto.getCurrencyDto()))
//                .user(userDtoToUser(optionsDto.getUserDto()))
//                .build();
//    }
}
