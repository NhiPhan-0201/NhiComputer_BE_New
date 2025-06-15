package com.mshop.service;

import com.mshop.entity.DiscountCode;
import java.util.Optional;

public interface DiscountCodeService {
    Optional<DiscountCode> findByCode(String code);
    DiscountCode save(DiscountCode code);

    // Thêm phương thức này
    void incrementUsedCount(DiscountCode code);
}