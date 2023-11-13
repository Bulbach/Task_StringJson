package org.example.data;

import java.util.List;

public record LocomotiveDto(
        String model
        , String locomotiveNumber
        , String typeLocomotive
        , List<String> documents
) {
}
