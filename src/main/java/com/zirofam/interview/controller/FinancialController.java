package com.zirofam.interview.controller;

import com.zirofam.interview.controller.dto.FinancialDto;
import com.zirofam.interview.controller.mapper.FinancialMapper;
import com.zirofam.interview.controller.model.FinancialModel;
import com.zirofam.interview.domain.FinancialEntity;
import com.zirofam.interview.service.FinancialService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class FinancialController {

    private final FinancialMapper mapper;

    private final FinancialService service;

    public FinancialController(FinancialMapper mapper, FinancialService service) {
        this.mapper = mapper;
        this.service = service;
    }

    @PostMapping("/v1/financial")
    public ResponseEntity<FinancialModel> create(@RequestBody FinancialDto dto) {

        if (dto.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        FinancialEntity entity = mapper.toEntity(dto);
        entity = service.save(entity);
        FinancialModel model = mapper.toModel(entity);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @GetMapping("/v1/financial")
    public ResponseEntity<List<FinancialModel>> findAll() {
        List<FinancialEntity> entities = service.findAll();
        return new ResponseEntity<>(mapper.toModel(entities), HttpStatus.CREATED);
    }

    /*
    * TODO: PART 1: add RESTful API
    * create
    * update
    * partial update
    * delete
    * find by id
    * find by user id
    * */


}
