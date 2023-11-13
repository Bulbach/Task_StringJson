package org.example.data;

import java.math.BigDecimal;

public record CargoDto(
        String invoiceNumber,
        String cargoName,
        Double weight,
        BigDecimal transportationCost) {
}
