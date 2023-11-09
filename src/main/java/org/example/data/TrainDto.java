package org.example.data;

import java.util.Map;

public record TrainDto(

        String trainNumber,
        String trainIndex,
        LocomotiveDto locomotive,
        Map<String, WagonDto> trainLineup
) {
}
