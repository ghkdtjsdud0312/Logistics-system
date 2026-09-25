package com.logistics.domain.driver;

import com.logistics.domain.driver.application.DriverService;
import com.logistics.domain.driver.domain.Driver;
import com.logistics.domain.driver.domain.DriverStatus;
import com.logistics.global.error.BusinessException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ActiveProfiles("test")
@SpringBootTest
@Transactional
class DriverServiceTest {

    @Autowired
    private DriverService driverService;

    @Test
    @DisplayName("기사를 등록하면 기본 상태는 AVAILABLE이다")
    void create_defaultsToAvailable() {
        Driver driver = driverService.create(Driver.builder().driverCode("D001").name("홍기사").build());

        assertThat(driver.getId()).isNotNull();
        assertThat(driver.getStatus()).isEqualTo(DriverStatus.AVAILABLE);
    }

    @Test
    @DisplayName("기사 상태를 휴무로 변경할 수 있다")
    void changeStatus_toOff() {
        Driver driver = driverService.create(Driver.builder().driverCode("D002").name("강기사").build());

        Driver updated = driverService.changeStatus(driver.getId(), DriverStatus.OFF);

        assertThat(updated.getStatus()).isEqualTo(DriverStatus.OFF);
    }

    @Test
    @DisplayName("같은 기사 ID는 중복 등록할 수 없다")
    void create_duplicateCode() {
        driverService.create(Driver.builder().driverCode("D100").name("김기사").build());

        assertThatThrownBy(() -> driverService.create(Driver.builder().driverCode("D100").name("박기사").build()))
                .isInstanceOf(BusinessException.class);
    }
}
