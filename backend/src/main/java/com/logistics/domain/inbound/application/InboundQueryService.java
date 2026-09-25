package com.logistics.domain.inbound.application;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundRepository;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.inbound.presentation.dto.InboundResponse;
import com.logistics.domain.master.application.ProductService;
import com.logistics.domain.master.domain.Product;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/** 입고 목록/상세 조회 (상품 이름은 master 서비스로 조회) */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundQueryService {

    private final InboundRepository inboundRepository;
    private final ProductService productService;

    public List<InboundResponse> getList(InboundStatus status) {
        List<Inbound> inbounds = inboundRepository.findAllByStatus(status);
        Map<Long, Product> products = productService.getProductMap(
                inbounds.stream().map(Inbound::getProductId).collect(Collectors.toSet()));
        return inbounds.stream().map(i -> InboundResponse.of(i, products.get(i.getProductId()))).toList();
    }

    public InboundResponse toResponse(Inbound inbound) {
        return InboundResponse.of(inbound, productService.getProduct(inbound.getProductId()));
    }
}
