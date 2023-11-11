package org.example.mapper;

import org.example.data.TrainDto;
import org.example.entity.Train;
import org.example.entity.TrainLineUp;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(uses = {WagonMapper.class,LocomotiveMapper.class, TrainLineUp.class})
public interface TrainMapper {

    TrainDto toDto(Train train);
    @Mapping(target = "uuid", ignore = true)
    Train toTrain(TrainDto trainDto);
}
