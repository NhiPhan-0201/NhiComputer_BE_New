package com.mshop.service.impl;

import com.mshop.entity.DiscountCode;
import com.mshop.repository.DiscountCodeRepository;
import com.mshop.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class DiscountCodeServiceImpl implements DiscountCodeService {
    @Autowired
    private DiscountCodeRepository repo;

    @Override
    public Optional<DiscountCode> findByCode(String code) {
        return repo.findByCode(code);
    }

    @Override
    public DiscountCode save(DiscountCode code) {
        return repo.save(code);
    }

    @Override
    public void incrementUsedCount(DiscountCode code) {
        code.setUsedCount(code.getUsedCount() + 1);
        repo.save(code);
    }
}