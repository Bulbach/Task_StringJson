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
public class Wagon {

    private UUID uuid;
    private int wagonNumber;
    private int loadCapacity;
    private int yearOfConstruction;
    private Cargo cargo;
}
