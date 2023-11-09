package org.example.data;

public record WagonDto(
        int wagonNumber,
        int loadCapacity,
        int yearOfConstruction,
        CargoDto cargo
) {
}
