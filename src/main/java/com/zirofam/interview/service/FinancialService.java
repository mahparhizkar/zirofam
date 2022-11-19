package com.zirofam.interview.service;

import com.zirofam.interview.domain.FinancialEntity;
import com.zirofam.interview.repository.FinancialRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class FinancialService {

    private final FinancialRepository repository;

    public FinancialService(FinancialRepository repository) {
        this.repository = repository;
    }

    public FinancialEntity save(FinancialEntity entity) {
        return repository.save(entity);
    }

    public List<FinancialEntity> findAll() {
        return repository.findAll();
    }

    public void delete(FinancialEntity entity) {
        repository.delete(entity);
    }

    public Optional<FinancialEntity> findById(String id) {
        return repository.findById(id);
    }

    public List<FinancialEntity> findByUser(String user) {
        return repository.findByUser(user);
    }
}
