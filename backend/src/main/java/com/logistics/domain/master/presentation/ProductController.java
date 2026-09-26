package com.logistics.domain.master.presentation;

import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.presentation.dto.ProductCreateRequest;
import com.logistics.domain.master.presentation.dto.ProductResponse;
import com.logistics.domain.master.presentation.dto.ProductUpdateRequest;
import com.logistics.global.common.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Tag(name = "상품 관리", description = "상품 기준정보 API")
@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ApiResponse<ProductResponse> create(@Valid @RequestBody ProductCreateRequest request) {
        return ApiResponse.success(ProductResponse.from(productService.create(request.toEntity())));
    }

    @GetMapping
    public ApiResponse<List<ProductResponse>> getList(@RequestParam(required = false) String keyword) {
        return ApiResponse.success(productService.search(keyword).stream().map(ProductResponse::from).toList());
    }

    @PutMapping("/{id}")
    public ApiResponse<ProductResponse> update(@PathVariable Long id, @Valid @RequestBody ProductUpdateRequest request) {
        return ApiResponse.success(ProductResponse.from(
                productService.update(id, request.name(), request.unit(), request.unitWeightKg())));
    }

    @DeleteMapping("/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id) {
        productService.delete(id);
        return ApiResponse.success(null);
    }
}
