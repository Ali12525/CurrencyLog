package com.myproject.currencylog.controller;

import com.myproject.currencylog.services.CurrencySyncService;
import io.swagger.v3.oas.annotations.Operation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/currencies")
public class CurrencyController {

    private final CurrencySyncService syncService;

    public CurrencyController(CurrencySyncService syncService) {
        this.syncService = syncService;
    }

    @Operation(summary = "Принудительно синхронизировать курсы сейчас")
    @PostMapping("/sync")
    public ResponseEntity<Void> syncNow() {
        syncService.syncHourly();
        return ResponseEntity.accepted().build();
    }
}