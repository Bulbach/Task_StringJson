package org.example.data;

import java.time.LocalDateTime;

public record TrainDto(

        String trainNumber,
        String trainIndex,
        LocomotiveDto locomotive,
        TrainLineUpDto trainLineUp,
        LocalDateTime departureTime
) {
}
