package com.logistics.domain.inbound.application;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundRepository;
import com.logistics.domain.inbound.presentation.dto.InboundCreateRequest;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 입고 유스케이스
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class InboundService {

    private final InboundRepository inboundRepository;

    @Transactional
    public Inbound createInbound(InboundCreateRequest request) {
        Inbound inbound = Inbound.builder()
                .itemName(request.itemName())
                .quantity(request.quantity())
                .warehouseLocation(request.warehouseLocation())
                .build();
        return inboundRepository.save(inbound);
    }

    public Inbound getInbound(Long id) {
        return inboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INBOUND_NOT_FOUND));
    }

    public List<Inbound> getInboundList() {
        return inboundRepository.findAll();
    }

    @Transactional
    public Inbound completeInbound(Long id) {
        Inbound inbound = getInbound(id);
        inbound.complete();
        return inbound;
    }
}
