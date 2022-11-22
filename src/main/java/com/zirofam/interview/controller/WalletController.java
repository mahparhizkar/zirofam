package com.zirofam.interview.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.zirofam.interview.controller.dto.FinancialDto;
import com.zirofam.interview.controller.mapper.FinancialMapper;
import com.zirofam.interview.controller.model.FinancialModel;
import com.zirofam.interview.domain.FinancialEntity;
import com.zirofam.interview.domain.WalletEntity;
import com.zirofam.interview.service.FinancialService;
import com.zirofam.interview.service.WalletService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/wallet")
public class WalletController {

    private final FinancialMapper mapper;

    private final FinancialService financialService;

    private final WalletService walletService;

    public WalletController(FinancialMapper mapper, FinancialService financialService, WalletService walletService) {
        this.mapper = mapper;
        this.financialService = financialService;
        this.walletService = walletService;
    }

    @PostMapping("/v1/create")
    public ResponseEntity<FinancialModel> create(@RequestBody FinancialDto dto) throws JsonProcessingException {

        if (dto.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        //Create wallet for user if no exist a wallet
        if (walletService.findByUser(dto.getUser()) == null) {
            walletService.createWallet(dto.getUser());
        }

        //Save request in financial table
        FinancialEntity entity = mapper.toEntity(dto);
        entity = financialService.save(entity);
        FinancialModel model = mapper.toModel(entity);

        //Send update wallet request to kafka topic
        walletService.updateWallet(model);

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @GetMapping("/v1/findBalanceByUser")
    public ResponseEntity<BigDecimal> findBalanceByUser(String user) {
        if (user == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        WalletEntity wallet = walletService.findByUser(user);
        return new ResponseEntity<>(wallet.getBalance(), HttpStatus.OK);
    }
}
