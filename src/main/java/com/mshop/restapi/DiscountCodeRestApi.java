package com.mshop.restapi;

import com.mshop.entity.DiscountCode;
import com.mshop.repository.DiscountCodeRepository;
import com.mshop.service.DiscountCodeService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/discount-codes")
@CrossOrigin(origins = {"http://localhost:4200", "http://localhost:4201"})
public class DiscountCodeRestApi {
    @Autowired
    private DiscountCodeService service;

    @Autowired
    private DiscountCodeRepository repo;

    @GetMapping
    public List<DiscountCode> getAll() {
        return repo.findAll();
    }

    // Kiểm tra mã giảm giá
    @GetMapping("/check/{code}")
    public DiscountCode checkCode(@PathVariable String code, @RequestParam Double orderTotal) {
        Optional<DiscountCode> opt = service.findByCode(code);
        if (opt.isPresent()) {
            DiscountCode dc = opt.get();
            Date now = new Date();
            boolean valid = dc.getStatus() != null && dc.getStatus()
                    && (dc.getStartDate() == null || !now.before(dc.getStartDate()))
                    && (dc.getEndDate() == null || !now.after(dc.getEndDate()))
                    && (dc.getMaxUses() == null || dc.getUsedCount() < dc.getMaxUses())
                    && (orderTotal == null || orderTotal >= dc.getMinOrderValue());
            if (valid) {
                return dc;
            }
        }
        return null;
    }

    // Thêm mã giảm giá (admin)
    @PostMapping
    public DiscountCode create(@RequestBody DiscountCode code) {
        return service.save(code);
    }

    // Sửa mã giảm giá (admin)
    @PutMapping("/{id}")
    public DiscountCode update(@PathVariable Long id, @RequestBody DiscountCode code) {
        code.setId(id);
        return service.save(code);
    }

    // Xóa mã giảm giá (admin)
    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        repo.deleteById(id);
    }

    // Lấy danh sách mã giảm giá còn hiệu lực
    @GetMapping("/available")
    public List<DiscountCode> getAvailableCodes(@RequestParam(required = false) Double orderTotal) {
        Date now = new Date();
        List<DiscountCode> all = repo.findAll();
        for (DiscountCode dc : all) {
            if (dc.getStatus() == null || !dc.getStatus()) {
                System.out.println("Loại bởi status: " + dc.getCode());
                continue;
            }
            if (dc.getStartDate() != null && now.before(dc.getStartDate())) {
                System.out.println("Loại bởi startDate: " + dc.getCode());
                continue;
            }
            if (dc.getEndDate() != null && now.after(dc.getEndDate())) {
                System.out.println("Loại bởi endDate: " + dc.getCode());
                continue;
            }
            if (dc.getMaxUses() != null && dc.getUsedCount() >= dc.getMaxUses()) {
                System.out.println("Loại bởi maxUses: " + dc.getCode());
                continue;
            }
            if (orderTotal != null && orderTotal < dc.getMinOrderValue()) {
                System.out.println("Loại bởi minOrderValue: " + dc.getCode());
                continue;
            }
            System.out.println("Mã hợp lệ: " + dc.getCode());
        }
        // Filter như cũ
        List<DiscountCode> filtered = all.stream()
                .filter(dc -> dc.getStatus() != null && dc.getStatus()
                        && (dc.getStartDate() == null || !now.before(dc.getStartDate()))
                        && (dc.getEndDate() == null || !now.after(dc.getEndDate()))
                        && (dc.getMaxUses() == null || dc.getUsedCount() < dc.getMaxUses())
                        && (orderTotal == null || orderTotal >= dc.getMinOrderValue()))
                .collect(Collectors.toList());
        return filtered;
    }
    @GetMapping("/public")
    public List<DiscountCode> getPublicCodes() {
        Date now = new Date();
        return repo.findAll().stream()
                .filter(dc -> dc.getStatus() != null && dc.getStatus()
                        && (dc.getStartDate() == null || !now.before(dc.getStartDate()))
                        && (dc.getEndDate() == null || !now.after(dc.getEndDate()))
                        && (dc.getMaxUses() == null || dc.getUsedCount() < dc.getMaxUses()))
                .collect(Collectors.toList());
    }
}