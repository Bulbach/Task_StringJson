package org.example.mapper;

import org.example.data.TrainLineUpDto;
import org.example.entity.TrainLineUp;
import org.mapstruct.Mapper;

@Mapper(uses = {WagonMapper.class, CargoMapper.class})
public interface TrainLineUpMapper {
    TrainLineUpDto toDto(TrainLineUp trainLineUp);

    TrainLineUp toTrainLineUp(TrainLineUpDto trainLineUpDto);
}
