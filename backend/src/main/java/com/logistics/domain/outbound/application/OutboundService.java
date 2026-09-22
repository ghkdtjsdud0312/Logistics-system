package com.logistics.domain.outbound.application;

import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.domain.OutboundRepository;
import com.logistics.domain.outbound.presentation.dto.OutboundCreateRequest;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundService {

    private final OutboundRepository outboundRepository;

    @Transactional
    public Outbound createOutbound(OutboundCreateRequest request) {
        Outbound outbound = Outbound.builder()
                .itemName(request.itemName())
                .quantity(request.quantity())
                .destination(request.destination())
                .build();
        return outboundRepository.save(outbound);
    }

    public Outbound getOutbound(Long id) {
        return outboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.OUTBOUND_NOT_FOUND));
    }

    public List<Outbound> getOutboundList() {
        return outboundRepository.findAll();
    }

    @Transactional
    public Outbound shipOutbound(Long id) {
        Outbound outbound = getOutbound(id);
        outbound.ship();
        return outbound;
    }
}
