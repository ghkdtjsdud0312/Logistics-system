package com.logistics.domain.inbound.application;

import com.logistics.domain.inbound.domain.Inbound;
import com.logistics.domain.inbound.domain.InboundRepository;
import com.logistics.domain.inbound.domain.InboundStatus;
import com.logistics.domain.inventory.application.StockService;
import com.logistics.domain.master.application.LocationQueryService;
import com.logistics.domain.master.application.ProductService;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import com.logistics.global.event.StatusChangedEventPublisher;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;

import static com.logistics.domain.inbound.domain.InboundStatus.*;

/** 입고 예정 → 입고완료 → 적치대기 → 적치완료. 적치 시 재고가 늘어난다. */
@Service
@RequiredArgsConstructor
@Transactional
public class InboundService {

    private static final String TARGET = "INBOUND";

    private final InboundRepository inboundRepository;
    private final ProductService productService;
    private final LocationQueryService locationQueryService;
    private final StockService stockService;
    private final StatusChangedEventPublisher eventPublisher;

    public Inbound create(String partnerName, Long productId, int quantity, LocalDate inboundDate) {
        productService.getProduct(productId);
        Inbound inbound = inboundRepository.save(new Inbound(partnerName, productId, quantity, inboundDate));
        inbound.assignNo();
        publish(inbound, "CREATE", null, EXPECTED);
        return inbound;
    }

    public Inbound receive(Long id) {
        Inbound inbound = get(id);
        inbound.receive();
        publish(inbound, "RECEIVE", EXPECTED, RECEIVED);
        inbound.readyForPutaway();
        publish(inbound, "READY_FOR_PUTAWAY", RECEIVED, PUTAWAY_WAITING);
        return inbound;
    }

    public Inbound putaway(Long id, Long locationId) {
        Inbound inbound = get(id);
        locationQueryService.getLocationInfo(locationId);
        inbound.putaway(locationId);
        stockService.increase(inbound.getProductId(), locationId, inbound.getQuantity());
        publish(inbound, "PUTAWAY", PUTAWAY_WAITING, PUTAWAY_DONE);
        return inbound;
    }

    @Transactional(readOnly = true)
    public Inbound get(Long id) {
        return inboundRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.INBOUND_NOT_FOUND));
    }

    private void publish(Inbound inbound, String action, InboundStatus from, InboundStatus to) {
        eventPublisher.publish(TARGET, inbound.getId(), inbound.getInboundNo(), null, action,
                from == null ? null : from.name(), to.name());
    }
}
