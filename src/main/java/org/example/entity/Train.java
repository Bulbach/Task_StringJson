package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class Train {

    private UUID uuid;
    private String trainNumber;
    private String trainIndex;
    private Locomotive locomotive;
    private TrainLineUp trainLineUp;
//    Map<String, Wagon> trainLineup;

}
