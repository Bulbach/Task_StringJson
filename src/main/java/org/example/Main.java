package org.example;

import org.example.data.TrainDto;
import org.example.entity.Cargo;
import org.example.entity.Locomotive;
import org.example.entity.Train;
import org.example.entity.TrainLineUp;
import org.example.entity.Wagon;
import org.example.mapper.TrainMapperImpl;
import org.example.transformation.Transform;
import org.example.transformation.TransformationString;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class Main {
    public static void main(String[] args) {
        Cargo cargo = Cargo.builder()
                .invoiceNumber("B123456JK")
                .cargoName("sea container")
                .weight(23.5d)
                .transportationCost(BigDecimal.valueOf(1200))
                .build();
        Cargo doubleCargo = Cargo.builder()
                .invoiceNumber("SD563456JK")
                .cargoName("combine harvester")
                .weight(33.1d)
                .transportationCost(BigDecimal.valueOf(2400))
                .build();
        Wagon wagon = Wagon.builder()
                .wagonNumber("91750059")
                .loadCapacity(64)
                .yearOfConstruction(2015)
                .cargo(cargo)
                .build();
        Wagon doubleWagon = Wagon.builder()
                .wagonNumber("24506345")
                .loadCapacity(63)
                .yearOfConstruction(1999)
                .cargo(doubleCargo)
                .build();
        Locomotive locomotive = Locomotive.builder()
                .model("2ТЭ10У")
                .locomotiveNumber("0234")
                .typeLocomotive("Грузовой")
                .documents(new ArrayList<>(Arrays.asList("Книга машиниста", "Справка о мед.осмотре")))
                .build();

        Map<String, Wagon> line = new HashMap<>();
        line.put(wagon.getWagonNumber(), wagon);
        line.put(doubleWagon.getWagonNumber(), doubleWagon);

        TrainLineUp lineUp = TrainLineUp.builder()
                .wagons(line)
                .build();

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm");
        LocalDateTime dateTime = LocalDateTime.parse("2022-12-31T13:00", formatter);
        Train train = Train.builder()
                .trainNumber("9502ПВЭ")
                .trainIndex("1550-287-1500")
                .locomotive(locomotive)
                .trainLineUp(lineUp)
                .departureTime(dateTime)
                .build();

        TrainMapperImpl trainMapper = new TrainMapperImpl();
        TrainDto trainDto = trainMapper.toDto(train);
        System.out.println(trainDto);

        String serialized = TransformationString.serialize(trainDto);
        System.out.println(serialized);

        Transform transform = new Transform(serialized);
        Object object = transform.deserialize(Train.class);
        System.out.println(object);

    }
}