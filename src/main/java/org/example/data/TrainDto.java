package org.example.data;

import org.example.entity.Cargo;
import org.example.entity.Locomotive;
import org.example.entity.Wagon;

import java.util.Map;

public record TrainDto(

        String trainNumber,
        String trainIndex,
        Locomotive locomotive,
        Map<Wagon, Cargo> trainLineup
) {
}
