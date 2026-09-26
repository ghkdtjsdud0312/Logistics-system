package com.logistics.domain.master.domain;

import com.logistics.global.error.BusinessException;
import com.logistics.global.error.ErrorCode;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Getter
@Entity
@Table(name = "zone")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Zone {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    private Warehouse warehouse;

    @Column(nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    @OneToMany(mappedBy = "zone", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("code")
    private List<Location> locations = new ArrayList<>();

    Zone(Warehouse warehouse, String code, String name) {
        this.warehouse = warehouse;
        this.code = code;
        this.name = name;
    }

    public Location getLocation(String locationCode) {
        return locations.stream().filter(l -> l.getCode().equals(locationCode)).findFirst()
                .orElseThrow(() -> new BusinessException(ErrorCode.ENTITY_NOT_FOUND));
    }

    public void rename(String name) {
        this.name = name;
    }

    public boolean removeLocation(Long locationId) {
        return locations.removeIf(l -> l.getId().equals(locationId));
    }

    public Location addLocation(String locationCode) {
        if (locations.stream().anyMatch(l -> l.getCode().equals(locationCode))) {
            throw new BusinessException(ErrorCode.DUPLICATE_CODE);
        }
        Location location = new Location(this, locationCode);
        locations.add(location);
        return location;
    }
}
