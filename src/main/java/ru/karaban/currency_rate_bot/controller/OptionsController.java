package ru.karaban.currency_rate_bot.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import ru.karaban.currency_rate_bot.dto.OptionsDto;
import ru.karaban.currency_rate_bot.dto.UserDto;
import ru.karaban.currency_rate_bot.service.OptionsService;


@RestController
@RequiredArgsConstructor
@RequestMapping(value = "/options")
public class OptionsController {

    private final OptionsService optionsService;

    @PostMapping()
    public void createOptions(OptionsDto optionsDto){
            optionsService.createOptions(optionsDto);
    }
    @DeleteMapping("/{id}")
    public void deleteOptions(@PathVariable Long id){
        optionsService.deleteOptions(id);
    }

    @GetMapping()
    public void getAllOptions(UserDto userDto){
        optionsService.findOptionByUser(userDto);
    }
}
