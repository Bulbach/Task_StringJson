package org.example;

import org.example.data.TrainDto;
import org.example.entity.Cargo;
import org.example.entity.Locomotive;
import org.example.entity.Train;
import org.example.entity.Wagon;
import org.example.mapper.TrainMapperImpl;
import org.example.transformation.TransformationJson;
import org.example.transformation.TransformationString;

import java.math.BigDecimal;
import java.util.HashMap;

public class Main {
    public static void main(String[] args) {
        Cargo cargo = Cargo.builder()
                .invoiceNumber("B123456JK")
                .cargoName("container")
                .weight(23.5d)
                .transportationCost(BigDecimal.valueOf(1200))
                .build();
        Wagon wagon = Wagon.builder()
                .wagonNumber("91750059")
                .loadCapacity(64)
                .yearOfConstruction(2015)
                .cargo(cargo)
                .build();
        Locomotive locomotive = Locomotive.builder()
                .model("2ТЭ10У")
                .locomotiveNumber("0234")
                .typeLocomotive("Грузовой")
                .build();

        HashMap<String, Wagon> line = new HashMap<>();
        line.put(wagon.getWagonNumber(),wagon);

        Train train = Train.builder()
                .trainNumber("9502ПВЭ")
                .trainIndex("1550-287-1500")
                .locomotive(locomotive)
                .trainLineup(line)
                .build();

        TrainMapperImpl trainMapper = new TrainMapperImpl();
        TrainDto trainDto = trainMapper.toDto(train);
        String serialized = TransformationString.serialize(trainDto);
        System.out.println(serialized);

        Object deserialized = TransformationJson.deserializeObject(serialized, Train.class);
        System.out.println(deserialized);
    }
}