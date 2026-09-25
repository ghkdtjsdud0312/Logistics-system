package com.logistics.domain.loading.application;

import com.logistics.domain.loading.domain.Shipment;
import com.logistics.domain.loading.domain.ShipmentRepository;
import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;

/** 배차·배송 도메인이 Shipment를 조회할 때 쓰는 서비스 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ShipmentService {

    private final ShipmentRepository shipmentRepository;

    public Shipment get(Long id) {
        return shipmentRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND));
    }

    /** 요청한 ID가 모두 존재해야 한다. 배정 동시성을 위해 행을 잠근다. */
    @Transactional
    public List<Shipment> getAllForUpdate(Collection<Long> ids) {
        List<Shipment> shipments = shipmentRepository.findAllByIdForUpdate(ids);
        if (shipments.size() != ids.stream().distinct().count()) {
            throw new BusinessException(ErrorCode.SHIPMENT_NOT_FOUND);
        }
        return shipments;
    }

    public List<Shipment> getByOrderIds(Collection<Long> orderIds) {
        return shipmentRepository.findAllByOrderIds(orderIds);
    }

    public List<Shipment> getByDispatchId(Long dispatchId) {
        return shipmentRepository.findAllByDispatchId(dispatchId);
    }
}
