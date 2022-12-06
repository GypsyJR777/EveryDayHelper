package com.github.gypsyjr777.service;

import com.github.gypsyjr777.config.CurrencyConfig;
import com.github.gypsyjr777.model.currency.Currency;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class CurrencyService {
    private CurrencyConfig currencyConfig;

    @Autowired
    public CurrencyService(CurrencyConfig currencyConfig) {
        this.currencyConfig = currencyConfig;
    }

    public Currency getCurrency(String from, String to) {
        String query = "?api_key=" + currencyConfig.getKey() + "&from=" + from + "&to=" + to;
        RestTemplate restTemplate = new RestTemplate();

        return restTemplate.getForObject(currencyConfig.getUrl() + query, Currency.class);
    }
}
