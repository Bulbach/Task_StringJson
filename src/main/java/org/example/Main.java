package org.example;

import org.example.data.TrainDto;
import org.example.data.TrainLineUpDto;
import org.example.data.WagonDto;
import org.example.entity.Cargo;
import org.example.entity.Locomotive;
import org.example.entity.Train;
import org.example.entity.TrainLineUp;
import org.example.entity.Wagon;
import org.example.mapper.TrainLineUpMapper;
import org.example.mapper.TrainLineUpMapperImpl;
import org.example.mapper.TrainMapperImpl;
import org.example.mapper.WagonMapper;
import org.example.mapper.WagonMapperImpl;
import org.example.transformation.Transform;
import org.example.transformation.TransformationString;

import java.math.BigDecimal;
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
        Wagon doubleWagon= Wagon.builder()
                .wagonNumber("24506345")
                .loadCapacity(63)
                .yearOfConstruction(1999)
                .cargo(doubleCargo)
                .build();
        Locomotive locomotive = Locomotive.builder()
                .model("2ТЭ10У")
                .locomotiveNumber("0234")
                .typeLocomotive("Грузовой")
                .build();

        Map<String, Wagon> line = new HashMap<>();
        line.put(wagon.getWagonNumber(), wagon);
        line.put(doubleWagon.getWagonNumber(), doubleWagon);

        TrainLineUp lineUp = TrainLineUp.builder()
                .wagons(line)
                .build();
        TrainLineUpMapper trainLineUpMapper = new TrainLineUpMapperImpl();
        TrainLineUpDto lineUpDto = trainLineUpMapper.toDto(lineUp);

        Train train = Train.builder()
                .trainNumber("9502ПВЭ")
                .trainIndex("1550-287-1500")
                .locomotive(locomotive)
                .trainLineUp(lineUp)
                .build();

        TrainMapperImpl trainMapper = new TrainMapperImpl();
        TrainDto trainDto = trainMapper.toDto(train);
//        System.out.println(trainDto);
        String serialized = TransformationString.serialize(trainDto);
        Transform transform = new Transform(serialized);
//        Train train1 = transform.parse(Train.class);
//        System.out.println(train1);
        Map<String, Object> objectMap = transform.parse();
        objectMap.forEach((key,value)-> System.out.println(key + ":" +value));
//        System.out.println(serialized);

        WagonMapper wagonMapper = new WagonMapperImpl();
        WagonDto wagonDto = wagonMapper.toDto(wagon);
        String wagTest = TransformationString.serialize(wagonDto);
//        System.out.println(wagTest);
//        LocomotiveMapperImpl locomotiveMapper = new LocomotiveMapperImpl();
//        LocomotiveDto locomotiveDto = locomotiveMapper.toDto(locomotive);
//        String likTest = TransformationString.serialize(locomotiveDto);
//        System.out.println(likTest);

//        Object deserialized = TransformationJson.deserializeObject(serialized, Train.class);
//        Object deserialized = TransformationJson.deserializeObject(likTest,Locomotive.class);
//        Object deserialized = TransformationJson.deserializeObject(wagTest, Wagon.class);
//        System.out.println(deserialized);
    }
}