package com.myproject.currencylog.scheduler;

import com.myproject.currencylog.services.CurrencySyncService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class CurrencySyncScheduler {
    private static final Logger log = LoggerFactory.getLogger(CurrencySyncScheduler.class);
    private final CurrencySyncService currencySyncService;

    public CurrencySyncScheduler(CurrencySyncService currencySyncService) {
        this.currencySyncService = currencySyncService;
    }

    @Scheduled(fixedRateString = "${currency.sync.rate:3600000}")
    public void syncCurrencyTask() {
        try {
            log.info("Запуск синхронизации курсов валют");
            currencySyncService.syncCurrency();
            log.info("Синхронизация курсов валют завершена успешно");
        } catch (Exception e) {
            log.error("Ошибка при выполнении синхронизации курсов валют", e);
        }
    }
}