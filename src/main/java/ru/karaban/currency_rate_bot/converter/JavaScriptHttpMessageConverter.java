package ru.karaban.currency_rate_bot.converter;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.AbstractJackson2HttpMessageConverter;
import org.springframework.stereotype.Component;

@Component
public class JavaScriptHttpMessageConverter extends AbstractJackson2HttpMessageConverter {

    protected JavaScriptHttpMessageConverter(ObjectMapper objectMapper) {
        super(objectMapper ,new MediaType("application","javascript"));
    }
}
