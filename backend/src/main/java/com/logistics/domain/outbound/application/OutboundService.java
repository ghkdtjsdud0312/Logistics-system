package com.logistics.domain.outbound.application;

import com.logistics.domain.inbound.application.InboundService;
import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.outbound.domain.Outbound;
import com.logistics.domain.outbound.domain.OutboundRepository;
import com.logistics.domain.outbound.domain.OutboundStatus;
import com.logistics.domain.outbound.presentation.dto.AvailableInboundResponse;
import com.logistics.domain.outbound.presentation.dto.OutboundCreateRequest;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 출고 유스케이스
 * - Inbound 도메인과는 InboundService(Application Service) 호출로만 협력한다 (Repository 직접 참조 금지).
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class OutboundService {

    private final OutboundRepository outboundRepository;
    private final InboundService inboundService;

    @Transactional
    public Outbound createOutbound(OutboundCreateRequest request) {
        Map<Long, Integer> quantities = new LinkedHashMap<>();
        request.items().forEach(item -> {
            validateAvailable(item.inboundId(), item.quantity());
            quantities.merge(item.inboundId(), item.quantity(), Integer::sum);
        });
        return outboundRepository.save(Outbound.create(request.destination(), quantities));
    }

    public Outbound getOutbound(Long id) {
        return outboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.OUTBOUND_NOT_FOUND));
    }

    public List<Outbound> getOutboundList() {
        return outboundRepository.findAll();
    }

    @Transactional
    public Outbound pickOutbound(Long id) {
        Outbound outbound = getOutbound(id);
        outbound.pick();
        return outbound;
    }

    @Transactional
    public Outbound shipOutbound(Long id) {
        Outbound outbound = getOutbound(id);
        outbound.ship();
        return outbound;
    }

    /** 출고 대상 선정: 검수 완료되고 가용 수량이 남은 입고 목록 */
    public List<AvailableInboundResponse> getAvailableInbounds() {
        return inboundService.getCompletedInbounds().stream()
                .map(inbound -> AvailableInboundResponse.of(inbound, availableQuantity(inbound)))
                .filter(response -> response.availableQuantity() > 0)
                .toList();
    }

    private void validateAvailable(Long inboundId, int requestedQuantity) {
        Inbound inbound = inboundService.getInbound(inboundId);
        if (inbound.getStatus() != InboundStatus.COMPLETED) {
            throw new BusinessException(ErrorCode.OUTBOUND_SOURCE_INBOUND_NOT_COMPLETED);
        }
        if (requestedQuantity > availableQuantity(inbound)) {
            throw new BusinessException(ErrorCode.OUTBOUND_QUANTITY_EXCEEDED);
        }
    }

    private int availableQuantity(Inbound inbound) {
        int allocated = outboundRepository
                .findByItemsInboundIdAndStatusNot(inbound.getId(), OutboundStatus.CANCELLED).stream()
                .flatMap(outbound -> outbound.getItems().stream())
                .filter(item -> item.getInboundId().equals(inbound.getId()))
                .mapToInt(item -> item.getQuantity())
                .sum();
        return inbound.getInspectedQuantity() - allocated;
    }
}
