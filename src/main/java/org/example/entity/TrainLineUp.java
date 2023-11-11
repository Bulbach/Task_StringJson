package org.example.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

import java.util.Map;
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class TrainLineUp {
    private Map<String, Wagon> wagons;

}