package com.myproject.currencylog.integration;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.myproject.currencylog.models.dto.CbrDailyResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

@Component
public class CbrClient {

    private final RestTemplate rest;
    private final XmlMapper xml;
    private final String url;

    public CbrClient(RestTemplate restTemplate, @Value("${currency.cbr.url:https://www.cbr-xml-daily.ru/daily_utf8.xml}") String cbrUrl) {
        this.rest = restTemplate;
        this.xml = new XmlMapper();
        this.url = cbrUrl;
    }

    public CbrDailyResponse fetchDaily() {
        try {
            String xmlStr = rest.getForObject(url, String.class);
            if (xmlStr == null || xmlStr.isBlank()) {
                throw new IllegalStateException("Пустой ответ от ЦБР");
            }
            return xml.readValue(xmlStr, CbrDailyResponse.class);
        } catch (RestClientException e) {
            throw new RuntimeException("Ошибка при вызове ЦБР: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Ошибка парсинга XML от ЦБР", e);
        }
    }
}