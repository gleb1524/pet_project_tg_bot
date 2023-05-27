package ru.karaban.currency_rate_bot.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@AllArgsConstructor
@Builder
public class UserDto {

    private Long id;
    private String name;
    private String surname;
}
