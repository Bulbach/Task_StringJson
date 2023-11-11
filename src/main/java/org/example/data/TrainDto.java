package org.example.data;

public record TrainDto(

        String trainNumber,
        String trainIndex,
        LocomotiveDto locomotive,
        TrainLineUpDto trainLineUp
) {
}
