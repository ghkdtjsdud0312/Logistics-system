package com.logistics.domain.vehicle;

import com.logistics.domain.vehicle.domain.CapacityValidator;
import com.logistics.domain.vehicle.domain.Vehicle;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CapacityValidatorTest {

    private final Vehicle vehicle = Vehicle.builder()
            .vehicleNumber("12가1234").vehicleType("1톤").capacityKg(1000).build();

    @Test
    @DisplayName("적재량과 같은 중량은 통과하고 초과하면 거부한다")
    void fits_boundary() {
        assertThat(CapacityValidator.fits(vehicle, 1000)).isTrue();
        assertThat(CapacityValidator.fits(vehicle, 1000.001)).isFalse();
    }
}
