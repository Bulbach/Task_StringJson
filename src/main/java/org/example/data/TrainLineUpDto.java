package org.example.data;

import java.util.Map;

public record TrainLineUpDto(Map<String, WagonDto> wagons) {
}
