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

    @PostMapping("/v1/create")
    public ResponseEntity<FinancialModel> create(@RequestBody FinancialDto dto) {

        if (dto.getId() != null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }

        FinancialEntity entity = mapper.toEntity(dto);
        entity = service.save(entity);
        FinancialModel model = mapper.toModel(entity);
        return new ResponseEntity<>(model, HttpStatus.CREATED);
    }

    @PutMapping("/v1/update")
    public ResponseEntity<FinancialModel> update(@RequestBody FinancialDto dto) {
        if (dto.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FinancialEntity entity = mapper.toEntity(dto);
        entity = service.save(entity);
        FinancialModel model = mapper.toModel(entity);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @PutMapping("/v1/partialUpdate")
    public ResponseEntity<FinancialModel> partialUpdate(@RequestBody FinancialDto dto) {
        if (dto.getId() == null) {
            return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
        }
        FinancialEntity entity = service.findById(dto.getId()).get();
        if (dto.getUser() != null) {
            entity.setUser(dto.getUser());
        }
        if (dto.getAmount() != null) {
            entity.setAmount(dto.getAmount());
        }
        if (dto.getStatus() != null) {
            entity.setStatus(dto.getStatus());
        }
        entity = service.save(entity);
        FinancialModel model = mapper.toModel(entity);
        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    @DeleteMapping("/v1/delete")
    public ResponseEntity delete(@RequestBody FinancialDto dto) {
        if (dto.getId() == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        if (!service.findById(dto.getId()).isPresent()) {
            return new ResponseEntity(HttpStatus.NOT_FOUND);
        }
        FinancialEntity entity = mapper.toEntity(dto);
        service.delete(entity);
        return new ResponseEntity(HttpStatus.OK);
    }

    @GetMapping("/v1/findById")
    public ResponseEntity<FinancialModel> findById(String id) {
        if (id == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        FinancialEntity entity = service.findById(id).get();
        return new ResponseEntity<>(mapper.toModel(entity), HttpStatus.OK);
    }

    @GetMapping("/v1/findByUser")
    public ResponseEntity<List<FinancialModel>> findByUser(String user) {
        if (user == null) {
            return new ResponseEntity(HttpStatus.BAD_REQUEST);
        }
        List<FinancialEntity> entities = service.findByUser(user);
        return new ResponseEntity<>(mapper.toModel(entities), HttpStatus.OK);
    }

    @GetMapping("/v1/findAll")
    public ResponseEntity<List<FinancialModel>> findAll() {
        List<FinancialEntity> entities = service.findAll();
        return new ResponseEntity<>(mapper.toModel(entities), HttpStatus.OK);
    }
}
