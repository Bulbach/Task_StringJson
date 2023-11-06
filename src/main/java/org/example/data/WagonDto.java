package org.example.data;

import org.example.entity.Cargo;

public record WagonDto(
        int wagonNumber,
        int loadCapacity,
        int yearOfConstruction,
        Cargo cargo
) {
}
